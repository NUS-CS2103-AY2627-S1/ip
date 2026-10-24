package clara.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

// NOTE: AI-assisted test suite implementation. See CITATIONS.md [C-010].

/**
 * Unit tests for {@link Event}.
 */
public class EventTest {

    @Test
    // NOTE: AI-assisted task priority feature. See CITATIONS.md [C-018].
    public void toString_uncompletedEvent_correctFormat() {
        LocalDateTime from = LocalDateTime.of(2025, 11, 1, 14, 0);
        LocalDateTime to = LocalDateTime.of(2025, 11, 1, 16, 0);
        Event event = new Event("project meeting", from, to);

        assertFalse(event.isDone());
        assertEquals("project meeting", event.getTaskName());
        assertEquals(from, event.getFromTime());
        assertEquals(to, event.getToTime());
        assertEquals(
                "[E][ ] Medium project meeting (from: Nov 01 2025, 2:00 PM to: Nov 01 2025, 4:00 PM)",
                event.toString());
    }

    @Test
    public void toString_completedEvent_correctFormat() {
        LocalDateTime from = LocalDateTime.of(2025, 11, 1, 14, 0);
        LocalDateTime to = LocalDateTime.of(2025, 11, 1, 16, 0);
        Event event = new Event("project meeting", from, to);
        event.setDone(true);

        assertTrue(event.isDone());
        assertEquals(
                "[E][X] Medium project meeting (from: Nov 01 2025, 2:00 PM to: Nov 01 2025, 4:00 PM)",
                event.toString());
    }

    @Test
    public void setTimes_validTimes_timesUpdated() {
        LocalDateTime initialFrom = LocalDateTime.of(2025, 11, 1, 14, 0);
        LocalDateTime initialTo = LocalDateTime.of(2025, 11, 1, 16, 0);
        Event event = new Event("project meeting", initialFrom, initialTo);

        LocalDateTime newFrom = LocalDateTime.of(2025, 11, 1, 15, 0);
        LocalDateTime newTo = LocalDateTime.of(2025, 11, 1, 17, 0);
        event.setFromTime(newFrom);
        event.setToTime(newTo);

        assertEquals(newFrom, event.getFromTime());
        assertEquals(newTo, event.getToTime());
    }

    @Test
    // NOTE: AI-assisted assertion test. See CITATIONS.md [C-015].
    public void constructor_missingStartTime_assertionErrorThrown() {
        LocalDateTime endTime = LocalDateTime.of(2025, 11, 1, 16, 0);

        assertThrows(AssertionError.class, () -> new Event("project meeting", null, endTime));
    }
}
