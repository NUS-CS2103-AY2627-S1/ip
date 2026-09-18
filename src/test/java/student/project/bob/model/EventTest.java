package student.project.bob.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Test;

/**
 * Tests event parsing, state, and display formatting.
 */
public class EventTest {
    /**
     * Verifies that date-only event endpoints are parsed and formatted correctly.
     */
    @Test
    public void constructor_dateOnlyEvent_storesAndFormatsDateRange() {
        Event event = new Event("team sync", "2019-10-16", "2019-10-17");

        assertEquals("team sync", event.getDescription());
        assertEquals(LocalDate.of(2019, 10, 16), event.getFrom());
        assertEquals(LocalDate.of(2019, 10, 17), event.getTo());
        assertEquals("[E][ ] team sync (from: Wed, Oct 16 2019 to: Thu, Oct 17 2019)", event.toString());
    }

    /**
     * Verifies that date-time event endpoints preserve their times and seconds in the display.
     */
    @Test
    public void constructor_dateTimeEvent_storesAndFormatsDateTimeRange() {
        Event event = new Event("team sync", "2019-10-16T09:30", "2019-10-16T10:45:05");

        assertEquals(LocalDateTime.of(2019, 10, 16, 9, 30), event.getFrom());
        assertEquals(LocalDateTime.of(2019, 10, 16, 10, 45, 5), event.getTo());
        assertEquals("[E][ ] team sync (from: Wed, Oct 16 2019 09:30 to: Wed, Oct 16 2019 10:45:05)", event.toString());
    }

    /**
     * Verifies that a completed event includes its completed status in the display.
     */
    @Test
    public void toString_completedEvent_includesCompletedStatus() {
        Event event = new Event("team sync", "2019-10-16", "2019-10-17");

        event.setDone();

        assertEquals("[E][X] team sync (from: Wed, Oct 16 2019 to: Thu, Oct 17 2019)", event.toString());
    }

    /**
     * Verifies that an invalid event endpoint is rejected during construction.
     */
    @Test
    public void constructor_invalidEventEndpoint_throwsDateTimeParseException() {
        assertThrows(DateTimeParseException.class, () -> new Event("team sync", "not-a-date", "2019-10-17"));
        assertThrows(DateTimeParseException.class, () -> new Event("team sync", "2019-10-16", "not-a-date"));
    }
}
