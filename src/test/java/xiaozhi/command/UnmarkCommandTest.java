package xiaozhi.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import xiaozhi.exception.XiaoZhiException;
import xiaozhi.storage.Storage;
import xiaozhi.task.TaskList;
import xiaozhi.task.Todo;
import xiaozhi.testutil.OutputCapture;
import xiaozhi.ui.Ui;

/**
 * Tests {@link UnmarkCommand}, the mirror image of {@link MarkCommandTest}:
 * the out-of-range guard, and {@code undo} restoring whatever the task's
 * done status was immediately before this specific command ran.
 */
public class UnmarkCommandTest {

    @TempDir
    Path tempDir;

    private Storage newStorage() {
        return new Storage(tempDir.resolve("xiaozhi.txt").toString());
    }

    @Test
    public void execute_doneTask_unmarksAndReports() throws Exception {
        TaskList tasks = new TaskList();
        Todo task = new Todo("read book");
        task.markAsDone();
        tasks.add(task);
        Ui ui = new Ui();
        UnmarkCommand command = new UnmarkCommand(0);

        String output = OutputCapture.capture(() ->
                command.execute(tasks, ui, newStorage(), new CommandHistory()));

        assertFalse(tasks.get(0).isDone());
        assertEquals(
                "What was closed is opened again." + System.lineSeparator()
                        + "  [T][ ] read book" + System.lineSeparator(),
                output);
    }

    @Test
    public void execute_indexBeyondListSize_throwsWithTaskCountInMessage() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        UnmarkCommand command = new UnmarkCommand(4);

        XiaoZhiException thrown = assertThrows(XiaoZhiException.class, () ->
                command.execute(tasks, new Ui(), newStorage(), new CommandHistory()));

        assertEquals("There is no task 5 to speak of. Only 1 task(s) exist; look again before you act.",
                thrown.getMessage());
    }

    @Test
    public void undo_taskWasDoneBeforeExecute_marksItDoneAgain() throws Exception {
        TaskList tasks = new TaskList();
        Todo task = new Todo("read book");
        task.markAsDone();
        tasks.add(task);
        Ui ui = new Ui();
        Storage storage = newStorage();
        UnmarkCommand command = new UnmarkCommand(0);
        command.execute(tasks, ui, storage, new CommandHistory());

        command.undo(tasks, ui, storage);

        assertTrue(tasks.get(0).isDone());
    }

    @Test
    public void undo_taskWasAlreadyNotDoneBeforeExecute_staysNotDone() throws Exception {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        Ui ui = new Ui();
        Storage storage = newStorage();
        UnmarkCommand command = new UnmarkCommand(0);
        command.execute(tasks, ui, storage, new CommandHistory());

        command.undo(tasks, ui, storage);

        assertFalse(tasks.get(0).isDone());
    }
}
