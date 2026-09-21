package auto.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import auto.storage.Storage;
import auto.task.Task;
import auto.task.TaskList;
import auto.task.ToDo;

class AddCommandTest extends CommandTestSupport {
    @TempDir
    Path tempDirectory;

    @Test
    void execute_saveSucceeds_addsPersistsAndAcknowledgesTask() throws Exception {
        Task task = new ToDo("read book");
        TaskList tasks = new TaskList();
        Storage storage = writableStorage(tempDirectory);

        CommandResult result = new AddCommand(task).execute(tasks, storage);

        assertEquals(1, tasks.size());
        assertSame(task, tasks.get(1));
        assertEquals("[T][ ] read book", storage.load().tasks().getFirst().toString());
        assertTrue(result.message().contains("Can! Added this task for you:"));
        assertTrue(result.message().contains("You now have 1 tasks on your list. Slowly clear, can one."));
    }

    @Test
    void execute_saveFails_rollsBackAdditionAndReportsFailure() {
        TaskList tasks = new TaskList();

        CommandResult result = new AddCommand(new ToDo("read book"))
                .execute(tasks, failingStorage(tempDirectory));

        assertEquals(0, tasks.size());
        assertTrue(result.message().contains("Paiseh, I couldn't save your tasks."));
        assertTrue(!result.message().contains("Can! Added this task for you:"));
    }
}
