package task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import exception.CbtException;

/** Tests event invariants and inclusive date-range behavior. */
public class EventTest {
    private static final LocalDateTime START = LocalDateTime.of(2026, 8, 25, 10, 0);
    private static final LocalDateTime END = LocalDateTime.of(2026, 8, 27, 18, 0);

    /** Verifies that an event whose start follows its end is rejected. */
    @Test
    public void constructor_startAfterEnd_exceptionThrown() {
        assertThrows(CbtException.class, () -> new Event("conference", END, START));
    }

    /** Verifies that an event cannot start and end at the same instant. */
    @Test
    public void constructor_startEqualsEnd_exceptionThrown() {
        assertThrows(CbtException.class, () -> new Event("instant event", START, START));
    }

    /** Verifies inclusive matching on an event's boundary and intermediate dates. */
    @Test
    public void occursOn_boundaryAndMiddleDates_trueReturned() throws CbtException {
        Event event = new Event("conference", START, END);

        assertTrue(event.occursOn(LocalDate.of(2026, 8, 25)));
        assertTrue(event.occursOn(LocalDate.of(2026, 8, 26)));
        assertTrue(event.occursOn(LocalDate.of(2026, 8, 27)));
    }

    /** Verifies that dates outside an event's range do not match. */
    @Test
    public void occursOn_datesOutsideRange_falseReturned() throws CbtException {
        Event event = new Event("conference", START, END);

        assertFalse(event.occursOn(LocalDate.of(2026, 8, 24)));
        assertFalse(event.occursOn(LocalDate.of(2026, 8, 28)));
    }

    /** Verifies that an event uses its start time for chronological sorting. */
    @Test
    public void getChronologicalDateTime_event_startTimeReturned() throws CbtException {
        Event event = new Event("conference", START, END);

        assertEquals(START, event.getChronologicalDateTime().orElseThrow());
    }

    /** Verifies event display, serialization, and duplicate identity. */
    @Test
    public void formattingAndIdentity_variedEvents_expectedValuesReturned() throws CbtException {
        Event event = new Event("conference", START, END);
        Event caseVariant = new Event("CONFERENCE", START, END);
        Event differentEnd = new Event("conference", START, END.plusMinutes(1));

        assertEquals("[E][ ] conference (from: Aug 25 2026, 10:00am to: Aug 27 2026, 6:00pm)",
                event.toString());
        event.markAsDone();
        assertEquals("EVENT | 1 | conference | 25/08/2026 1000 | 27/08/2026 1800",
                event.toFileFormat());
        assertTrue(event.hasSameDetails(caseVariant));
        assertFalse(event.hasSameDetails(differentEnd));
        assertFalse(event.hasSameDetails(new Todo("conference")));
    }
}
