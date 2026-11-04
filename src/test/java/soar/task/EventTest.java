package soar.task;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests calendar-date matching for dated and free-form events.
 */
public class EventTest {
    /** Verifies that a fully dated event matches its inclusive date range. */
    @Test
    public void occursOn_fullyDatedRange_matchesInclusiveBoundaries() {
        Event event = new Event("conference", "2019-10-14T09:00", "2019-10-16T17:00");

        assertFalse(event.occursOn(LocalDate.of(2019, 10, 13)));
        assertTrue(event.occursOn(LocalDate.of(2019, 10, 14)));
        assertTrue(event.occursOn(LocalDate.of(2019, 10, 15)));
        assertTrue(event.occursOn(LocalDate.of(2019, 10, 16)));
        assertFalse(event.occursOn(LocalDate.of(2019, 10, 17)));
    }

    /** Verifies that one dated endpoint matches only that endpoint's date. */
    @Test
    public void occursOn_oneDatedEndpoint_matchesOnlyKnownDate() {
        Event datedStart = new Event("starts", "2019-10-15", "sometime");
        Event datedEnd = new Event("ends", "sometime", "16/10/2019");

        assertTrue(datedStart.occursOn(LocalDate.of(2019, 10, 15)));
        assertFalse(datedStart.occursOn(LocalDate.of(2019, 10, 16)));
        assertTrue(datedEnd.occursOn(LocalDate.of(2019, 10, 16)));
        assertFalse(datedEnd.occursOn(LocalDate.of(2019, 10, 15)));
    }

    /** Verifies that an event with no parseable dates never matches a date query. */
    @Test
    public void occursOn_freeFormTimes_returnsFalse() {
        Event event = new Event("lunch", "noon", "1pm");

        assertFalse(event.occursOn(LocalDate.of(2019, 10, 15)));
    }
}
