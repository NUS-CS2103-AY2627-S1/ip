package bobby;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Test;

/** Tests typed event dates and their display and storage representations. */
public class EventTest {

    /** Verifies that event input is parsed into LocalDate values. */
    @Test
    public void fromInput_isoDates_localDatesReturned() {
        Event event = Event.fromInput("project meeting", "2026-08-25", "2026-08-26");

        assertEquals(LocalDate.of(2026, 8, 25), event.getFrom());
        assertEquals(LocalDate.of(2026, 8, 26), event.getTo());
    }

    /** Verifies that event dates are formatted consistently for users and storage. */
    @Test
    public void representations_typedDates_formattedConsistently() {
        Event event = new Event("project meeting",
                LocalDate.of(2026, 8, 25), LocalDate.of(2026, 8, 26));

        assertEquals("[E][ ] project meeting (from: Aug 25 2026 to: Aug 26 2026)",
                event.toString());
        assertEquals("E | 0 | project meeting | 2026-08-25 | 2026-08-26 | NONE",
                event.toStorageString());
    }

    /** Verifies that event construction allows a same-day date range. */
    @Test
    public void constructor_sameStartAndEndDate_eventCreated() {
        Event event = new Event("same-day event",
                LocalDate.of(2026, 8, 25), LocalDate.of(2026, 8, 25));

        assertEquals(LocalDate.of(2026, 8, 25), event.getFrom());
        assertEquals(LocalDate.of(2026, 8, 25), event.getTo());
        assertEquals("[E][ ] same-day event (from: Aug 25 2026 to: Aug 25 2026)",
                event.toString());
    }

    /** Verifies that event construction still rejects a reversed date range. */
    @Test
    public void constructor_startAfterEnd_exceptionThrown() {
        assertThrows(IllegalArgumentException.class, () -> Event.fromInput("reversed",
                "2026-08-26", "2026-08-25"));
    }

    /** Verifies strict parsing rejects invalid and unsupported date values. */
    @Test
    public void fromInput_invalidDates_dateTimeParseExceptionThrown() {
        assertThrows(DateTimeParseException.class, () -> Event.fromInput(
                "invalid start", "2026-02-29", "2026-03-01"));
        assertThrows(DateTimeParseException.class, () -> Event.fromInput(
                "invalid end", "2026-08-25", "25-08-2026"));
    }

    /** Verifies event identity includes type, normalized description, and both dates. */
    @Test
    public void hasSameDetails_variedEvents_expectedResultReturned() {
        Event event = new Event("Project Meeting",
                LocalDate.of(2026, 8, 25), LocalDate.of(2026, 8, 26));

        assertTrue(event.hasSameDetails(new Event(" project   meeting ",
                LocalDate.of(2026, 8, 25), LocalDate.of(2026, 8, 26))));
        assertFalse(event.hasSameDetails(new Event("project meeting",
                LocalDate.of(2026, 8, 24), LocalDate.of(2026, 8, 26))));
        assertFalse(event.hasSameDetails(new Event("project meeting",
                LocalDate.of(2026, 8, 25), LocalDate.of(2026, 8, 27))));
        assertFalse(event.hasSameDetails(new ToDo("project meeting")));
        assertFalse(event.hasSameDetails(null));
    }
}
