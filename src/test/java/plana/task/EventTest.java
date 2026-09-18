package plana.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests whether an {@link Event} includes a supplied date.
 */
public class EventTest {
    private static final LocalDate EVENT_START = LocalDate.of(2026, 8, 31);
    private static final LocalDate EVENT_END = LocalDate.of(2026, 9, 2);

    /**
     * Verifies that an event includes its start date, dates within its range,
     * and its end date.
     */
    @Test
    public void occursOn_dateWithinOrAtBoundary_trueReturned() {
        Event event = new Event("team planning", EVENT_START, EVENT_END);

        assertTrue(event.occursOn(EVENT_START));
        assertTrue(event.occursOn(EVENT_START.plusDays(1)));
        assertTrue(event.occursOn(EVENT_END));
    }

    /**
     * Verifies that an event does not include dates immediately before or
     * immediately after its date range.
     */
    @Test
    public void occursOn_dateOutsideRange_falseReturned() {
        Event event = new Event("team planning", EVENT_START, EVENT_END);

        assertFalse(event.occursOn(EVENT_START.minusDays(1)));
        assertFalse(event.occursOn(EVENT_END.plusDays(1)));
    }

    /**
     * Verifies that an event with equal start and end dates is rejected.
     */
    @Test
    public void constructor_equalBoundaryDates_illegalArgumentExceptionThrown() {
        assertThrows(IllegalArgumentException.class, () ->
                new Event("presentation", EVENT_START, EVENT_START));
    }

    /**
     * Verifies that an incomplete event is displayed with its type, status,
     * description, and formatted date range.
     */
    @Test
    public void toString_incompleteEvent_formattedDetailsReturned() {
        Event event = new Event("team planning", EVENT_START, EVENT_END);

        assertEquals("[E][ ] team planning (from: Aug 31 2026 to: Sep 02 2026)", event.toString());
    }

    /**
     * Verifies that the display format reflects a completed event.
     */
    @Test
    public void toString_completedEvent_doneStatusReturned() {
        Event event = new Event("team planning", EVENT_START, EVENT_END);
        event.markAsDone();

        assertEquals("[E][X] team planning (from: Aug 31 2026 to: Sep 02 2026)", event.toString());
    }

    /**
     * Verifies that an incomplete event is serialized with its type, status,
     * dates, and escaped description.
     */
    @Test
    public void toStorageString_incompleteEvent_storageRecordReturned() {
        Event event = new Event("review | plan", EVENT_START, EVENT_END);

        assertEquals("E | 0 | review \\| plan | 2026-08-31 | 2026-09-02", event.toStorageString());
    }

    /**
     * Verifies that the storage format reflects a completed event.
     */
    @Test
    public void toStorageString_completedEvent_doneStatusReturned() {
        Event event = new Event("team planning", EVENT_START, EVENT_END);
        event.markAsDone();

        assertEquals("E | 1 | team planning | 2026-08-31 | 2026-09-02", event.toStorageString());
    }

    @Test
    public void constructor_missingBoundaryDate_assertionFails() {
        assertThrows(AssertionError.class, () -> new Event("team planning", EVENT_START, null));
    }
}
