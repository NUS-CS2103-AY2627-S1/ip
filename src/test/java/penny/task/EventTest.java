package penny.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import penny.common.PennyException;
import penny.common.Time;

/**
 * Unit test suite for {@link Event}.
 * Verifies event construction, date range coverage, display string formatting, and storage serialization.
 */
public class EventTest {

    @Test
    public void constructor_validStrings_createsEvent() throws PennyException {
        Event event = new Event("camp", "2019-10-15 1400", "2019-10-18 1600");
        assertEquals("camp", event.getDescription());
        assertNotNull(event.getFrom());
        assertNotNull(event.getTo());
    }

    @Test
    public void constructor_timeObjects_createsEvent() throws PennyException {
        Time from = new Time("2019-10-15 1400");
        Time to = new Time("2019-10-18 1600");
        Event event = new Event("camp", from, to);
        assertEquals(from, event.getFrom());
        assertEquals(to, event.getTo());
    }

    @Test
    public void isOnDate_startDate_returnsTrue() throws PennyException {
        Event event = new Event("camp", "2019-10-15", "2019-10-18");
        assertTrue(event.isOnDate(LocalDate.of(2019, 10, 15)));
    }

    @Test
    public void isOnDate_endDate_returnsTrue() throws PennyException {
        Event event = new Event("camp", "2019-10-15", "2019-10-18");
        assertTrue(event.isOnDate(LocalDate.of(2019, 10, 18)));
    }

    @Test
    public void isOnDate_dateWithinRange_returnsTrue() throws PennyException {
        Event event = new Event("camp", "2019-10-15", "2019-10-18");
        assertTrue(event.isOnDate(LocalDate.of(2019, 10, 16)));
    }

    @Test
    public void isOnDate_dateBeforeStart_returnsFalse() throws PennyException {
        Event event = new Event("camp", "2019-10-15", "2019-10-18");
        assertFalse(event.isOnDate(LocalDate.of(2019, 10, 14)));
    }

    @Test
    public void isOnDate_dateAfterEnd_returnsFalse() throws PennyException {
        Event event = new Event("camp", "2019-10-15", "2019-10-18");
        assertFalse(event.isOnDate(LocalDate.of(2019, 10, 19)));
    }

    @Test
    public void toString_validEvent_formatsDisplayString() throws PennyException {
        Event event = new Event("camp", "2019-10-15 1400", "2019-10-18 1600");
        assertEquals("[E][ ] camp (from: Oct 15 2019, 2:00pm to: Oct 18 2019, 4:00pm)", event.toString());
    }

    @Test
    public void toFileFormat_validEvent_returnsPipeSeparatedString() throws PennyException {
        Event event = new Event("camp", "2019-10-15 1400", "2019-10-18 1600");
        assertEquals("E | 0 | camp | 2019-10-15 1400 | 2019-10-18 1600", event.toFileFormat());
    }
}
