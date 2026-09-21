package auto.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/** Tests inclusive event ranges, rendering, and durable representation. */
class EventTest {
    private static final LocalDate START_DATE = LocalDate.of(2026, 8, 21);
    private static final LocalDate END_DATE = LocalDate.of(2026, 8, 23);

    @Test
    void occursOn_outsideAndBoundaryDates_matchesInclusiveRange() {
        Event event = new Event("conference", START_DATE, END_DATE);

        assertFalse(event.occursOn(START_DATE.minusDays(1)));
        assertTrue(event.occursOn(START_DATE));
        assertTrue(event.occursOn(START_DATE.plusDays(1)));
        assertTrue(event.occursOn(END_DATE));
        assertFalse(event.occursOn(END_DATE.plusDays(1)));
    }

    @Test
    void occursOn_singleDayEvent_matchesOnlyItsDate() {
        Event event = new Event("meeting", START_DATE, START_DATE);

        assertFalse(event.occursOn(START_DATE.minusDays(1)));
        assertTrue(event.occursOn(START_DATE));
        assertFalse(event.occursOn(START_DATE.plusDays(1)));
    }

    @Test
    void toString_incompleteAndCompletedEvent_displaysTypeStatusAndRange() {
        Event event = new Event("conference", START_DATE, END_DATE);
        assertEquals("[E][ ] conference (from: Aug 21 2026 to: Aug 23 2026)", event.toString());

        event.mark();
        assertEquals("[E][X] conference (from: Aug 21 2026 to: Aug 23 2026)", event.toString());
    }

    @Test
    void toDataString_completedEvent_encodesNameDatesAndStatus() {
        Event event = new Event("conference", START_DATE, END_DATE);
        event.mark();

        assertEquals("E | 1 | Y29uZmVyZW5jZQ== | QXVnIDIxIDIwMjY= | QXVnIDIzIDIwMjY=",
                event.toDataString());
    }
}
