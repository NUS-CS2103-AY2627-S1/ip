package greenchonk.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class EventTest {
    private static final LocalDate START_DATE = LocalDate.of(2026, 8, 28);
    private static final LocalDate END_DATE = LocalDate.of(2026, 8, 30);

    @Test
    void constructor_endBeforeStart_exceptionThrown() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new Event("conference", START_DATE, START_DATE.minusDays(1)));

        assertEquals("An event cannot end before it starts.", exception.getMessage());
    }

    @Test
    void constructor_sameDayEvent_eventCreated() {
        Event event = new Event("conference", START_DATE, START_DATE);

        assertTrue(event.occursOn(START_DATE));
        assertEquals(START_DATE, event.getStartDate());
        assertEquals(START_DATE, event.getEndDate());
    }

    @Test
    void occursOn_beforeDuringAndAfterEvent_matchesInclusiveRange() {
        Event event = new Event("conference", START_DATE, END_DATE);

        assertFalse(event.occursOn(START_DATE.minusDays(1)));
        assertTrue(event.occursOn(START_DATE));
        assertTrue(event.occursOn(START_DATE.plusDays(1)));
        assertTrue(event.occursOn(END_DATE));
        assertFalse(event.occursOn(END_DATE.plusDays(1)));
    }

    @Test
    void accessorsAndDisplay_validEvent_valuesAndFormatMatch() {
        Event event = new Event("conference", START_DATE, END_DATE);

        assertEquals(START_DATE, event.getStartDate());
        assertEquals(END_DATE, event.getEndDate());
        assertEquals("E", event.getTypeIcon());
        assertEquals("[E][ ] conference (from: Aug 28 2026 to: Aug 30 2026)",
                event.toString());
    }
}
