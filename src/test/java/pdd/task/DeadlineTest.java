package pdd.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class DeadlineTest {
    @Test
    public void toString_includesDisplayFormattedDate() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 12, 1));
        assertEquals("[D][ ] return book (by: Dec 01 2019)", deadline.toString());
    }

    @Test
    public void toFileFormat_usesIsoDate() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 12, 1));
        assertEquals("D | 0 | return book | 2019-12-01", deadline.toFileFormat());
    }

    @Test
    public void occursOn_matchingDate_returnsTrue() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 12, 1));
        assertTrue(deadline.occursOn(LocalDate.of(2019, 12, 1)));
    }

    @Test
    public void occursOn_differentDate_returnsFalse() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 12, 1));
        assertFalse(deadline.occursOn(LocalDate.of(2019, 12, 2)));
    }
}
