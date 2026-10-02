package florkofcows.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class EventTest {
    @Test
    public void toString_dateRange_showsFormattedDates() {
        Event e = new Event("conference", "2019-12-01", "2019-12-03");
        assertEquals("[E][ ] conference (from: Dec 1 2019 to: Dec 3 2019)", e.toString());
    }

    @Test
    public void toSaveFormat_dateRange_correctFormat() {
        Event e = new Event("conference", "2019-12-01", "2019-12-03");
        assertEquals("E | 0 | conference | 2019-12-01 | 2019-12-03", e.toSaveFormat());
    }

    @Test
    public void toSaveFormat_done_correctFormat() {
        Event e = new Event("conference", "2019-12-01", "2019-12-03");
        e.markAsDone();
        assertEquals("E | 1 | conference | 2019-12-01 | 2019-12-03", e.toSaveFormat());
    }

    @Test
    public void isOccurringOn_dateWithinRange_returnsTrue() {
        Event e = new Event("trip", "2019-12-01", "2019-12-05");
        assertTrue(e.isOccurringOn(LocalDate.of(2019, 12, 3)));
    }

    @Test
    public void isOccurringOn_dateOutsideRange_returnsFalse() {
        Event e = new Event("trip", "2019-12-01", "2019-12-05");
        assertFalse(e.isOccurringOn(LocalDate.of(2019, 12, 10)));
    }

    @Test
    public void isOccurringOn_dateOnRangeBoundary_returnsTrue() {
        Event e = new Event("trip", "2019-12-01", "2019-12-05");
        assertTrue(e.isOccurringOn(LocalDate.of(2019, 12, 1)));
        assertTrue(e.isOccurringOn(LocalDate.of(2019, 12, 5)));
    }

    @Test
    public void isOccurringOn_onlyFromIsDate_matchesFromOnly() {
        Event e = new Event("call", "2019-12-02", "later");
        assertTrue(e.isOccurringOn(LocalDate.of(2019, 12, 2)));
        assertFalse(e.isOccurringOn(LocalDate.of(2019, 12, 3)));
    }

    @Test
    public void isOccurringOn_onlyToIsDate_matchesToOnly() {
        Event e = new Event("call", "sometime", "2019-12-05");
        assertTrue(e.isOccurringOn(LocalDate.of(2019, 12, 5)));
    }

    @Test
    public void isOccurringOn_neitherIsDate_returnsFalse() {
        Event e = new Event("party", "tonight", "late");
        assertFalse(e.isOccurringOn(LocalDate.of(2019, 12, 2)));
    }

    @Test
    public void constructor_endBeforeStart_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Event("trip", "2019-12-05", "2019-12-03"));
    }

    @Test
    public void constructor_sameStartAndEnd_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> new Event("brief sync", "2019-12-05 0900", "2019-12-05 0900"));
    }

    @Test
    public void getFromAndGetTo_returnFormattedBoundaries() {
        Event e = new Event("meeting", "2019-12-02", "2019-12-03");
        assertEquals("Dec 2 2019", e.getFrom());
        assertEquals("Dec 3 2019", e.getTo());
    }
}
