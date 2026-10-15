package minty.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests event date matching, persistence formatting, and display formatting.
 */
public class EventTest {

    private static final LocalDate START_DATE = LocalDate.of(2026, 9, 10);
    private static final LocalDate END_DATE = LocalDate.of(2026, 9, 12);

    @Test
    public void occursOn_dateWithinEventPeriod_returnsTrue() {
        Event event = new Event("Conference", START_DATE, END_DATE);

        assertTrue(event.occursOn(LocalDate.of(2026, 9, 11)));
    }

    @Test
    public void occursOn_dateBeforeEvent_returnsFalse() {
        Event event = new Event("Conference", START_DATE, END_DATE);

        assertFalse(event.occursOn(LocalDate.of(2026, 9, 9)));
    }

    @Test
    public void occursOn_eventStartDate_returnsTrue() {
        Event event = new Event("Conference", START_DATE, END_DATE);

        assertTrue(event.occursOn(START_DATE));
    }

    @Test
    public void occursOn_eventEndDate_returnsTrue() {
        Event event = new Event("Conference", START_DATE, END_DATE);

        assertTrue(event.occursOn(END_DATE));
    }

    @Test
    public void occursOn_dateAfterEvent_returnsFalse() {
        Event event = new Event("Conference", START_DATE, END_DATE);

        assertFalse(event.occursOn(LocalDate.of(2026, 9, 13)));
    }

    @Test
    public void constructor_endBeforeStart_throwsAssertionError() {
        assertThrows(AssertionError.class, () ->
                new Event("Conference", END_DATE, START_DATE));
    }

    @Test
    public void toDataString_incompleteEvent_returnsSerializedEvent() {
        Event event = new Event("Conference", START_DATE, END_DATE);

        assertEquals("E | 0 | Conference | 2026-09-10 | 2026-09-12",
                event.toDataString());
    }

    @Test
    public void toDataString_completedEvent_returnsCompletedStatus() {
        Event event = new Event("Conference", START_DATE, END_DATE);
        event.markAsDone();

        assertEquals("E | 1 | Conference | 2026-09-10 | 2026-09-12",
                event.toDataString());
    }

    @Test
    public void toDataString_descriptionWithSpecialCharacters_escapesCharacters() {
        Event event = new Event("Plan | route \\ home", START_DATE, END_DATE);

        assertEquals("E | 0 | Plan \\| route \\\\ home | 2026-09-10 | 2026-09-12",
                event.toDataString());
    }

    @Test
    public void toString_incompleteEvent_returnsFormattedEvent() {
        Event event = new Event("Conference", START_DATE, END_DATE);

        assertEquals("[E][ ] Conference (from: Sep 10 2026 to: Sep 12 2026)",
                event.toString());
    }

    @Test
    public void toString_completedEvent_returnsFormattedEventWithCompletedStatus() {
        Event event = new Event("Conference", START_DATE, END_DATE);
        event.markAsDone();

        assertEquals("[E][X] Conference (from: Sep 10 2026 to: Sep 12 2026)",
                event.toString());
    }
}
