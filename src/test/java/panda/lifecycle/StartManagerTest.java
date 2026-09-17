package panda.lifecycle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import panda.exception.storage.FileCorruptedException;
import panda.exception.storage.TaskLoadingException;
import panda.task.Event;
import panda.task.Task;
import panda.task.TaskList;

/**
 * Contains unit tests for task-list initialization from decoded tasks.
 */
public class StartManagerTest {

    @Test
    public void createTaskList_nonOverlappingTasks_returnsPopulatedTaskList() throws Exception {
        List<Task> tasks = List.of(
                createEvent("lecture", 9, 0, 10, 0),
                createEvent("laboratory", 10, 0, 11, 0));

        TaskList taskList = StartManager.createTaskList(tasks);

        assertEquals(2, taskList.getSize());
    }

    @Test
    public void createTaskList_overlappingEvents_throwsTaskLoadingException() throws Exception {
        List<Task> tasks = List.of(
                createEvent("lecture", 9, 0, 11, 0),
                createEvent("workshop", 10, 0, 12, 0));

        TaskLoadingException exception = assertThrows(TaskLoadingException.class, () ->
                StartManager.createTaskList(tasks));

        assertEquals("Your old training plan is a mess. Honestly, this tracks. We're starting fresh."
                + System.lineSeparator()
                + "Stored events overlap.", exception.getMessage());
        assertInstanceOf(FileCorruptedException.class, exception.getCause());
    }

    private static Event createEvent(String description, int startHour, int startMinute,
            int endHour, int endMinute) throws Exception {
        return new Event(description,
                LocalDateTime.of(2026, 9, 10, startHour, startMinute),
                LocalDateTime.of(2026, 9, 10, endHour, endMinute));
    }
}
