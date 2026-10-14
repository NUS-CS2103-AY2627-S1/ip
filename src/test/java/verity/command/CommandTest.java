package verity.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import verity.VerityException;
import verity.storage.Storage;
import verity.task.TaskList;
import verity.task.Todo;

class CommandTest {

    @TempDir
    Path tempDir;

    private Storage newStorage() {
        return new Storage(tempDir.resolve("verity.txt").toString());
    }

    @Test
    void requireValidIndex_singleTaskInList_errorUsesSingularTaskWord() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        Command command = new MarkCommand(5);

        VerityException e = assertThrows(VerityException.class, () -> command.execute(tasks, newStorage()));

        assertEquals("There is no task 6, you currently only have 1 task.", e.getMessage());
    }

    @Test
    void requireValidIndex_multipleTasksInList_errorUsesPluralTaskWord() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Todo("write essay"));
        Command command = new DeleteCommand(5);

        VerityException e = assertThrows(VerityException.class, () -> command.execute(tasks, newStorage()));

        assertEquals("There is no task 6, you currently only have 2 tasks.", e.getMessage());
    }

    @Test
    void requireValidIndex_emptyList_errorUsesPluralTaskWord() {
        TaskList tasks = new TaskList();
        Command command = new UnmarkCommand(0);

        VerityException e = assertThrows(VerityException.class, () -> command.execute(tasks, newStorage()));

        assertEquals("There is no task 1, you currently only have 0 tasks.", e.getMessage());
    }

    @Test
    void execute_saveFails_warningAppendedButNoExceptionPropagates() throws IOException, VerityException {
        // Force IOException: the save file's parent path already exists as a regular
        // file, so Files.createDirectories() inside Storage.save() cannot create it.
        Path blocker = tempDir.resolve("blocker");
        Files.writeString(blocker, "not a directory");
        Storage storage = new Storage(blocker.resolve("verity.txt").toString());
        TaskList tasks = new TaskList();
        Command command = new AddCommand(new Todo("read book"));

        String response = command.execute(tasks, storage);

        assertEquals(1, tasks.size(), "the in-memory task list must still be updated even if saving fails");
        assertTrue(response.contains("Warning: could not save tasks to disk"));
    }

    @Test
    void isExit_exitCommand_returnsTrue() {
        assertTrue(new ExitCommand().isExit());
    }

    @Test
    void isExit_everyOtherCommand_returnsFalse() {
        assertFalse(new ListCommand().isExit());
        assertFalse(new AddCommand(new Todo("read book")).isExit());
        assertFalse(new MarkCommand(0).isExit());
        assertFalse(new UnmarkCommand(0).isExit());
        assertFalse(new DeleteCommand(0).isExit());
    }

    @Test
    void listCommand_emptyList_noTasksMessage() {
        String response = new ListCommand().execute(new TaskList(), null);

        assertEquals("Nothing. You have zero tasks - impressive, in a bad way.", response);
    }

    @Test
    void listCommand_nonEmptyList_numberedListReturned() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        String response = new ListCommand().execute(tasks, null);

        assertEquals("Here's all 1 tasks, since you clearly forgot:\n1.[T][ ] read book", response);
    }

    @Test
    void addCommand_undo_removesTheAddedTask() throws VerityException {
        TaskList tasks = new TaskList();
        AddCommand command = new AddCommand(new Todo("read book"));
        command.execute(tasks, newStorage());

        String response = command.undo(tasks, newStorage());

        assertTrue(tasks.isEmpty());
        assertTrue(response.contains("Whatever. Un-did it"));
    }

    @Test
    void deleteCommand_undo_restoresTaskAtOriginalIndex() throws VerityException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Todo("write essay"));
        tasks.add(new Todo("return book"));
        DeleteCommand command = new DeleteCommand(1);
        command.execute(tasks, newStorage());

        command.undo(tasks, newStorage());

        assertEquals(3, tasks.size());
        assertEquals("[T][ ] write essay", tasks.get(1).toString());
    }

    @Test
    void markCommand_undo_marksTheTaskAsNotDone() throws VerityException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        MarkCommand command = new MarkCommand(0);
        command.execute(tasks, newStorage());

        command.undo(tasks, newStorage());

        assertEquals("[T][ ] read book", tasks.get(0).toString());
    }

    @Test
    void unmarkCommand_undo_marksTheTaskAsDone() throws VerityException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.get(0).markAsDone();
        UnmarkCommand command = new UnmarkCommand(0);
        command.execute(tasks, newStorage());

        command.undo(tasks, newStorage());

        assertEquals("[T][X] read book", tasks.get(0).toString());
    }

    @Test
    void undoCommand_nothingToUndo_returnsNothingToUndoMessage() {
        String response = new UndoCommand(null).execute(new TaskList(), newStorage());

        assertEquals("There's nothing to undo, genius.", response);
    }

    @Test
    void undoCommand_givenUndoableCommand_delegatesToItsUndo() throws VerityException {
        TaskList tasks = new TaskList();
        AddCommand addCommand = new AddCommand(new Todo("read book"));
        addCommand.execute(tasks, newStorage());

        String response = new UndoCommand(addCommand).execute(tasks, newStorage());

        assertTrue(tasks.isEmpty());
        assertTrue(response.contains("Whatever. Un-did it"));
    }

    @Test
    void findCommand_noMatches_returnsNothingMatchesMessage() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        String response = new FindCommand("essay").execute(tasks, null);

        assertEquals("Nothing matches. Try spelling it right.", response);
    }

    @Test
    void onCommand_noMatches_returnsNothingOnDateMessage() {
        TaskList tasks = new TaskList();
        LocalDate date = LocalDate.of(2020, 1, 1);

        String response = new OnCommand(date).execute(tasks, null);

        assertEquals("Nothing on Jan 01 2020. Shocking, I know.", response);
    }

    @Test
    void exitCommand_execute_returnsFarewellMessage() {
        String response = new ExitCommand().execute(new TaskList(), null);

        assertEquals("Finally. Try not to forget anything important. Bye! ;)", response);
    }
}
