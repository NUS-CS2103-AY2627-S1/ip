package xiaozhi.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import xiaozhi.exception.XiaoZhiException;
import xiaozhi.storage.Storage;
import xiaozhi.task.Task;
import xiaozhi.task.TaskList;
import xiaozhi.task.Todo;
import xiaozhi.testutil.OutputCapture;
import xiaozhi.ui.Ui;

/**
 * Tests {@link DeleteCommand}, including its out-of-range guard and its
 * {@code undo} putting the deleted task back at its original position.
 */
public class DeleteCommandTest {

    @TempDir
    Path tempDir;

    @Test
    public void execute_validIndex_removesTaskAndReportsAndSaves() throws Exception {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        Ui ui = new Ui();
        Storage storage = new Storage(tempDir.resolve("xiaozhi.txt").toString());
        DeleteCommand command = new DeleteCommand(0);

        String output = OutputCapture.capture(() ->
                command.execute(tasks, ui, storage, new CommandHistory()));

        assertEquals(0, tasks.size());
        assertEquals(
                "What no longer serves you has been released." + System.lineSeparator()
                        + "  [T][ ] read book" + System.lineSeparator()
                        + "Now 0 task(s) remain." + System.lineSeparator(),
                output);
        assertTrue(storage.load().isEmpty());
    }

    @Test
    public void execute_indexBeyondListSize_throwsWithTaskCountInMessage() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        DeleteCommand command = new DeleteCommand(4);

        XiaoZhiException thrown = assertThrows(XiaoZhiException.class, () ->
                command.execute(tasks, new Ui(), new Storage(tempDir.resolve("xiaozhi.txt").toString()),
                        new CommandHistory()));

        assertEquals("There is no task 5 to speak of. Only 1 task(s) exist; look again before you act.",
                thrown.getMessage());
        assertEquals(1, tasks.size());
    }

    @Test
    public void execute_negativeIndex_throws() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        DeleteCommand command = new DeleteCommand(-1);

        assertThrows(XiaoZhiException.class, () ->
                command.execute(tasks, new Ui(), new Storage(tempDir.resolve("xiaozhi.txt").toString()),
                        new CommandHistory()));
    }

    @Test
    public void isUndoable_returnsTrue() {
        assertTrue(new DeleteCommand(0).isUndoable());
    }

    @Test
    public void undo_afterExecute_putsTaskBackAtOriginalPosition() throws Exception {
        TaskList tasks = new TaskList();
        Task first = new Todo("read book");
        Task second = new Todo("return book");
        tasks.add(first);
        tasks.add(second);
        Ui ui = new Ui();
        Storage storage = new Storage(tempDir.resolve("xiaozhi.txt").toString());
        DeleteCommand command = new DeleteCommand(0);
        command.execute(tasks, ui, storage, new CommandHistory());

        OutputCapture.capture(() -> command.undo(tasks, ui, storage));

        assertEquals(2, tasks.size());
        assertSame(first, tasks.get(0));
        assertSame(second, tasks.get(1));
    }
}
