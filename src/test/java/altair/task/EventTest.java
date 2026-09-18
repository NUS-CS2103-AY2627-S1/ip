package altair.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link Event}.
 *
 * <p>{@code Event} overrides {@link Event#toString()} and
 * {@link Event#toFileString()} to append a start date and an end date. The
 * tests check the {@code E} type marker, the {@code (from: ... to: ...)}
 * display format, and the {@code start - end} layout used in the save
 * file.</p>
 */
public class EventTest {

    /** Fixed dates keep the expected output stable regardless of when the test runs. */
    private static final LocalDate FROM = LocalDate.of(2019, 10, 15);
    private static final LocalDate TO = LocalDate.of(2019, 10, 20);

    @Test
    public void toString_newEvent_showsTypeStatusAndDisplayDateRange() {
        Event event = new Event("project meeting", FROM, TO);
        assertEquals("[E][ ] project meeting (from: Oct 15 2019 to: Oct 20 2019)", event.toString());
    }

    @Test
    public void toString_doneEvent_showsXStatus() {
        Event event = new Event("project meeting", FROM, TO);
        event.markAsDone();
        assertEquals("[E][X] project meeting (from: Oct 15 2019 to: Oct 20 2019)", event.toString());
    }

    @Test
    public void toString_sameStartAndEndDate_showsThatDateTwice() {
        Event event = new Event("all-day workshop", FROM, FROM);
        assertEquals("[E][ ] all-day workshop (from: Oct 15 2019 to: Oct 15 2019)", event.toString());
    }

    @Test
    public void isDuplicateOf_sameDescriptionAndDates_returnsTrue() {
        Event first = new Event("project meeting", FROM, TO);
        Event second = new Event("project meeting", FROM, TO);
        assertTrue(first.isDuplicateOf(second));
    }

    @Test
    public void isDuplicateOf_sameDescriptionDifferentDates_returnsFalse() {
        // A different date range makes this a different commitment, not a duplicate.
        Event first = new Event("project meeting", FROM, TO);
        Event second = new Event("project meeting", FROM, LocalDate.of(2019, 10, 21));
        assertFalse(first.isDuplicateOf(second));
    }

    @Test
    public void toFileString_newEvent_appendsIsoDatesJoinedByDash() {
        Event event = new Event("project meeting", FROM, TO);
        assertEquals("E | 0 | project meeting | 2019-10-15 - 2019-10-20", event.toFileString());
    }

    @Test
    public void toFileString_doneEvent_usesOneStatus() {
        Event event = new Event("project meeting", FROM, TO);
        event.markAsDone();
        assertEquals("E | 1 | project meeting | 2019-10-15 - 2019-10-20", event.toFileString());
    }
}
