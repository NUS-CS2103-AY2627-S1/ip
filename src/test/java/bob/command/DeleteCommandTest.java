package bob.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import bob.exception.BobException;
import bob.storage.Storage;
import bob.task.TaskList;
import bob.task.ToDo;

/** Tests removing a task through a {@link DeleteCommand}. */
class DeleteCommandTest {
    @TempDir
    private Path tempDirectory;

    @Test
    void execute_validOneBasedTaskNumber_removesSavesAndReturnsConfirmation()
            throws BobException, IOException {
        TaskList taskList = new TaskList(List.of(new ToDo("first task"), new ToDo("second task")));
        Storage storage = new Storage(tempDirectory.resolve("tasks.txt").toString());

        String response = new DeleteCommand(1).execute(taskList, storage);

        assertEquals(1, taskList.getTaskCount());
        assertEquals("second task", taskList.get(0).getDescription());
        assertEquals("second task", storage.load().get(0).getDescription());
        assertTrue(response.contains("Consider it cleared"));
        assertTrue(response.contains("[T][ ] first task"));
    }

    @Test
    void execute_nonexistentTaskNumber_bobExceptionThrown() {
        TaskList taskList = new TaskList(List.of(new ToDo("read book")));
        Storage storage = new Storage(tempDirectory.resolve("tasks.txt").toString());

        assertThrows(BobException.class, () -> new DeleteCommand(2).execute(taskList, storage));
    }

    @Test
    void execute_lastTask_usesPluralTaskLabelWhenListIsEmpty() throws Exception {
        TaskList taskList = TaskList.of(new ToDo("read book"));
        Storage storage = new Storage(tempDirectory.resolve("tasks.txt").toString());

        String response = new DeleteCommand(1).execute(taskList, storage);

        assertEquals(0, taskList.getTaskCount());
        assertTrue(response.contains("There are 0 tasks left in your list."));
    }
}
