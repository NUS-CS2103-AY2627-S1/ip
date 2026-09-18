package adrian;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/**
 * Tests the display and storage representations of {@link Event} objects.
 */
public class EventTest {

    /**
     * Verifies the display representation of an incomplete event.
     */
    @Test
    public void toString_unmarkedEvent_correctString() {
        LocalDateTime startDateTime = LocalDateTime.of(2026, 8, 6, 14, 0);
        LocalDateTime endDateTime = LocalDateTime.of(2026, 8, 6, 16, 0);
        Event event = new Event("project meeting", startDateTime, endDateTime);

        assertEquals(
                "[E][ ] project meeting (from: Aug 06 2026, 2:00 pm"
                        + " to: Aug 06 2026, 4:00 pm)",
                event.toString());
    }

    /**
     * Verifies the display representation of a completed event.
     */
    @Test
    public void toString_markedEvent_correctString() {
        LocalDateTime startDateTime = LocalDateTime.of(2026, 8, 6, 14, 0);
        LocalDateTime endDateTime = LocalDateTime.of(2026, 8, 6, 16, 0);
        Event event = new Event("project meeting", startDateTime, endDateTime);
        event.markAsDone();

        assertEquals(
                "[E][X] project meeting (from: Aug 06 2026, 2:00 pm"
                        + " to: Aug 06 2026, 4:00 pm)",
                event.toString());
    }

    /**
     * Verifies the storage representation of an incomplete event.
     */
    @Test
    public void toDataString_unmarkedEvent_correctString() {
        LocalDateTime startDateTime = LocalDateTime.of(2026, 8, 6, 14, 0);
        LocalDateTime endDateTime = LocalDateTime.of(2026, 8, 6, 16, 0);
        Event event = new Event("project meeting", startDateTime, endDateTime);

        assertEquals(
                "E | 0 | project meeting | 2026-08-06T14:00 | 2026-08-06T16:00",
                event.toDataString());
    }

    /**
     * Verifies that an event cannot end at or before its start time.
     */
    @Test
    public void constructor_endNotAfterStart_throwsException() {
        LocalDateTime startDateTime = LocalDateTime.of(2026, 8, 6, 14, 0);

        assertThrows(
                IllegalArgumentException.class,
                () -> new Event("project meeting", startDateTime, startDateTime));
        assertThrows(
                IllegalArgumentException.class,
                () -> new Event("project meeting", startDateTime, startDateTime.minusMinutes(1)));
    }
}
