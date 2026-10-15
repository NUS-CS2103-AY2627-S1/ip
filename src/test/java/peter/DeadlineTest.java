package peter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link Deadline}.
 */
public class DeadlineTest {
    @Test
    public void getBy_returnsConstructorValue() {
        LocalDateTime by = LocalDateTime.of(2019, 12, 2, 18, 0);
        Deadline deadline = new Deadline("return book", by);
        assertEquals(by, deadline.getBy());
    }

    @Test
    public void toFileFormat_notDone_usesDeadlineTypeCodeAndStorageDateFormat() {
        Deadline deadline = new Deadline("return book", LocalDateTime.of(2019, 12, 2, 18, 0));
        assertEquals("D | 0 | return book | 2019-12-02T1800", deadline.toFileFormat());
    }

    @Test
    public void toFileFormat_done_usesOneMarker() {
        Deadline deadline = new Deadline("return book", LocalDateTime.of(2019, 12, 2, 18, 0));
        deadline.markAsDone();
        assertEquals("D | 1 | return book | 2019-12-02T1800", deadline.toFileFormat());
    }

    @Test
    public void toString_nonMidnightTime_showsTime() {
        Deadline deadline = new Deadline("return book", LocalDateTime.of(2019, 12, 2, 18, 0));
        assertEquals("[D][ ] return book (by: Dec 02 2019, 6:00PM)", deadline.toString());
    }

    @Test
    public void toString_exactlyMidnight_omitsTime() {
        // A date-only deadline (Parser defaults the time to midnight) should
        // display as just a date, not a misleading "12:00AM".
        Deadline deadline = new Deadline("return book", LocalDateTime.of(2019, 12, 2, 0, 0));
        assertEquals("[D][ ] return book (by: Dec 02 2019)", deadline.toString());
    }

    @Test
    public void equals_sameDescriptionAndBy_returnsTrue() {
        LocalDateTime by = LocalDateTime.of(2019, 12, 2, 18, 0);
        assertEquals(new Deadline("return book", by), new Deadline("return book", by));
    }

    @Test
    public void equals_differentBy_returnsFalse() {
        Deadline first = new Deadline("return book", LocalDateTime.of(2019, 12, 2, 18, 0));
        Deadline second = new Deadline("return book", LocalDateTime.of(2019, 12, 3, 18, 0));
        assertFalse(first.equals(second));
    }

    @Test
    public void equals_sameDescriptionAsTodo_returnsFalse() {
        Deadline deadline = new Deadline("return book", LocalDateTime.of(2019, 12, 2, 18, 0));
        assertFalse(deadline.equals(new Todo("return book")));
    }

    @Test
    public void hashCode_equalDeadlines_haveSameHashCode() {
        LocalDateTime by = LocalDateTime.of(2019, 12, 2, 18, 0);
        assertEquals(new Deadline("return book", by).hashCode(), new Deadline("return book", by).hashCode());
    }
}
