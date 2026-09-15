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
 * Tests marking, saving, and reporting a task through {@link MarkCommand}.
 */
public class MarkCommandTest {
    @Test
    public void execute_validIndex_marksSavesAndReturnsResponse()
            throws TianyiException {
        TaskList tasks = new TaskList(List.of(new ToDo("first"), new ToDo("second")));
        RecordingStorage storage = new RecordingStorage();
        MarkCommand command = new MarkCommand(1);

        Response response = command.execute(tasks, storage);

        assertEquals("T | 0 | first", tasks.getTasks().get(0).getData());
        assertEquals("T | 1 | second", tasks.getTasks().get(1).getData());
        assertNotNull(storage.savedTasks);
        assertEquals("T | 1 | second", storage.savedTasks.get(1).getData());
        assertEquals("Well done! I've marked this task as complete:", response.getHeader());
        assertEquals("  [T][X] second", response.getMessage());
        assertEquals("", response.getFooter());
    }

    @Test
    public void execute_lastIncompleteTask_returnsCompletionEncouragement()
            throws TianyiException {
        TaskList tasks = new TaskList(List.of(new ToDo("read book")));
        RecordingStorage storage = new RecordingStorage();

        Response response = new MarkCommand(0).execute(tasks, storage);

        assertEquals("  [T][X] read book", response.getMessage());
        assertEquals("That's everything done. You've earned a little rest!", response.getFooter());
        assertNotNull(storage.savedTasks);
    }

    @Test
    public void execute_alreadyCompletedTask_doesNotRepeatCompletionEncouragement()
            throws TianyiException {
        ToDo completedTask = new ToDo("read book");
        completedTask.markAsDone();
        TaskList tasks = new TaskList(List.of(completedTask));
        RecordingStorage storage = new RecordingStorage();

        Response response = new MarkCommand(0).execute(tasks, storage);

        assertEquals("  [T][X] read book", response.getMessage());
        assertEquals("", response.getFooter());
        assertNotNull(storage.savedTasks);
    }
}
