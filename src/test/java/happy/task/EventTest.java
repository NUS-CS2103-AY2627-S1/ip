package happy.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for the Event task class.
 */
public class EventTest {

    @Test
    public void toString_validDates_formattedCorrectly() {
        Event event = new Event("project meeting", "2026-10-10", "2026-10-12");
        assertEquals("[E][ ] project meeting (from: Oct 10 2026 to: Oct 12 2026)", event.toString());
    }

    @Test
    public void toString_rawStrings_fallbackToRawString() {
        Event event = new Event("party", "Monday", "Tuesday");
        assertEquals("[E][ ] party (from: Monday to: Tuesday)", event.toString());
    }

    @Test
    public void isOccurringOn_matchingFromDate_returnsTrue() {
        Event event = new Event("workshop", "2026-05-15", "2026-05-20");
        assertTrue(event.isOccurringOn(LocalDate.of(2026, 5, 15)));
        assertTrue(event.isOccurringOn(LocalDate.of(2026, 5, 18)));
        assertTrue(event.isOccurringOn(LocalDate.of(2026, 5, 20)));
        assertFalse(event.isOccurringOn(LocalDate.of(2026, 5, 21)));
    }

    @Test
    public void equals_sameDetails_returnsTrue() {
        Event event1 = new Event("meeting", "2026-10-10", "2026-10-12");
        Event event2 = new Event("meeting", "2026-10-10", "2026-10-12");
        assertTrue(event1.equals(event2));
    }

    @Test
    public void equals_differentTimes_returnsFalse() {
        Event event1 = new Event("meeting", "2026-10-10", "2026-10-12");
        Event event2 = new Event("meeting", "2026-10-10", "2026-10-15");
        assertFalse(event1.equals(event2));
    }
}
