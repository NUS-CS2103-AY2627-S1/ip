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
import ted.task.Task;
import ted.task.TaskList;
import ted.task.Todo;

/**
 * Tests {@link AddCommand}, which adds a task, confirms it and saves the list.
 * <p>
 * Every test saves inside a {@link TempDir}, so running the tests can never
 * disturb the user's own tasks.
 */
public class AddCommandTest {
    /**
     * Verifies that a new task is added, confirmed with the new count, and saved.
     */
    @Test
    public void execute_newTask_addsConfirmsAndSaves(@TempDir Path tempDir) throws TedException {
        Storage storage = new Storage(tempDir.resolve("ted.txt").toString());
        TaskList tasks = new TaskList();
        Ui ui = new Ui();

        new AddCommand(new Todo("borrow book")).execute(tasks, ui, storage);

        assertEquals(1, tasks.size());
        String reply = ui.flush();
        assertTrue(reply.contains("[T][ ] borrow book"));
        assertTrue(reply.contains("That's 1 task."));
        assertEquals(1, storage.load().size());
    }

    /**
     * Verifies that a task the list already holds is rejected and the list is
     * left as it was.
     */
    @Test
    public void execute_duplicateTask_exceptionThrownAndListUnchanged(@TempDir Path tempDir) {
        Storage storage = new Storage(tempDir.resolve("ted.txt").toString());
        TaskList tasks = new TaskList(List.of(new Todo("borrow book")));
        AddCommand addAgain = new AddCommand(new Todo("Borrow Book"));

        TedException e = assertThrows(TedException.class, () -> addAgain.execute(tasks, new Ui(), storage));

        assertTrue(e.getMessage().contains("already have this task"));
        List<Task> storedTasks = tasks.asList();
        assertEquals(1, storedTasks.size());
        assertEquals("[T][ ] borrow book", storedTasks.get(0).toString());
    }
}
