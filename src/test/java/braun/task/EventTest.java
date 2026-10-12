package braun.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import braun.exception.BraunException;

/**
 * Unit tests for {@link Event} validation and interval behaviors.
 */
public class EventTest {

    @Test
    public void constructor_validInterval_success() throws BraunException {
        Event event = new Event("broadcast discussion", "2026-08-24 1400", "2026-08-24 1600");
        assertEquals("broadcast discussion", event.getDescription());
        assertEquals("[E][ ] broadcast discussion (from: Aug 24 2026, 2:00PM to: Aug 24 2026, 4:00PM)",
                event.toString());
    }

    @Test
    public void constructor_endTimeBeforeStartTime_exceptionThrown() {
        assertThrows(BraunException.class, () ->
            new Event("impossible paradox", "2026-08-24 1600", "2026-08-24 1400"));
    }

    @Test
    public void isOnDate_dateWithinRange_returnsTrue() throws BraunException {
        Event event = new Event("field survey", "2026-08-20 0900", "2026-08-25 1800");
        assertTrue(event.isOnDate(LocalDate.of(2026, 8, 20)));
        assertTrue(event.isOnDate(LocalDate.of(2026, 8, 22)));
        assertTrue(event.isOnDate(LocalDate.of(2026, 8, 25)));
    }

    @Test
    public void isOnDate_dateOutsideRange_returnsFalse() throws BraunException {
        Event event = new Event("field survey", "2026-08-20 0900", "2026-08-25 1800");
        assertFalse(event.isOnDate(LocalDate.of(2026, 8, 19)));
        assertFalse(event.isOnDate(LocalDate.of(2026, 8, 26)));
    }

    @Test
    public void toFileFormat_validEvent_formattedCorrectly() throws BraunException {
        Event event = new Event("ghost hunting", "2026-08-24 1400", "2026-08-24 1600");
        assertEquals("E | 0 | ghost hunting | 2026-08-24 1400 | 2026-08-24 1600", event.toFileFormat());
        event.markAsDone();
        assertEquals("E | 1 | ghost hunting | 2026-08-24 1400 | 2026-08-24 1600", event.toFileFormat());
    }
}
