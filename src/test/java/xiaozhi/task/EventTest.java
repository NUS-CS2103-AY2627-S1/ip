package xiaozhi.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests {@link Event}'s own behavior on top of the shared {@link Task}
 * behavior already covered by {@link TodoTest}: its type icon, and how its
 * two dates are woven into {@link Event#toString} and {@link Event#toSaveFormat}.
 */
public class EventTest {

    @Test
    public void getTypeIcon_returnsE() {
        Event event = new Event("project meeting", LocalDate.of(2019, 8, 6), LocalDate.of(2019, 8, 7));

        assertEquals("E", event.getTypeIcon());
    }

    @Test
    public void toString_notDone_showsFriendlyDatesInFromToClause() {
        Event event = new Event("project meeting", LocalDate.of(2019, 8, 6), LocalDate.of(2019, 8, 7));

        assertEquals("[E][ ] project meeting (from: Aug 06 2019 to: Aug 07 2019)", event.toString());
    }

    @Test
    public void toString_done_showsXStatusIcon() {
        Event event = new Event("project meeting", LocalDate.of(2019, 8, 6), LocalDate.of(2019, 8, 7));
        event.markAsDone();

        assertEquals("[E][X] project meeting (from: Aug 06 2019 to: Aug 07 2019)", event.toString());
    }

    @Test
    public void toSaveFormat_appendsBothIsoDatesAfterBaseFields() {
        Event event = new Event("project meeting", LocalDate.of(2019, 8, 6), LocalDate.of(2019, 8, 7));

        assertEquals("E | 0 | project meeting | 2019-08-06 | 2019-08-07", event.toSaveFormat());
    }

    @Test
    public void toSaveFormat_done_usesOneForDoneFlag() {
        Event event = new Event("project meeting", LocalDate.of(2019, 8, 6), LocalDate.of(2019, 8, 7));
        event.markAsDone();

        assertEquals("E | 1 | project meeting | 2019-08-06 | 2019-08-07", event.toSaveFormat());
    }

    @Test
    public void equals_sameDescriptionAndDates_isEqualRegardlessOfDoneStatus() {
        Event first = new Event("project meeting", LocalDate.of(2019, 8, 6), LocalDate.of(2019, 8, 7));
        Event second = new Event("project meeting", LocalDate.of(2019, 8, 6), LocalDate.of(2019, 8, 7));
        second.markAsDone();

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    public void equals_differentToDate_isNotEqual() {
        Event first = new Event("project meeting", LocalDate.of(2019, 8, 6), LocalDate.of(2019, 8, 7));
        Event second = new Event("project meeting", LocalDate.of(2019, 8, 6), LocalDate.of(2019, 8, 8));

        assertFalse(first.equals(second));
    }

    @Test
    public void equals_notAnEvent_isNotEqual() {
        Event event = new Event("project meeting", LocalDate.of(2019, 8, 6), LocalDate.of(2019, 8, 7));

        assertFalse(event.equals(new Todo("project meeting")));
    }
}
