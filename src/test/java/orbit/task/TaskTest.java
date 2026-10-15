package orbit.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests behavior shared by tasks and specialized by each task subtype.
 */
class TaskTest {
    /**
     * Verifies that marking and unmarking a task update both display and storage forms.
     */
    @Test
    void markAndUnmark_todo_updatesDisplayAndStorageStatus() {
        Task task = new Todo("check telemetry");

        assertEquals("[T][X] check telemetry", task.mark());
        assertEquals("T | 1 | check telemetry", task.toDataString());
        assertEquals("[T][ ] check telemetry", task.unmark());
        assertEquals("T | 0 | check telemetry", task.toDataString());
    }

    /**
     * Verifies polymorphic scheduling for todos, deadlines, and date-range events.
     */
    @Test
    void occursOn_differentTaskTypes_usesSubtypeSchedulingRules() {
        LocalDate targetDate = LocalDate.parse("2026-10-09");
        Task todo = new Todo("calibrate telescope");
        Task deadline = new Deadline("transmit report", targetDate);
        Task event = new Event("lunar observation",
                targetDate.minusDays(1), targetDate.plusDays(1));

        assertFalse(todo.occursOn(targetDate));
        assertTrue(deadline.occursOn(targetDate));
        assertTrue(event.occursOn(targetDate));
        assertFalse(event.occursOn(targetDate.plusDays(2)));
    }

    /**
     * Verifies that an event cannot be created with an end date before its start date.
     */
    @Test
    void constructor_eventWithReversedDates_throwsIllegalArgumentException() {
        LocalDate start = LocalDate.parse("2026-10-10");
        LocalDate end = LocalDate.parse("2026-10-09");

        assertThrows(IllegalArgumentException.class, () ->
                new Event("lunar observation", start, end));
    }
}
