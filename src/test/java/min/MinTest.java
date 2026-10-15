package min;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.time.format.DateTimeParseException;
import java.util.List;

import org.junit.jupiter.api.Test;

import min.command.Command;
import min.command.Parser;
import min.exception.MinException;
import min.list.NoteList;
import min.list.TaskList;
import min.note.Note;
import min.storage.NoteStorage;
import min.storage.TaskStorage;
import min.task.Task;
import min.task.Todo;
import min.ui.Messages;

class MinTest {
    private final Parser parser = new Parser();
    private final RecordingTaskStorage storage = new RecordingTaskStorage();
    private final RecordingNoteStorage noteStorage = new RecordingNoteStorage();

    @Test
    void constructor_invalidTaskRecord_throwsMinExceptionNamingTaskFile() {
        TaskStorage taskStorage = new FailingTaskStorage(
                new IllegalArgumentException("Invalid task status."));

        MinException exception = assertThrows(MinException.class,
                () -> new Min(taskStorage, new LoadingNoteStorage(List.of())));

        assertEquals(Messages.formatCorruptedTaskData("Invalid task status."),
                exception.getMessage());
    }

    @Test
    void constructor_invalidDeadlineDate_throwsSpecificMinException() {
        TaskStorage taskStorage = new FailingTaskStorage(
                new DateTimeParseException("Invalid date", "2026-02-30", 0));

        MinException exception = assertThrows(MinException.class,
                () -> new Min(taskStorage, new LoadingNoteStorage(List.of())));

        assertEquals(Messages.INVALID_SAVED_DEADLINE_DATE, exception.getMessage());
    }

    @Test
    void constructor_invalidNoteRecord_throwsMinExceptionNamingNoteFile() {
        NoteStorage noteStorage = new FailingNoteStorage(
                new IllegalArgumentException("Note text cannot be blank."));

        MinException exception = assertThrows(MinException.class,
                () -> new Min(new LoadingTaskStorage(List.of()), noteStorage));

        assertEquals(Messages.formatCorruptedNoteData("Note text cannot be blank."),
                exception.getMessage());
    }

    @Test
    void constructor_validStorage_loadsTasksAndNotes() throws IOException, MinException {
        TaskStorage taskStorage = new LoadingTaskStorage(List.of(new Todo("read book")));
        NoteStorage noteStorage = new LoadingNoteStorage(List.of(new Note("watch Dune")));

        Min min = new Min(taskStorage, noteStorage);

        assertEquals(Messages.TASK_LIST_HEADING + "\n"
                + " 1.[T][ ] read book\n"
                + "\n"
                + Messages.NOTE_LIST_HEADING + "\n"
                + " 1.watch Dune", min.getResponse("list"));
    }

    @Test
    void isExitCommand_byeWithSurroundingWhitespace_returnsTrue() {
        Min min = createMin(new TaskList(List.of()));

        assertTrue(min.isExitCommand("  bye  "));
    }

    @Test
    void isExitCommand_nonByeInputs_returnsFalse() {
        Min min = createMin(new TaskList(List.of()));

        assertFalse(min.isExitCommand("byebye"));
        assertFalse(min.isExitCommand("list"));
        assertFalse(min.isExitCommand("dance"));
    }

    @Test
    void getResponse_bye_returnsGoodbyeMessage() {
        Min min = createMin(new TaskList(List.of()));

        assertEquals(Messages.GOODBYE, min.getResponse("bye"));
    }

    @Test
    void getResponse_help_returnsHelpMessage() {
        Min min = createMin(new TaskList(List.of()));

        assertEquals(Messages.HELP, min.getResponse("help"));
    }

    @Test
    void help_everyCommandWord_isDocumented() {
        for (Command command : Command.values()) {
            assertTrue(Messages.HELP.contains(command.getWord()),
                    "Help text does not mention the " + command.getWord() + " command.");
        }
    }

    @Test
    void getResponse_todo_addsTaskAndReturnsConfirmation() {
        TaskList tasks = new TaskList(List.of());
        Min min = createMin(tasks);

        String response = min.getResponse("todo read book");

        assertEquals(Messages.formatAddedTask("[T][ ] read book", 1), response);
        assertEquals("T | 0 | read book", tasks.getTasks().get(0).toFileString());
        assertEquals(1, storage.getSaveCount());
    }

    @Test
    void getResponse_todoSaveFails_returnsDataSaveError() {
        Min min = new Min(parser, new TaskList(List.of()), new SaveFailingTaskStorage(),
                new NoteList(List.of()), noteStorage);

        String response = min.getResponse("todo read book");

        assertEquals(Messages.SAVE_DATA_ERROR, response);
    }

    @Test
    void getResponse_deadline_addsTaskAndReturnsConfirmation() {
        TaskList tasks = new TaskList(List.of());
        Min min = createMin(tasks);

        String response = min.getResponse("deadline submit report /by 2026-08-28");

        assertEquals(Messages.formatAddedTask("[D][ ] submit report (by: Aug 28 2026)", 1), response);
        assertEquals("D | 0 | submit report | 2026-08-28",
                tasks.getTasks().get(0).toFileString());
        assertEquals(1, storage.getSaveCount());
    }

    @Test
    void getResponse_event_addsTaskAndReturnsConfirmation() {
        TaskList tasks = new TaskList(List.of());
        Min min = createMin(tasks);

        String response = min.getResponse("event meeting /from 2pm /to 4pm");

        assertEquals(Messages.formatAddedTask("[E][ ] meeting (from: 2pm to: 4pm)", 1), response);
        assertEquals("E | 0 | meeting | 2pm | 4pm",
                tasks.getTasks().get(0).toFileString());
        assertEquals(1, storage.getSaveCount());
    }

    @Test
    void getResponse_mark_marksTaskAndReturnsConfirmation() {
        Todo task = new Todo("read book");
        Min min = createMin(new TaskList(List.of(task)));

        String response = min.getResponse("mark 1");

        assertEquals(Messages.formatMarkedTask("[T][X] read book"), response);
        assertTrue(task.isDone());
        assertEquals(1, storage.getSaveCount());
    }

    @Test
    void getResponse_unmark_unmarksTaskAndReturnsConfirmation() {
        Todo task = new Todo("read book");
        task.markAsDone();
        Min min = createMin(new TaskList(List.of(task)));

        String response = min.getResponse("unmark 1");

        assertEquals(Messages.formatUnmarkedTask("[T][ ] read book"), response);
        assertFalse(task.isDone());
        assertEquals(1, storage.getSaveCount());
    }

    @Test
    void getResponse_delete_removesTaskAndReturnsConfirmation() {
        Todo deletedTask = new Todo("read book");
        Todo remainingTask = new Todo("buy milk");
        TaskList tasks = new TaskList(List.of(deletedTask, remainingTask));
        Min min = createMin(tasks);

        String response = min.getResponse("delete 1");

        assertEquals(Messages.formatRemovedTask("[T][ ] read book", 1), response);
        assertEquals(List.of(remainingTask), tasks.getTasks());
        assertEquals(1, storage.getSaveCount());
    }

    @Test
    void getResponse_list_returnsNumberedTasksAndNotes() {
        TaskList tasks = new TaskList(List.of(
                new Todo("read book"), new Todo("buy milk")));
        NoteList notes = new NoteList(List.of(new Note("watch Dune")));
        Min min = createMin(tasks, notes);

        String response = min.getResponse("list");

        assertEquals(Messages.TASK_LIST_HEADING + "\n"
                + " 1.[T][ ] read book\n"
                + " 2.[T][ ] buy milk\n"
                + "\n"
                + Messages.NOTE_LIST_HEADING + "\n"
                + " 1.watch Dune", response);
        assertEquals(0, storage.getSaveCount());
    }

    @Test
    void getResponse_list_emptyLists_reportsBothListsEmpty() {
        Min min = createMin(new TaskList(List.of()));

        assertEquals(Messages.EMPTY_TASK_LIST + "\n"
                + "\n"
                + Messages.EMPTY_NOTE_LIST, min.getResponse("list"));
    }

    @Test
    void getResponse_listtasks_returnsNumberedTasksOnly() {
        TaskList tasks = new TaskList(List.of(
                new Todo("read book"), new Todo("buy milk")));
        NoteList notes = new NoteList(List.of(new Note("watch Dune")));
        Min min = createMin(tasks, notes);

        String response = min.getResponse("listtasks");

        assertEquals(Messages.TASK_LIST_HEADING + "\n"
                + " 1.[T][ ] read book\n"
                + " 2.[T][ ] buy milk", response);
        assertEquals(0, storage.getSaveCount());
    }

    @Test
    void getResponse_find_returnsNumberedTaskAndNoteMatches() {
        TaskList tasks = new TaskList(List.of(
                new Todo("read book"), new Todo("buy milk")));
        NoteList notes = new NoteList(List.of(
                new Note("read the CS2103T guide"), new Note("watch Dune")));
        Min min = createMin(tasks, notes);

        String response = min.getResponse("find read");

        assertEquals(Messages.MATCHING_TASK_HEADING + "\n"
                + " 1.[T][ ] read book\n"
                + "\n"
                + Messages.MATCHING_NOTE_HEADING + "\n"
                + " 1.read the CS2103T guide", response);
        assertEquals(0, storage.getSaveCount());
        assertEquals(0, noteStorage.getSaveCount());
    }

    @Test
    void getResponse_note_addsNoteAndReturnsConfirmation() {
        NoteList notes = new NoteList(List.of());
        Min min = createMin(new TaskList(List.of()), notes);

        String response = min.getResponse("note watch Dune");

        assertEquals(Messages.formatAddedNote("watch Dune", 1), response);
        assertEquals("N | watch Dune", notes.getNotes().get(0).toFileString());
        assertEquals(1, noteStorage.getSaveCount());
        assertEquals(0, storage.getSaveCount());
    }

    @Test
    void getResponse_noteSaveFails_returnsDataSaveError() {
        Min min = new Min(parser, new TaskList(List.of()), storage,
                new NoteList(List.of()), new SaveFailingNoteStorage());

        String response = min.getResponse("note watch Dune");

        assertEquals(Messages.SAVE_DATA_ERROR, response);
    }

    @Test
    void getResponse_noteWithoutText_returnsError() {
        Min min = createMin(new TaskList(List.of()));

        assertEquals(Messages.INVALID_NOTE, min.getResponse("note"));
        assertEquals(0, noteStorage.getSaveCount());
    }

    @Test
    void getResponse_noteContainingFieldSeparator_returnsError() {
        Min min = createMin(new TaskList(List.of()));

        assertEquals(Messages.INVALID_NOTE_TEXT,
                min.getResponse("note watch Dune | part two"));
        assertEquals(0, noteStorage.getSaveCount());
    }

    @Test
    void getResponse_listnotes_returnsNumberedNotes() {
        NoteList notes = new NoteList(List.of(
                new Note("watch Dune"), new Note("read the CS2103T guide")));
        Min min = createMin(new TaskList(List.of()), notes);

        String response = min.getResponse("listnotes");

        assertEquals(Messages.NOTE_LIST_HEADING + "\n"
                + " 1.watch Dune\n"
                + " 2.read the CS2103T guide", response);
        assertEquals(0, noteStorage.getSaveCount());
    }

    @Test
    void getResponse_listnotes_emptyList_reportsEmptyList() {
        Min min = createMin(new TaskList(List.of()));

        assertEquals(Messages.EMPTY_NOTE_LIST, min.getResponse("listnotes"));
    }

    @Test
    void getResponse_listtasks_emptyList_reportsEmptyList() {
        Min min = createMin(new TaskList(List.of()));

        assertEquals(Messages.EMPTY_TASK_LIST, min.getResponse("listtasks"));
    }

    @Test
    void getResponse_find_noMatches_reportsBothListsEmpty() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));
        NoteList notes = new NoteList(List.of(new Note("watch Dune")));
        Min min = createMin(tasks, notes);

        assertEquals(Messages.EMPTY_MATCHING_TASKS + "\n"
                + "\n"
                + Messages.EMPTY_MATCHING_NOTES, min.getResponse("find spreadsheet"));
    }

    @Test
    void getResponse_deletenote_removesNoteAndReturnsConfirmation() {
        Note deletedNote = new Note("watch Dune");
        Note remainingNote = new Note("read the CS2103T guide");
        NoteList notes = new NoteList(List.of(deletedNote, remainingNote));
        Min min = createMin(new TaskList(List.of()), notes);

        String response = min.getResponse("deletenote 1");

        assertEquals(Messages.formatRemovedNote("watch Dune", 1), response);
        assertEquals(List.of(remainingNote), notes.getNotes());
        assertEquals(1, noteStorage.getSaveCount());
    }

    @Test
    void getResponse_findThenDeletenote_removesDisplayedMatch() {
        Note nonMatch = new Note("watch Dune");
        Note match = new Note("read the CS2103T guide");
        NoteList notes = new NoteList(List.of(nonMatch, match));
        Min min = createMin(new TaskList(List.of()), notes);

        min.getResponse("find read");
        min.getResponse("deletenote 1");

        assertEquals(List.of(nonMatch), notes.getNotes());
    }

    @Test
    void getResponse_deletenoteOutsideFindResults_returnsRangeError() {
        NoteList notes = new NoteList(List.of(
                new Note("watch Dune"), new Note("read the CS2103T guide")));
        Min min = createMin(new TaskList(List.of()), notes);
        min.getResponse("find read");

        String response = min.getResponse("deletenote 2");

        assertEquals(Messages.formatIndexOutOfRange("note", 1), response);
        assertEquals(2, notes.size());
    }

    @Test
    void getResponse_deletenoteWithEmptyNoteList_returnsError() {
        Min min = createMin(new TaskList(List.of()));

        assertEquals(Messages.formatNoItems("note", "delete"), min.getResponse("deletenote 1"));
    }

    @Test
    void getResponse_findThenMark_marksSecondDisplayedMatch() {
        Todo firstMatch = new Todo("read book");
        Todo nonMatch = new Todo("buy milk");
        Todo secondMatch = new Todo("reread notes");
        TaskList tasks = new TaskList(List.of(firstMatch, nonMatch, secondMatch));
        Min min = createMin(tasks);

        min.getResponse("find read");
        min.getResponse("mark 2");

        assertFalse(nonMatch.isDone());
        assertTrue(secondMatch.isDone());
    }

    @Test
    void getResponse_indexOutsideFindResults_returnsRangeError() {
        Todo firstMatch = new Todo("read book");
        Todo nonMatch = new Todo("buy milk");
        Todo secondMatch = new Todo("reread notes");
        TaskList tasks = new TaskList(List.of(firstMatch, nonMatch, secondMatch));
        Min min = createMin(tasks);
        min.getResponse("find read");

        String response = min.getResponse("mark 3");

        assertEquals(Messages.formatIndexOutOfRange("task", 2), response);
        assertFalse(secondMatch.isDone());
    }

    @Test
    void getResponse_listtasksAfterFind_restoresFullListNumbering() {
        Todo firstMatch = new Todo("read book");
        Todo nonMatch = new Todo("buy milk");
        Todo secondMatch = new Todo("reread notes");
        TaskList tasks = new TaskList(List.of(firstMatch, nonMatch, secondMatch));
        Min min = createMin(tasks);

        min.getResponse("find read");
        min.getResponse("listtasks");
        min.getResponse("mark 2");

        assertTrue(nonMatch.isDone());
        assertFalse(secondMatch.isDone());
    }

    private Min createMin(TaskList tasks) {
        return createMin(tasks, new NoteList(List.of()));
    }

    private Min createMin(TaskList tasks, NoteList notes) {
        return new Min(parser, tasks, storage, notes, noteStorage);
    }

    private static class RecordingTaskStorage extends TaskStorage {
        private int saveCount;

        @Override
        public void save(List<Task> tasks) {
            saveCount++;
        }

        private int getSaveCount() {
            return saveCount;
        }
    }

    private static class RecordingNoteStorage extends NoteStorage {
        private int saveCount;

        @Override
        public void save(List<Note> notes) {
            saveCount++;
        }

        private int getSaveCount() {
            return saveCount;
        }
    }

    private static class LoadingTaskStorage extends TaskStorage {
        private final List<Task> tasks;

        private LoadingTaskStorage(List<Task> tasks) {
            this.tasks = tasks;
        }

        @Override
        public List<Task> load() {
            return tasks;
        }
    }

    private static class LoadingNoteStorage extends NoteStorage {
        private final List<Note> notes;

        private LoadingNoteStorage(List<Note> notes) {
            this.notes = notes;
        }

        @Override
        public List<Note> load() {
            return notes;
        }
    }

    private static class FailingTaskStorage extends TaskStorage {
        private final RuntimeException failure;

        private FailingTaskStorage(RuntimeException failure) {
            this.failure = failure;
        }

        @Override
        public List<Task> load() {
            throw failure;
        }
    }

    private static class FailingNoteStorage extends NoteStorage {
        private final RuntimeException failure;

        private FailingNoteStorage(RuntimeException failure) {
            this.failure = failure;
        }

        @Override
        public List<Note> load() {
            throw failure;
        }
    }

    private static class SaveFailingTaskStorage extends TaskStorage {

        @Override
        public void save(List<Task> tasks) throws IOException {
            throw new IOException("Unable to save test tasks.");
        }
    }

    private static class SaveFailingNoteStorage extends NoteStorage {

        @Override
        public void save(List<Note> notes) throws IOException {
            throw new IOException("Unable to save test notes.");
        }
    }
}
