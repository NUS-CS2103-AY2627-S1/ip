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
 * Unit test suite for {@link Deadline}.
 * Verifies deadline construction, date checking, display string formatting, and storage serialization.
 */
public class DeadlineTest {

    @Test
    public void constructor_validDateAndTime_createsDeadlineWithTime() throws PennyException {
        Deadline deadline = new Deadline("submit assignment", "2019-10-15 1800");
        assertEquals("submit assignment", deadline.getDescription());
        assertTrue(deadline.getBy().hasTime());
    }

    @Test
    public void constructor_validDateOnly_createsDeadlineWithoutTime() throws PennyException {
        Deadline deadline = new Deadline("return book", "2019-10-15");
        assertEquals("return book", deadline.getDescription());
        assertFalse(deadline.getBy().hasTime());
    }

    @Test
    public void constructor_timeObject_createsDeadline() throws PennyException {
        Time time = new Time("2019-10-15");
        Deadline deadline = new Deadline("return book", time);
        assertNotNull(deadline.getBy());
        assertEquals("return book", deadline.getDescription());
    }

    @Test
    public void isOnDate_matchingDate_returnsTrue() throws PennyException {
        Deadline deadline = new Deadline("submit assignment", "2019-10-15 1800");
        assertTrue(deadline.isOnDate(LocalDate.of(2019, 10, 15)));
    }

    @Test
    public void isOnDate_differentDate_returnsFalse() throws PennyException {
        Deadline deadline = new Deadline("submit assignment", "2019-10-15 1800");
        assertFalse(deadline.isOnDate(LocalDate.of(2019, 10, 16)));
    }

    @Test
    public void toString_withTime_formatsDisplayString() throws PennyException {
        Deadline deadline = new Deadline("submit assignment", "2019-10-15 1800");
        assertEquals("[D][ ] submit assignment (by: Oct 15 2019, 6:00pm)", deadline.toString());
    }

    @Test
    public void toString_withoutTime_formatsDisplayString() throws PennyException {
        Deadline deadline = new Deadline("return book", "2019-10-15");
        assertEquals("[D][ ] return book (by: Oct 15 2019)", deadline.toString());
    }

    @Test
    public void toFileFormat_withTime_returnsPipeSeparatedString() throws PennyException {
        Deadline deadline = new Deadline("submit assignment", "2019-10-15 1800");
        assertEquals("D | 0 | submit assignment | 2019-10-15 1800", deadline.toFileFormat());
    }

    @Test
    public void toFileFormat_withoutTime_returnsPipeSeparatedString() throws PennyException {
        Deadline deadline = new Deadline("return book", "2019-10-15");
        assertEquals("D | 0 | return book | 2019-10-15", deadline.toFileFormat());
    }
}
