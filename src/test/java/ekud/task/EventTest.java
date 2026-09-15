package ekud.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import ekud.EkudException;

/**
 * Tests {@link Event} display text, save text, and inclusive date-range matching.
 */
public class EventTest {
    @Test
    public void toString_withTimes_includesFromAndTo() throws EkudException {
        Event event = new Event("meeting",
                TaskDateTime.parse("2019-12-02 1400"),
                TaskDateTime.parse("2019-12-02 1600"));
        assertEquals("[E][ ] meeting (from: Dec 02 2019, 2:00 PM to: Dec 02 2019, 4:00 PM)",
                event.toString());
    }

    @Test
    public void occursOn_dateInsideInclusiveRange_returnsTrue() throws EkudException {
        Event event = new Event("camp",
                TaskDateTime.parse("2019-12-02"),
                TaskDateTime.parse("2019-12-04"));
        assertTrue(event.occursOn(LocalDate.of(2019, 12, 2)));
        assertTrue(event.occursOn(LocalDate.of(2019, 12, 3)));
        assertTrue(event.occursOn(LocalDate.of(2019, 12, 4)));
        assertFalse(event.occursOn(LocalDate.of(2019, 12, 1)));
        assertFalse(event.occursOn(LocalDate.of(2019, 12, 5)));
    }

    @Test
    public void toString_dateOnly_omitsTimes() throws EkudException {
        Event event = new Event("camp",
                TaskDateTime.parse("2019-12-02"),
                TaskDateTime.parse("2019-12-04"));
        assertEquals("[E][ ] camp (from: Dec 02 2019 to: Dec 04 2019)", event.toString());
    }

    @Test
    public void getTypeIcon_returnsE() throws EkudException {
        Event event = new Event("meeting",
                TaskDateTime.parse("2019-12-02"),
                TaskDateTime.parse("2019-12-02"));
        assertEquals("E", event.getTypeIcon());
    }

    @Test
    public void getFromAndGetTo_returnParsedRange() throws EkudException {
        Event event = new Event("meeting",
                TaskDateTime.parse("2019-12-02 1400"),
                TaskDateTime.parse("2019-12-02 1600"));
        assertEquals("2019-12-02 1400", event.getFrom().toSaveString());
        assertEquals("2019-12-02 1600", event.getTo().toSaveString());
    }
}
