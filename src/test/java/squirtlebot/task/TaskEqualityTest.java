package squirtlebot.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import squirtlebot.TemporalPair;

/**
 * Tests value equality for concrete task types.
 */
public class TaskEqualityTest {
    @Test
    public void toDo_sameDescriptionAndStatus_tasksEqual() {
        Task first = new ToDo("read textbook");
        Task equivalent = new ToDo("  read textbook  ");
        Task different = new ToDo("buy textbook");

        assertEqualityBehavior(first, equivalent, different);
    }

    @Test
    public void toDo_differentCompletionStatus_tasksNotEqual() {
        Task incomplete = new ToDo("read textbook");
        Task completed = new ToDo("read textbook");
        completed.setDone(true);

        assertNotEquals(incomplete, completed);
        assertNotEquals(completed, incomplete);
    }

    @Test
    public void deadline_sameDescriptionDateAndStatus_tasksEqual() {
        Task first = new Deadline("submit report", LocalDate.of(2026, 9, 30));
        Task equivalent = new Deadline("submit report", LocalDate.of(2026, 9, 30));
        Task different = new Deadline("submit report", LocalDate.of(2026, 10, 1));

        assertEqualityBehavior(first, equivalent, different);
    }

    @Test
    public void deadline_differentDescription_tasksNotEqual() {
        Task first = new Deadline("submit report", LocalDate.of(2026, 9, 30));
        Task different = new Deadline("submit slides", LocalDate.of(2026, 9, 30));

        assertNotEquals(first, different);
        assertNotEquals(different, first);
    }

    @Test
    public void deadline_differentCompletionStatus_tasksNotEqual() {
        Task incomplete = new Deadline("submit report", LocalDate.of(2026, 9, 30));
        Task completed = new Deadline("submit report", LocalDate.of(2026, 9, 30));
        completed.setDone(true);

        assertNotEquals(incomplete, completed);
        assertNotEquals(completed, incomplete);
    }

    @Test
    public void event_sameDescriptionSchedulesAndStatus_tasksEqual() {
        Task first = new Event("project meeting", createSchedules(13));
        Task equivalent = new Event("project meeting", createSchedules(13));
        Task different = new Event("project meeting", createSchedules(20));

        assertEqualityBehavior(first, equivalent, different);
    }

    @Test
    public void event_differentDescription_tasksNotEqual() {
        Task first = new Event("project meeting", createSchedules(13));
        Task different = new Event("team meeting", createSchedules(13));

        assertNotEquals(first, different);
        assertNotEquals(different, first);
    }

    @Test
    public void event_differentCompletionStatus_tasksNotEqual() {
        Task incomplete = new Event("project meeting", createSchedules(13));
        Task completed = new Event("project meeting", createSchedules(13));
        completed.setDone(true);

        assertNotEquals(incomplete, completed);
        assertNotEquals(completed, incomplete);
    }

    @Test
    public void equals_differentTaskTypes_tasksNotEqual() {
        Task toDo = new ToDo("submit report");
        Task deadline = new Deadline("submit report", LocalDate.of(2026, 9, 30));
        Task event = new Event("submit report", createSchedules(13));

        assertNotEquals(toDo, deadline);
        assertNotEquals(deadline, toDo);
        assertNotEquals(toDo, event);
        assertNotEquals(event, toDo);
        assertNotEquals(deadline, event);
        assertNotEquals(event, deadline);
    }

    /**
     * Verifies equality behavior shared by all concrete task types.
     */
    private void assertEqualityBehavior(Task first, Task equivalent, Task different) {
        assertTrue(first.equals(first));
        assertEquals(first, equivalent);
        assertEquals(equivalent, first);
        assertNotEquals(first, different);
        assertNotEquals(first, null);
        assertNotEquals(first, new Object());
    }

    private ArrayList<TemporalPair> createSchedules(int startDay) {
        ArrayList<TemporalPair> schedules = new ArrayList<>();
        schedules.add(new TemporalPair(
                LocalDate.of(2026, 9, startDay), LocalDate.of(2026, 9, startDay + 1)));
        return schedules;
    }
}
