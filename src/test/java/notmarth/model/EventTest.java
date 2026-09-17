package notmarth.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/** Tests event range validation, date matching, storage values, and display text. */
class EventTest {
    /** Verifies that a multi-day event matches every date in its inclusive range. */
    @Test
    void isOccurringOn_eventSpanningSeveralDates_matchesEveryInclusiveDate() {
        Event event = new Event("planning", "2019-10-14 1400", "2019-10-16 1600");

        assertFalse(event.isOccurringOn(LocalDate.of(2019, 10, 13)));
        assertTrue(event.isOccurringOn(LocalDate.of(2019, 10, 14)));
        assertTrue(event.isOccurringOn(LocalDate.of(2019, 10, 15)));
        assertTrue(event.isOccurringOn(LocalDate.of(2019, 10, 16)));
        assertFalse(event.isOccurringOn(LocalDate.of(2019, 10, 17)));
        assertEquals("2019-10-14T14:00", event.getFromForStorage());
        assertEquals("2019-10-16T16:00", event.getToForStorage());
        assertEquals("[E][ ] planning (from: Oct 14 2019 2:00 PM to: Oct 16 2019 4:00 PM)",
                event.toString());
    }

    /** Verifies that date-only events preserve date-only display and storage formats. */
    @Test
    void eventCreation_dateOnlyInput_preservesDateOnlyFormatting() {
        Event event = new Event("conference", LocalDate.of(2019, 10, 15), LocalDate.of(2019, 10, 16));

        assertEquals(LocalDateTime.of(2019, 10, 15, 0, 0), event.getFrom());
        assertEquals(LocalDateTime.of(2019, 10, 16, 0, 0), event.getTo());
        assertEquals("2019-10-15", event.getFromForStorage());
        assertEquals("2019-10-16", event.getToForStorage());
        assertEquals("[E][ ] conference (from: Oct 15 2019 to: Oct 16 2019)", event.toString());
    }

    /** Verifies that an event ending before its start is rejected. */
    @Test
    void eventCreation_endBeforeStart_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> new Event("backwards", "2019-02-05", "2019-01-04"));
        assertThrows(IllegalArgumentException.class,
                () -> new Event("backwards", LocalDateTime.of(2019, 2, 5, 14, 0),
                        LocalDateTime.of(2019, 2, 5, 13, 0)));
        assertThrows(IllegalArgumentException.class,
                () -> new Event("same time", LocalDateTime.of(2019, 2, 5, 14, 0),
                        LocalDateTime.of(2019, 2, 5, 14, 0)));
        assertThrows(IllegalArgumentException.class,
                () -> new Event("missing", (LocalDateTime) null, LocalDateTime.now()));
        assertThrows(IllegalArgumentException.class,
                () -> new Event("missing", LocalDate.now(), null));
    }
}
