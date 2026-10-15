package dave.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.Test;

import dave.exception.DaveCommandException;

/**
 * Unit tests for the {@link Event} class.
 */
public class EventTest {

    @Test
    public void canSnooze_default_returnsTrue() {
        Event event = new Event("project meeting",
                LocalDateTime.of(2026, 10, 1, 14, 0),
                LocalDateTime.of(2026, 10, 1, 16, 0));
        assertTrue(event.canSnooze());
    }

    @Test
    public void constructor_startAfterEnd_throwsDaveCommandException() {
        DaveCommandException e = assertThrows(DaveCommandException.class, () ->
                new Event("meeting",
                        LocalDateTime.of(2026, 10, 5, 16, 0),
                        LocalDateTime.of(2026, 10, 5, 14, 0)));
        assertTrue(e.getMessage().contains("strictly earlier than end date/time"));
    }

    @Test
    public void constructor_startEqualToEnd_throwsDaveCommandException() {
        DaveCommandException e = assertThrows(DaveCommandException.class, () ->
                new Event("meeting",
                        LocalDateTime.of(2026, 10, 5, 14, 0),
                        LocalDateTime.of(2026, 10, 5, 14, 0)));
        assertTrue(e.getMessage().contains("strictly earlier than end date/time"));
    }

    @Test
    public void reschedule_newBoundaries_updatesFromAndTo() {
        Event event = new Event("project meeting",
                LocalDateTime.of(2026, 10, 1, 14, 0),
                LocalDateTime.of(2026, 10, 1, 16, 0));
        LocalDateTime newFrom = LocalDateTime.of(2026, 10, 5, 10, 0);
        LocalDateTime newTo = LocalDateTime.of(2026, 10, 5, 12, 0);

        event.reschedule(newFrom, true, newTo, true);
        assertEquals(newFrom, event.getFrom());
        assertEquals(newTo, event.getTo());
        assertTrue(event.hasFromTime());
        assertTrue(event.hasToTime());
        assertEquals("[E][ ] project meeting (from: Oct 05 2026 10:00 to: Oct 05 2026 12:00)", event.toString());
    }

    @Test
    public void reschedule_startAfterEnd_throwsDaveCommandException() {
        Event event = new Event("project meeting",
                LocalDateTime.of(2026, 10, 1, 14, 0),
                LocalDateTime.of(2026, 10, 1, 16, 0));

        assertThrows(DaveCommandException.class, () ->
                event.reschedule(LocalDateTime.of(2026, 10, 5, 16, 0), true,
                        LocalDateTime.of(2026, 10, 5, 14, 0), true));
    }

    @Test
    public void snoozeBy_positiveHours_shiftsBothFromAndToPreservingDuration() {
        Event event = new Event("project meeting",
                LocalDateTime.of(2026, 10, 1, 14, 0),
                LocalDateTime.of(2026, 10, 1, 16, 0));

        event.snoozeBy(2, ChronoUnit.HOURS);
        assertEquals(LocalDateTime.of(2026, 10, 1, 16, 0), event.getFrom());
        assertEquals(LocalDateTime.of(2026, 10, 1, 18, 0), event.getTo());
    }

    @Test
    public void toFileFormat_snoozedEvent_serializesUpdatedBoundaries() {
        Event event = new Event("project meeting",
                LocalDateTime.of(2026, 10, 1, 14, 0),
                LocalDateTime.of(2026, 10, 1, 16, 0));
        event.snoozeBy(1, ChronoUnit.DAYS);

        assertEquals("E | 0 | project meeting | 2026-10-02 14:00 | 2026-10-02 16:00", event.toFileFormat());
    }

    @Test
    public void isSameTask_sameEventDetails_returnsTrue() {
        Event event1 = new Event("project meeting",
                LocalDateTime.of(2026, 10, 1, 14, 0),
                LocalDateTime.of(2026, 10, 1, 16, 0));
        Event event2 = new Event("project meeting",
                LocalDateTime.of(2026, 10, 1, 14, 0),
                LocalDateTime.of(2026, 10, 1, 16, 0));
        assertTrue(event1.isSameTask(event2));
    }

    @Test
    public void isSameTask_differentEventDetails_returnsFalse() {
        Event event1 = new Event("project meeting",
                LocalDateTime.of(2026, 10, 1, 14, 0),
                LocalDateTime.of(2026, 10, 1, 16, 0));
        Event event2 = new Event("project meeting",
                LocalDateTime.of(2026, 10, 2, 14, 0),
                LocalDateTime.of(2026, 10, 2, 16, 0));
        assertFalse(event1.isSameTask(event2));
    }
}
