package will.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class DeadlineTest {

    @Test
    public void toString_recognizedByDate_appendsFormattedDate() {
        Deadline deadline = new Deadline("return book", "2019-10-15");
        assertEquals("[D][ ] return book (by: Oct 15 2019)", deadline.toString());
    }

    @Test
    public void toString_freeTextByField_appendsVerbatim() {
        Deadline deadline = new Deadline("return book", "sometime soon");
        assertEquals("[D][ ] return book (by: sometime soon)", deadline.toString());
    }

    @Test
    public void toSaveFormat_recognizedByDate_appendsNormalizedYyyyMmDd() {
        Deadline deadline = new Deadline("return book", "2019-10-15");
        assertEquals("D | 0 | return book | 2019-10-15", deadline.toSaveFormat());
    }

    @Test
    public void occursOn_matchingDate_returnsTrue() {
        Deadline deadline = new Deadline("return book", "2019-10-15");
        assertTrue(deadline.occursOn(LocalDate.of(2019, 10, 15)));
    }

    @Test
    public void occursOn_nonMatchingDate_returnsFalse() {
        Deadline deadline = new Deadline("return book", "2019-10-15");
        assertFalse(deadline.occursOn(LocalDate.of(2019, 10, 16)));
    }

    @Test
    public void occursOn_freeTextByField_alwaysReturnsFalse() {
        Deadline deadline = new Deadline("return book", "sometime soon");
        assertFalse(deadline.occursOn(LocalDate.of(2019, 10, 15)));
    }
}
