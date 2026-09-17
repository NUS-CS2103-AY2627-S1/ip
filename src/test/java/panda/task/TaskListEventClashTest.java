package panda.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import panda.exception.task.EventClashException;

/**
 * Contains an integration test for event-clash handling in {@link TaskList}.
 */
public class TaskListEventClashTest {

    @Test
    public void addEvent_multipleClashes_rejectsEventAndListsConflictsChronologically() throws Exception {
        TaskList taskList = new TaskList();
        taskList.addEvent("laboratory", dateTimeAt(11, 0), dateTimeAt(12, 0));
        taskList.addTodo("buy lunch");
        taskList.addDeadline("submit report", dateTimeAt(9, 45));
        taskList.addEvent("lecture", dateTimeAt(9, 0), dateTimeAt(10, 0));
        taskList.markTask(1);

        EventClashException exception = assertThrows(EventClashException.class, () ->
                taskList.addEvent("workshop", dateTimeAt(9, 30), dateTimeAt(11, 30)));

        assertEquals("Unless your kungfu includes cloning, this event clashes with:"
                + System.lineSeparator()
                + "4.[E][ ] lecture (from: 10 Sep 2026 9:00 to: 10 Sep 2026 10:00)"
                + System.lineSeparator()
                + "1.[E][X] laboratory (from: 10 Sep 2026 11:00 to: 10 Sep 2026 12:00)"
                + System.lineSeparator()
                + "Pick another time. Yes, schedules have rules too.", exception.getMessage());
        assertEquals(4, taskList.getSize());

        taskList.delete(4);
        taskList.addEvent("consultation", dateTimeAt(9, 0), dateTimeAt(10, 0));
        assertEquals(4, taskList.getSize());
    }

    private static LocalDateTime dateTimeAt(int hour, int minute) {
        return LocalDateTime.of(2026, 9, 10, hour, minute);
    }
}
