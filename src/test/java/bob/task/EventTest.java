package bob.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;

/** Tests the user-facing representation of event tasks. */
class EventTest {

    @Test
    void toString_dateAndTimeRange_includesFormattedStartAndEnd() {
        Event event = new Event("project meeting",
                new TaskDateTime(LocalDate.of(2019, 12, 2), LocalTime.of(18, 0)),
                new TaskDateTime(LocalDate.of(2019, 12, 2), LocalTime.of(19, 30)));

        assertEquals("[E][ ] project meeting (from: Dec 02 2019 18:00 to: Dec 02 2019 19:30)",
                event.toString());
    }

    @Test
    void toString_dateOnlyRange_includesFormattedStartAndEnd() {
        TaskDateTime from = new TaskDateTime(LocalDate.of(2019, 12, 2), null);
        TaskDateTime to = new TaskDateTime(LocalDate.of(2019, 12, 4), null);
        Event event = new Event("camp", from, to);

        assertEquals("[E][ ] camp (from: Dec 02 2019 to: Dec 04 2019)", event.toString());
        assertSame(from, event.getFrom());
        assertSame(to, event.getTo());
    }

    @Test
    void constructor_nullStartDate_illegalArgumentExceptionThrown() {
        TaskDateTime end = new TaskDateTime(LocalDate.of(2019, 12, 4), null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new Event("camp", null, end));

        assertEquals("An event needs both a start and an end date-time.", exception.getMessage());
    }

    @Test
    void constructor_nullEndDate_illegalArgumentExceptionThrown() {
        TaskDateTime start = new TaskDateTime(LocalDate.of(2019, 12, 2), null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new Event("camp", start, null));

        assertEquals("An event needs both a start and an end date-time.", exception.getMessage());
    }
}
