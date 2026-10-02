package florkofcows.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class DeadlineTest {
    @Test
    public void toString_dateOnly_showsFormattedDate() {
        Deadline d = new Deadline("submit report", "2019-12-02");
        assertEquals("[D][ ] submit report (by: Dec 2 2019)", d.toString());
    }

    @Test
    public void toString_plainTextBy_showsTextUnchanged() {
        Deadline d = new Deadline("submit report", "no idea :-p");
        assertEquals("[D][ ] submit report (by: no idea :-p)", d.toString());
    }

    @Test
    public void toSaveFormat_dateOnly_correctFormat() {
        Deadline d = new Deadline("submit report", "2019-12-02");
        assertEquals("D | 0 | submit report | 2019-12-02", d.toSaveFormat());
    }

    @Test
    public void toSaveFormat_done_correctFormat() {
        Deadline d = new Deadline("submit report", "2019-12-02");
        d.markAsDone();
        assertEquals("D | 1 | submit report | 2019-12-02", d.toSaveFormat());
    }

    @Test
    public void isOccurringOn_matchingDate_returnsTrue() {
        Deadline d = new Deadline("submit", "2019-12-02");
        assertTrue(d.isOccurringOn(LocalDate.of(2019, 12, 2)));
    }

    @Test
    public void isOccurringOn_differentDate_returnsFalse() {
        Deadline d = new Deadline("submit", "2019-12-02");
        assertFalse(d.isOccurringOn(LocalDate.of(2019, 12, 3)));
    }

    @Test
    public void isOccurringOn_nonDateText_returnsFalse() {
        Deadline d = new Deadline("submit", "no idea");
        assertFalse(d.isOccurringOn(LocalDate.of(2019, 12, 2)));
    }

    @Test
    public void getBy_returnsOriginalDisplayValue() {
        Deadline d = new Deadline("submit report", "2019-12-02");
        assertEquals("Dec 2 2019", d.getBy());
    }
}
