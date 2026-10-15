package vector.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class DeadlineTest {

    @Test
    public void toString_validDeadline_correctFormat() {
        try {
            Deadline d = new Deadline("return book", "2023-06-06 2359");
            assertEquals("[D][ ] return book (by: Jun 6 2023, 11:59 PM)", d.toString());
            d.markAsDone();
            assertEquals("[D][X] return book (by: Jun 6 2023, 11:59 PM)", d.toString());
        } catch (Exception e) {
            fail("Exception thrown on valid deadline: " + e.getMessage());
        }
    }

    @Test
    public void toFileFormat_validDeadline_correctFormat() {
        try {
            Deadline d = new Deadline("return book", "2023-06-06 2359");
            assertEquals("D | 0 | return book | 2023-06-06 2359", d.toFileFormat());
            d.markAsDone();
            assertEquals("D | 1 | return book | 2023-06-06 2359", d.toFileFormat());
        } catch (Exception e) {
            fail("Exception thrown on valid deadline: " + e.getMessage());
        }
    }

    @Test
    public void equals_sameDeadlines_returnsTrue() {
        try {
            Deadline d1 = new Deadline("return book", "2023-06-06 2359");
            Deadline d2 = new Deadline("return book", "2023-06-06 2359");
            assertTrue(d1.equals(d2));
            assertTrue(d1.equals(d1));
        } catch (Exception e) {
            fail("Exception thrown on valid deadline: " + e.getMessage());
        }
    }

    @Test
    public void equals_differentDeadlines_returnsFalse() {
        try {
            Deadline d1 = new Deadline("return book", "2023-06-06 2359");
            Deadline d2 = new Deadline("borrow book", "2023-06-06 2359");
            Deadline d3 = new Deadline("return book", "2023-06-07 2359");
            assertFalse(d1.equals(d2));
            assertFalse(d1.equals(d3));
            assertFalse(d1.equals(null));
        } catch (Exception e) {
            fail("Exception thrown on valid deadline: " + e.getMessage());
        }
    }

    @Test
    public void occursOn_matchingDate_returnsTrue() {
        try {
            Deadline d = new Deadline("return book", "2023-06-06 2359");
            LocalDate date = LocalDate.of(2023, 6, 6);
            assertTrue(d.occursOn(date));
        } catch (Exception e) {
            fail("Exception thrown on valid deadline: " + e.getMessage());
        }
    }

    @Test
    public void occursOn_differentDate_returnsFalse() {
        try {
            Deadline d = new Deadline("return book", "2023-06-06 2359");
            LocalDate date = LocalDate.of(2023, 6, 7);
            assertFalse(d.occursOn(date));
        } catch (Exception e) {
            fail("Exception thrown on valid deadline: " + e.getMessage());
        }
    }
}
