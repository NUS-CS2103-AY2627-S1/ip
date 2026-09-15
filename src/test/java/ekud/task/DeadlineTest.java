package ekud.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import ekud.EkudException;

/**
 * Tests {@link Deadline} display text, save text, and date matching.
 */
public class DeadlineTest {
    @Test
    public void toString_dateOnly_includesFormattedBy() throws EkudException {
        Deadline deadline = new Deadline("return book", TaskDateTime.parse("2019-12-02"));
        assertEquals("[D][ ] return book (by: Dec 02 2019)", deadline.toString());
    }

    @Test
    public void toString_withTime_includesFormattedTime() throws EkudException {
        Deadline deadline = new Deadline("return book", TaskDateTime.parse("2019-12-02 1800"));
        assertEquals("[D][ ] return book (by: Dec 02 2019, 6:00 PM)", deadline.toString());
    }

    @Test
    public void occursOn_matchingDate_returnsTrue() throws EkudException {
        Deadline deadline = new Deadline("return book", TaskDateTime.parse("2019-12-02 1800"));
        assertTrue(deadline.occursOn(LocalDate.of(2019, 12, 2)));
        assertFalse(deadline.occursOn(LocalDate.of(2019, 12, 3)));
    }

    @Test
    public void getTypeIcon_returnsD() throws EkudException {
        assertEquals("D", new Deadline("return book", TaskDateTime.parse("2019-12-02")).getTypeIcon());
    }

    @Test
    public void getBy_returnsParsedDate() throws EkudException {
        Deadline deadline = new Deadline("return book", TaskDateTime.parse("2019-12-02 1800"));
        assertEquals("2019-12-02 1800", deadline.getBy().toSaveString());
    }
}
