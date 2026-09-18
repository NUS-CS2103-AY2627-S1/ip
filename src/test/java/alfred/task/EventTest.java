package alfred.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests {@link Event#occursOn(LocalDate)} inclusive range matching and save format.
 */
public class EventTest {
    @Test
    public void occursOn_inclusiveRange_matchesStartMiddleAndEnd() {
        Event event = new Event("camp", date("2019-10-14"), date("2019-10-16"));
        assertTrue(event.occursOn(LocalDate.of(2019, 10, 14)));
        assertTrue(event.occursOn(LocalDate.of(2019, 10, 15)));
        assertTrue(event.occursOn(LocalDate.of(2019, 10, 16)));
        assertFalse(event.occursOn(LocalDate.of(2019, 10, 13)));
        assertFalse(event.occursOn(LocalDate.of(2019, 10, 17)));
    }

    @Test
    public void occursOn_sameDayEvent_matchesThatDateOnly() {
        Event event = new Event("meeting", date("2019-10-15 0900"), date("2019-10-15 1800"));
        assertTrue(event.occursOn(LocalDate.of(2019, 10, 15)));
        assertFalse(event.occursOn(LocalDate.of(2019, 10, 14)));
        assertFalse(event.occursOn(LocalDate.of(2019, 10, 16)));
    }

    @Test
    public void toSaveFormat_pendingRange() {
        Event event = new Event("camp", date("2019-10-14"), date("2019-10-16"));
        assertEquals("E | 0 | camp | 2019-10-14 | 2019-10-16 | UA", event.toSaveFormat());
        assertEquals("[E][ ] camp (from: Oct 14 2019 to: Oct 16 2019)", event.getDisplayText());
    }

    private static TaskDateTime date(String text) {
        TaskDateTime parsed = TaskDateTime.parseUserInput(text);
        assertNotNull(parsed);
        return parsed;
    }
}
