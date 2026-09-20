package atom.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import atom.exception.AtomImpossibleDateException;
import atom.exception.AtomInvalidEventTimeException;

public class EventTest {

    @Test
    public void mark() {
        Event markedEvent = new Event("meeting", "2020-10-10 14:30", "2020-10-10 16:00");
        markedEvent.mark();
        assertEquals("[E][X] meeting (from: Oct 10 2020, 2:30 PM to: Oct 10 2020, 4:00 PM)", markedEvent.toString());
    }

    @Test
    public void unmark() {
        Event unmarkedEvent = new Event("meeting", "2020-10-10 14:30", "2020-10-10 16:00");
        unmarkedEvent.mark();
        unmarkedEvent.unmark();
        assertEquals("[E][ ] meeting (from: Oct 10 2020, 2:30 PM to: Oct 10 2020, 4:00 PM)", unmarkedEvent.toString());
    }

    @Test
    public void testStringConversion() {
        assertEquals("[E][ ] meeting (from: Oct 10 2020, 2:30 PM to: Oct 10 2020, 4:00 PM)",
                new Event("meeting", "2020-10-10 14:30", "2020-10-10 16:00").toString());
    }

    @Test
    public void testTimeConversion() {
        assertEquals("[E][ ] meeting (from: Oct 10 2020, 2:30 PM to: Oct 10 2020, 4:00 PM)",
                new Event("meeting", "2020-10-10 14:30", "2020-10-10 16:00").toString());
    }

    @Test
    void eventMustEndAfterItStarts() {
        assertThrows(AtomInvalidEventTimeException.class, () ->
                new Event("meeting", "2024-02-01 10:00", "2024-02-01 10:00"));
        assertThrows(AtomInvalidEventTimeException.class, () ->
                new Event("meeting", "2024-02-02", "2024-02-01"));
    }

    @Test
    void eventWithEndBeforeStart_hasSpecificErrorMessage() {
        AtomInvalidEventTimeException exception = assertThrows(AtomInvalidEventTimeException.class, () ->
                new Event("meeting", "2024-02-02", "2024-02-01"));
        assertEquals("Oh no! The end time must be after the start time.", exception.getMessage());
    }

    @Test
    void rejectsImpossibleDates() {
        assertThrows(AtomImpossibleDateException.class, () ->
                new Event("meeting", "2024-02-30 10:00", "2024-02-30 11:00"));
        assertThrows(AtomImpossibleDateException.class, () ->
                new Event("meeting", "2026-09-31 10:00", "2026-10-01 11:00"));
    }

    @Test
    void impossibleDate_hasSpecificErrorMessage() {
        AtomImpossibleDateException exception = assertThrows(AtomImpossibleDateException.class, () ->
                new Event("meeting", "2026-09-31", "2026-10-01"));
        assertEquals("Oh no! That date does not exist. Please enter a valid calendar date.",
                exception.getMessage());
    }

    @Test
    void dateWithThirtyThirdDay_isRejectedAsImpossibleDate() {
        assertThrows(AtomImpossibleDateException.class, () ->
                new Event("meeting", "2026-01-33", "2026-02-01"));
    }
}
