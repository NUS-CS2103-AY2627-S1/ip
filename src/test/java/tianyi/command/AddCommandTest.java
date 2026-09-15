package tianyi.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.Test;

import tianyi.Response;
import tianyi.TianyiException;
import tianyi.command.CommandTestFixture.RecordingStorage;
import tianyi.task.TaskList;
import tianyi.task.ToDo;

/**
 * Tests adding, saving, and reporting a task through {@link AddCommand}.
 */
public class AddCommandTest {
    @Test
    public void execute_validTask_addsSavesAndReturnsResponse()
            throws TianyiException {
        TaskList tasks = new TaskList();
        RecordingStorage storage = new RecordingStorage();
        AddCommand command = new AddCommand(new ToDo("read book"));

        Response response = command.execute(tasks, storage);

        assertEquals(1, tasks.size());
        assertEquals("T | 0 | read book", tasks.getTasks().get(0).getData());
        assertNotNull(storage.savedTasks);
        assertEquals("T | 0 | read book", storage.savedTasks.get(0).getData());
        assertEquals("Got it. I've added this task for you:", response.getHeader());
        assertEquals("  [T][ ] read book", response.getMessage());
        assertEquals("Now you have 1 tasks in the list.\n"
                + "You've got this. I'm cheering for you!", response.getFooter());
    }

    @Test
    public void execute_moreThanFiveIncompleteTasks_returnsRestReminder()
            throws TianyiException {
        TaskList tasks = new TaskList(List.of(
                new ToDo("first"),
                new ToDo("second"),
                new ToDo("third"),
                new ToDo("fourth"),
                new ToDo("fifth")));
        RecordingStorage storage = new RecordingStorage();

        Response response = new AddCommand(new ToDo("sixth")).execute(tasks, storage);

        assertEquals(6, tasks.size());
        assertEquals("  [T][ ] sixth", response.getMessage());
        assertEquals("Now you have 6 tasks in the list.\n"
                + "Remember to rest, too. Take care of yourself.", response.getFooter());
        assertNotNull(storage.savedTasks);
    }
}
