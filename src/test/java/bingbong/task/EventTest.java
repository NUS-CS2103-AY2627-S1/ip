package bingbong.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class EventTest {

    @Test
    public void toString_validDates_formattedCorrectly() {
        Event event = new Event("buy eggs", "25/08/2026", "27/08/2026");
        assertEquals("[E] [ ] buy eggs (from: 25/08/2026 to: 27/08/2026)", event.toString());
    }

    @Test
    public void toString_markedAsCompleted_formattedCorrectly() {
        Event event = new Event("buy eggs", "25/08/2026", "27/08/2026");
        event.mark();
        assertEquals("[E] [X] buy eggs (from: 25/08/2026 to: 27/08/2026)", event.toString());
    }

    @Test
    public void formatToFile_normalInput_formattedCorrectly() {
        Event event = new Event("buy eggs", "25/08/2026", "27/08/2026");
        assertEquals("E | 0 | buy eggs | 2026-08-25 | 2026-08-27", event.formatToFile());
    }

    @Test
    public void formatToFile_completedInput_formattedCorrectly() {
        Event event = new Event("buy eggs", "25/08/2026", "27/08/2026");
        event.mark();
        assertEquals("E | 1 | buy eggs | 2026-08-25 | 2026-08-27", event.formatToFile());
    }

    @Test
    public void isOccuringOn_dateBoundsChecking_correctBooleanReturned() {
        Event event = new Event("buy eggs", "25/08/2026", "27/08/2026");
        assertTrue(event.isOccuringOn(LocalDate.of(2026, 8, 26)));
        assertFalse(event.isOccuringOn(LocalDate.of(2026, 8, 30)));
    }
}
