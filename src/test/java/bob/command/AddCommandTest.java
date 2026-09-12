package bob.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import bob.exception.BobException;
import bob.storage.Storage;
import bob.task.Task;
import bob.task.TaskList;
import bob.task.ToDo;

/** Tests adding a task through an {@link AddCommand}. */
class AddCommandTest {
    @TempDir
    private Path tempDirectory;

    @Test
    void execute_validTask_addsSavesAndReturnsConfirmation() throws BobException, IOException {
        Task task = new ToDo("read book");
        TaskList taskList = new TaskList();
        Storage storage = new Storage(tempDirectory.resolve("tasks.txt").toString());

        String response = new AddCommand(task).execute(taskList, storage);

        assertEquals(1, taskList.getTaskCount());
        assertSame(task, taskList.get(0));
        assertEquals("read book", storage.load().get(0).getDescription());
        assertTrue(response.contains("added: [T][ ] read book"));
    }
}
