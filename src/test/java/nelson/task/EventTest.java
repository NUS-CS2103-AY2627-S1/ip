package nelson.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import nelson.exception.NelsonException;

/** Tests the formatting and validation behavior of {@link Event}. */
public class EventTest {
    /** Verifies that a new event is displayed with its date range. */
    @Test
    public void toString_newEvent_displaysDescriptionAndDateRange() throws NelsonException {
        Event event = new Event("team meeting", "2026-08-06", "2026-08-07");

        assertEquals("[E][ ] team meeting (from: Aug 6 2026 to: Aug 7 2026)", event.toString());
    }

    /** Verifies that an event is saved with its type, status, description, and dates. */
    @Test
    public void toStorageString_newEvent_returnsStorageFormat() throws NelsonException {
        Event event = new Event("team meeting", "2026-08-06", "2026-08-07");

        assertEquals("E | 0 | team meeting | 2026-08-06 | 2026-08-07", event.toStorageString());
    }

    /** Verifies that marking an event done changes its displayed and stored status. */
    @Test
    public void markAsDone_completedEvent_updatesStatusInFormats() throws NelsonException {
        Event event = new Event("team meeting", "2026-08-06", "2026-08-07");

        event.markAsDone();

        assertEquals("[E][X] team meeting (from: Aug 6 2026 to: Aug 7 2026)", event.toString());
        assertEquals("E | 1 | team meeting | 2026-08-06 | 2026-08-07", event.toStorageString());
    }

    /** Verifies that an event rejects dates that do not use the required format. */
    @Test
    public void constructor_invalidDate_throwsNelsonException() {
        assertThrows(NelsonException.class, () -> new Event("team meeting", "06-08-2026", "2026-08-07"));
    }

    /** Verifies that an event cannot end on its start date. */
    @Test
    public void constructor_sameStartAndEndDate_throwsNelsonException() {
        assertThrows(NelsonException.class, () -> new Event("team meeting", "2026-08-06", "2026-08-06"));
    }

    /** Verifies that an event cannot end before its start date. */
    @Test
    public void constructor_endBeforeStart_throwsNelsonException() {
        assertThrows(NelsonException.class, () -> new Event("team meeting", "2026-08-07", "2026-08-06"));
    }
}
