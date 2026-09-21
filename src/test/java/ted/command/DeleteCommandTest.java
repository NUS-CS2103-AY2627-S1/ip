package ted.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import ted.Storage;
import ted.TedException;
import ted.Ui;
import ted.task.TaskList;
import ted.task.Todo;

/**
 * Tests {@link DeleteCommand}, which removes a task, echoes it and saves the list.
 * <p>
 * Every test saves inside a {@link TempDir}, so running the tests can never
 * disturb the user's own tasks.
 */
public class DeleteCommandTest {
    /**
     * Verifies that the chosen task is removed, echoed back, and that the
     * shorter list is saved.
     */
    @Test
    public void execute_validNumber_removesEchoesAndSaves(@TempDir Path tempDir) throws TedException {
        Storage storage = new Storage(tempDir.resolve("ted.txt").toString());
        TaskList tasks = new TaskList(List.of(new Todo("borrow book"), new Todo("read book")));
        Ui ui = new Ui();

        new DeleteCommand(0).execute(tasks, ui, storage);

        String reply = ui.flush();
        assertTrue(reply.contains("[T][ ] borrow book"));
        assertTrue(reply.contains("That's 1 task."));
        assertEquals("[T][ ] read book", storage.load().get(0).toString());
    }

    /**
     * Verifies that a number past the end of the list is rejected and nothing
     * is removed.
     */
    @Test
    public void execute_numberPastEnd_exceptionThrownAndListUnchanged(@TempDir Path tempDir) {
        Storage storage = new Storage(tempDir.resolve("ted.txt").toString());
        TaskList tasks = new TaskList(List.of(new Todo("borrow book")));
        DeleteCommand deleteSecond = new DeleteCommand(1);

        assertThrows(TedException.class, () -> deleteSecond.execute(tasks, new Ui(), storage));
        assertEquals(1, tasks.size());
    }
}
