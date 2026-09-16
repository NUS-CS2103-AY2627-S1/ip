package miku.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/** Tests the task-model assumptions checked during development. */
class TaskInvariantTest {

    @Test
    void todo_nullDescription_assertionThrown() {
        assertThrows(AssertionError.class, () -> new Todo(null));
    }

    @Test
    void deadline_nullDateTime_assertionThrown() {
        assertThrows(AssertionError.class, () -> new Deadline("submit work", null, false));
    }

    @Test
    void event_nullEndpoint_assertionThrown() {
        LocalDateTime dateTime = LocalDateTime.of(2026, 9, 8, 10, 0);

        assertThrows(AssertionError.class, () -> new Event("concert", null, false, dateTime, true));
        assertThrows(AssertionError.class, () -> new Event("concert", dateTime, true, null, false));
    }

    @Test
    void scheduledTasks_scheduleAccessors_returnProvidedValues() {
        LocalDateTime startDateTime = LocalDateTime.of(2026, 9, 8, 10, 0);
        LocalDateTime dueDateTime = LocalDateTime.of(2026, 9, 9, 17, 0);
        LocalDateTime endDateTime = LocalDateTime.of(2026, 9, 10, 12, 0);
        Deadline deadline = new Deadline("submit work", dueDateTime, true);
        Event event = new Event("concert", startDateTime, true, endDateTime, false);

        assertEquals(dueDateTime, deadline.getDueDateTime());
        assertTrue(deadline.hasDueTime());
        assertEquals(startDateTime, event.getStartDateTime());
        assertEquals(endDateTime, event.getEndDateTime());
        assertTrue(event.hasStartTime());
    }
}
