package tianyi.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;

import org.junit.jupiter.api.Test;

import tianyi.Response;
import tianyi.command.CommandTestFixture.RecordingStorage;
import tianyi.task.Deadline;
import tianyi.task.TaskList;
import tianyi.task.TaskTime;
import tianyi.task.ToDo;

/**
 * Tests listing all tasks or tasks relevant to a date through {@link ListCommand}.
 */
public class ListCommandTest {
    @Test
    public void execute_withoutDate_returnsAllTasksWithoutSaving() {
        TaskList tasks = new TaskList(List.of(new ToDo("read book")));
        RecordingStorage storage = new RecordingStorage();
        ListCommand command = new ListCommand(null);

        Response response = command.execute(tasks, storage);

        assertEquals("Here's your task list. Let's have a look:", response.getHeader());
        assertEquals("1.[T][ ] read book", response.getMessage());
        assertEquals("", response.getFooter());
        assertNull(storage.savedTasks);
    }

    @Test
    public void execute_withDate_returnsMatchingTasksWithoutSaving() {
        TaskList tasks = new TaskList(List.of(
                new ToDo("read book"),
                new Deadline("submit report", new TaskTime("3-12-2019"))));
        RecordingStorage storage = new RecordingStorage();
        ListCommand command = new ListCommand(new TaskTime("2-12-2019"));

        Response response = command.execute(tasks, storage);

        assertEquals("Here are your deadlines and events for 2-12-2019:", response.getHeader());
        assertEquals("2.[D][ ] submit report (by: Tue, Dec 03 2019)", response.getMessage());
        assertEquals("", response.getFooter());
        assertNull(storage.savedTasks);
    }

    @Test
    public void execute_emptyList_returnsInvitationWithoutSaving() {
        RecordingStorage storage = new RecordingStorage();

        Response response = new ListCommand(null).execute(new TaskList(), storage);

        assertEquals("", response.getHeader());
        assertEquals("Your list is empty. What would you like to add?", response.getMessage());
        assertNull(storage.savedTasks);
    }

    @Test
    public void execute_allTasksCompleted_returnsCompletionEncouragementWithoutSaving() {
        ToDo completedTask = new ToDo("read book");
        completedTask.markAsDone();
        TaskList tasks = new TaskList(List.of(completedTask));
        RecordingStorage storage = new RecordingStorage();

        Response response = new ListCommand(null).execute(tasks, storage);

        assertEquals("Here's your task list. Let's have a look:", response.getHeader());
        assertEquals("1.[T][X] read book", response.getMessage());
        assertEquals("All done. Enjoy a little time for yourself!", response.getFooter());
        assertNull(storage.savedTasks);
    }

    @Test
    public void execute_dateWithNoMatchingTasks_returnsNoTasksFoundWithoutSaving() {
        TaskList tasks = new TaskList(List.of(new ToDo("read book")));
        RecordingStorage storage = new RecordingStorage();

        Response response = new ListCommand(new TaskTime("2-12-2019")).execute(tasks, storage);

        assertEquals("", response.getHeader());
        assertEquals("No tasks found.", response.getMessage());
        assertNull(storage.savedTasks);
    }

    @Test
    public void execute_dateWithAllTasksCompleted_returnsCompletionEncouragementWithoutSaving() {
        Deadline completedDeadline = new Deadline("submit report", new TaskTime("3-12-2019"));
        completedDeadline.markAsDone();
        TaskList tasks = new TaskList(List.of(new ToDo("read book"), completedDeadline));
        RecordingStorage storage = new RecordingStorage();

        Response response = new ListCommand(new TaskTime("2-12-2019")).execute(tasks, storage);

        assertEquals("Here are your deadlines and events for 2-12-2019:", response.getHeader());
        assertEquals("2.[D][X] submit report (by: Tue, Dec 03 2019)", response.getMessage());
        assertEquals("All tasks listed for 2-12-2019 are done. Nicely done!", response.getFooter());
        assertNull(storage.savedTasks);
    }
}
