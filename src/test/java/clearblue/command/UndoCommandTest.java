package clearblue.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import clearblue.ClearblueException;
import clearblue.storage.Storage;
import clearblue.task.Task;
import clearblue.task.TaskList;
import clearblue.task.Todo;
import clearblue.ui.Ui;

/**
 * Tests for {@link UndoCommand} and the {@code undo()} overrides on the
 * commands that support it.
 */
public class UndoCommandTest {
    @TempDir
    private Path tempDir;

    private Storage newStorage() {
        return new Storage(tempDir.resolve("test-data.txt").toString());
    }

    @Test
    public void execute_noCommandToUndo_throwsNothingToUndo() {
        UndoCommand undoCommand = new UndoCommand();
        TaskList tasks = new TaskList();
        Ui ui = new Ui();
        Storage storage = newStorage();

        ClearblueException exception =
                assertThrows(ClearblueException.class, () -> undoCommand.execute(tasks, ui, storage));
        assertEquals("There is nothing to undo.", exception.getMessage());
    }

    @Test
    public void execute_undoesAdd_removesTheAddedTask() throws ClearblueException {
        TaskList tasks = new TaskList();
        AddCommand addCommand = new AddCommand(new Todo("read book"));
        addCommand.execute(tasks, new Ui(), newStorage());
        assertEquals(1, tasks.size());

        UndoCommand undoCommand = new UndoCommand();
        undoCommand.setCommandToUndo(addCommand);
        undoCommand.execute(tasks, new Ui(), newStorage());

        assertTrue(tasks.isEmpty());
    }

    @Test
    public void execute_undoesDelete_restoresTaskAtOriginalIndex() throws ClearblueException {
        TaskList tasks = new TaskList();
        Ui ui = new Ui();
        Storage storage = newStorage();
        new AddCommand(new Todo("first")).execute(tasks, ui, storage);
        new AddCommand(new Todo("second")).execute(tasks, ui, storage);

        DeleteCommand deleteCommand = new DeleteCommand(1);
        deleteCommand.execute(tasks, ui, storage);
        assertEquals(1, tasks.size());

        UndoCommand undoCommand = new UndoCommand();
        undoCommand.setCommandToUndo(deleteCommand);
        undoCommand.execute(tasks, ui, storage);

        assertEquals(2, tasks.size());
        assertEquals("first", tasks.get(0).getDescription());
        assertEquals("second", tasks.get(1).getDescription());
    }

    @Test
    public void execute_undoesMark_revertsTaskToNotDone() throws ClearblueException {
        TaskList tasks = new TaskList();
        Ui ui = new Ui();
        Storage storage = newStorage();
        new AddCommand(new Todo("read book")).execute(tasks, ui, storage);

        MarkCommand markCommand = new MarkCommand(1, true);
        markCommand.execute(tasks, ui, storage);
        assertTrue(tasks.get(0).isDone());

        UndoCommand undoCommand = new UndoCommand();
        undoCommand.setCommandToUndo(markCommand);
        undoCommand.execute(tasks, ui, storage);

        assertFalse(tasks.get(0).isDone());
    }

    @Test
    public void execute_undoesUnmark_revertsTaskToDone() throws ClearblueException {
        TaskList tasks = new TaskList();
        Ui ui = new Ui();
        Storage storage = newStorage();
        new AddCommand(new Todo("read book")).execute(tasks, ui, storage);
        Task task = tasks.get(0);
        task.markAsDone();

        MarkCommand unmarkCommand = new MarkCommand(1, false);
        unmarkCommand.execute(tasks, ui, storage);
        assertFalse(task.isDone());

        UndoCommand undoCommand = new UndoCommand();
        undoCommand.setCommandToUndo(unmarkCommand);
        undoCommand.execute(tasks, ui, storage);

        assertTrue(task.isDone());
    }
}
