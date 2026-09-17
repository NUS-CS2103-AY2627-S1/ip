package crystal.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests event normalization, date ranges, and representations.
 */
public class EventTest {
    /** Verifies normalized display and storage forms for event date-times. */
    @Test
    public void representations_supportedDateTimes_normalizedForDisplayAndStorage() {
        Event event = new Event("conference", "2Oct26 6am", "3 October 2026 18:30");

        assertEquals("E | 0 | conference | 02 Oct 2026 0600 | 03 Oct 2026 1830",
                event.toDataString());
        assertEquals("[E][ ] conference (from: 02 Oct 2026 0600 to: 03 Oct 2026 1830)",
                event.toString());
    }

    /** Verifies that valid event ranges include both endpoints and intervening dates. */
    @Test
    public void occursOn_validDateRange_matchesInclusiveRangeOnly() {
        Event event = new Event("conference", "1 Dec 2026", "3 Dec 2026");

        assertTrue(event.occursOn(LocalDate.of(2026, 12, 1)));
        assertTrue(event.occursOn(LocalDate.of(2026, 12, 2)));
        assertTrue(event.occursOn(LocalDate.of(2026, 12, 3)));
        assertFalse(event.occursOn(LocalDate.of(2026, 11, 30)));
        assertFalse(event.occursOn(LocalDate.of(2026, 12, 4)));
    }

    /** Verifies that reversed event ranges match their endpoints without inventing a range. */
    @Test
    public void occursOn_reversedDateRange_matchesEndpointsOnly() {
        Event event = new Event("conference", "3 Dec 2026", "1 Dec 2026");

        assertTrue(event.occursOn(LocalDate.of(2026, 12, 3)));
        assertTrue(event.occursOn(LocalDate.of(2026, 12, 1)));
        assertFalse(event.occursOn(LocalDate.of(2026, 12, 2)));
    }

    /** Verifies date matching when only one event endpoint contains a date. */
    @Test
    public void occursOn_oneDatedEndpoint_matchesThatEndpoint() {
        Event event = new Event("workshop", "Monday", "2 Dec 2026");

        assertTrue(event.occursOn(LocalDate.of(2026, 12, 2)));
        assertFalse(event.occursOn(LocalDate.of(2026, 12, 3)));
    }

    /** Verifies that completed events persist their completed status. */
    @Test
    public void toDataString_completedEvent_includesCompletedStatus() {
        Event event = new Event("workshop", "1700", "2000");
        event.markAsDone();

        assertEquals("E | 1 | workshop | 1700 | 2000", event.toDataString());
    }
}
