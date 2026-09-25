package shan.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import shan.exception.DataFileException;
import shan.exception.InvalidArgumentException;
import shan.exception.ShanException;
import shan.storage.Storage;
import shan.task.Deadline;
import shan.task.Event;
import shan.task.Task;
import shan.task.TaskList;
import shan.task.ToDo;
import shan.ui.Ui;

class CommandExecutionTest {
    @Test
    void add_saveSucceeds_addsAndPersistsTask() throws ShanException {
        TaskList tasks = new TaskList();
        Task task = new ToDo("read book");
        RecordingUi ui = new RecordingUi();
        RecordingStorage storage = new RecordingStorage(false);

        new AddCommand(task).execute(tasks, ui, storage);

        assertEquals(1, tasks.size());
        assertSame(task, tasks.get(1));
        assertEquals(List.of(task), storage.savedTasks());
        assertTrue(ui.message().contains("I've added this"));
    }

    @Test
    void add_saveFails_removesAddedTask() {
        TaskList tasks = new TaskList();
        RecordingUi ui = new RecordingUi();
        RecordingStorage storage = new RecordingStorage(true);

        assertThrows(
                DataFileException.class, () -> new AddCommand(new ToDo("read book")).execute(tasks, ui, storage));

        assertEquals(0, tasks.size());
        assertNull(ui.message());
    }

    @Test
    void delete_validTask_deletesAndPersistsRemainingTasks()
            throws ShanException {
        Task firstTask = new ToDo("first task");
        Task secondTask = new ToDo("second task");
        TaskList tasks = taskListOf(firstTask, secondTask);
        RecordingUi ui = new RecordingUi();
        RecordingStorage storage = new RecordingStorage(false);

        new DeleteCommand(1).execute(tasks, ui, storage);

        assertEquals(1, tasks.size());
        assertSame(secondTask, tasks.get(1));
        assertEquals(List.of(secondTask), storage.savedTasks());
    }

    @Test
    void delete_invalidTaskNumber_exceptionThrownWithoutSaving() {
        TaskList tasks = taskListOf(new ToDo("read book"));
        RecordingUi ui = new RecordingUi();
        RecordingStorage storage = new RecordingStorage(false);

        assertThrows(
                InvalidArgumentException.class, () -> new DeleteCommand(2).execute(tasks, ui, storage));

        assertEquals(1, tasks.size());
        assertEquals(0, storage.saveCalls());
        assertNull(ui.message());
    }

    @Test
    void delete_saveFails_restoresTaskAtOriginalPosition() {
        Task firstTask = new ToDo("first task");
        Task secondTask = new ToDo("second task");
        TaskList tasks = taskListOf(firstTask, secondTask);
        RecordingUi ui = new RecordingUi();
        RecordingStorage storage = new RecordingStorage(true);

        assertThrows(
                DataFileException.class, () -> new DeleteCommand(1).execute(tasks, ui, storage));

        assertEquals(2, tasks.size());
        assertSame(firstTask, tasks.get(1));
        assertSame(secondTask, tasks.get(2));
        assertNull(ui.message());
    }

    @Test
    void mark_validTask_marksAndPersistsTask()
            throws ShanException {
        Task task = new ToDo("read book");
        TaskList tasks = taskListOf(task);
        RecordingUi ui = new RecordingUi();
        RecordingStorage storage = new RecordingStorage(false);

        new MarkCommand(1).execute(tasks, ui, storage);

        assertTrue(task.isDone());
        assertEquals(List.of(task), storage.savedTasks());
        assertEquals(
                "Well done! I have marked this task as done!\n  [T][X] read book",
                ui.message());
    }

    @Test
    void mark_saveFails_restoresIncompleteState() {
        Task task = new ToDo("read book");
        TaskList tasks = taskListOf(task);
        RecordingUi ui = new RecordingUi();
        RecordingStorage storage = new RecordingStorage(true);

        assertThrows(
                DataFileException.class, () -> new MarkCommand(1).execute(tasks, ui, storage));

        assertFalse(task.isDone());
        assertNull(ui.message());
    }

    @Test
    void unmark_validTask_unmarksAndPersistsTask()
            throws ShanException {
        Task task = new ToDo("read book");
        task.markDone();
        TaskList tasks = taskListOf(task);
        RecordingUi ui = new RecordingUi();
        RecordingStorage storage = new RecordingStorage(false);

        new UnmarkCommand(1).execute(tasks, ui, storage);

        assertFalse(task.isDone());
        assertEquals(List.of(task), storage.savedTasks());
        assertEquals(
                "What happened? I have unmarked this task as completed...\n  [T][ ] read book",
                ui.message());
    }

    @Test
    void unmark_saveFails_restoresCompletedState() {
        Task task = new ToDo("read book");
        task.markDone();
        TaskList tasks = taskListOf(task);
        RecordingUi ui = new RecordingUi();
        RecordingStorage storage = new RecordingStorage(true);

        assertThrows(
                DataFileException.class, () -> new UnmarkCommand(1).execute(tasks, ui, storage));

        assertTrue(task.isDone());
        assertNull(ui.message());
    }

    @Test
    void on_rangeQuery_displaysOnlyOverlappingDatedTasks() throws ShanException {
        LocalDateTime deadlineDate = LocalDateTime.of(2019, 12, 3, 18, 0);
        Task toDo = new ToDo("read book");
        Task deadline = new Deadline("return book", deadlineDate);
        Task event = new Event(
                "project meeting",
                LocalDateTime.of(2019, 12, 4, 14, 0),
                LocalDateTime.of(2019, 12, 6, 16, 0));
        TaskList tasks = taskListOf(toDo, deadline, event);
        RecordingUi ui = new RecordingUi();
        RecordingStorage storage = new RecordingStorage(false);

        new OnCommand(
                LocalDate.of(2019, 12, 3),
                LocalDate.of(2019, 12, 5)).execute(tasks, ui, storage);

        assertFalse(ui.message().contains("1.[T]"));
        assertTrue(ui.message().contains("2.[D]"));
        assertTrue(ui.message().contains("3.[E]"));
        assertEquals(0, storage.saveCalls());
    }

    @Test
    void on_endBeforeStart_assertionThrown() {
        LocalDate startDate = LocalDate.of(2019, 12, 5);
        LocalDate endDate = LocalDate.of(2019, 12, 3);

        assertThrows(AssertionError.class, () -> new OnCommand(startDate, endDate));
    }

    @Test
    void find_matchingTasks_displaysRenumberedMatchesWithoutSaving() throws ShanException {
        Task nonMatch = new ToDo("write report");
        Task firstMatch = new ToDo("read book");
        Task secondMatch = new Deadline(
                "return BOOK", LocalDateTime.of(2019, 12, 3, 18, 0));
        TaskList tasks = taskListOf(nonMatch, firstMatch, secondMatch);
        RecordingUi ui = new RecordingUi();
        RecordingStorage storage = new RecordingStorage(false);

        new FindCommand("book").execute(tasks, ui, storage);

        assertEquals(
                "Here are the matching tasks in your list:\n"
                        + "1.[T][ ] read book\n"
                        + "2.[D][ ] return BOOK (by: Dec 03 2019, 6:00 PM)",
                ui.message());
        assertEquals(0, storage.saveCalls());
    }

    @Test
    void find_noMatchingTask_displaysEmptyResultWithoutSaving() throws ShanException {
        TaskList tasks = taskListOf(new ToDo("read book"));
        RecordingUi ui = new RecordingUi();
        RecordingStorage storage = new RecordingStorage(false);

        new FindCommand("report").execute(tasks, ui, storage);

        assertEquals("Here are the matching tasks in your list:", ui.message());
        assertEquals(0, storage.saveCalls());
    }

    @Test
    void undo_saveFails_restoresCurrentStateAndRetainsUndoState() {
        Task firstTask = new ToDo("first task");
        Task secondTask = new ToDo("second task");
        TaskList tasks = taskListOf(firstTask);
        TaskList.State stateBeforeAdd = tasks.createState();
        tasks.add(secondTask);
        tasks.recordUndoState(stateBeforeAdd);
        RecordingStorage storage = new RecordingStorage(true);

        assertThrows(
                DataFileException.class, () -> new UndoCommand().execute(tasks, storage));

        assertEquals(2, tasks.size());
        assertSame(firstTask, tasks.get(1));
        assertSame(secondTask, tasks.get(2));
        assertTrue(tasks.canUndo());
    }

    private TaskList taskListOf(Task... tasks) {
        TaskList taskList = new TaskList();
        taskList.replaceAll(List.of(tasks));
        return taskList;
    }

    private static class RecordingUi extends Ui {
        private String message;

        @Override
        public void showMessage(String message) {
            this.message = message;
        }

        private String message() {
            return this.message;
        }
    }

    private static class RecordingStorage extends Storage {
        private final boolean shouldFail;
        private List<Task> savedTasks;
        private int saveCalls;

        RecordingStorage(boolean shouldFail) {
            super(Path.of("unused"));
            this.shouldFail = shouldFail;
            this.savedTasks = List.of();
        }

        @Override
        public void save(List<Task> tasks) throws DataFileException {
            this.saveCalls++;
            if (this.shouldFail) {
                throw new DataFileException("Simulated save failure.");
            }
            this.savedTasks = List.copyOf(tasks);
        }

        private List<Task> savedTasks() {
            return this.savedTasks;
        }

        private int saveCalls() {
            return this.saveCalls;
        }
    }
}
