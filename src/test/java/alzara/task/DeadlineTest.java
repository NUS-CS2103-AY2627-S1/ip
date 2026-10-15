package alzara.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link Deadline}: the "[D]"/"D |" prefix plus the "by" date, shown
 * as "MMM dd yyyy" in toString() and as the raw ISO date (LocalDate's default
 * toString()) in toSaveFormat(). Mark/unmark state is already covered via
 * ToDoTest, since that behaviour is inherited from Task unchanged.
 */
class DeadlineTest {

    // A freshly constructed Deadline starts out not done, with the date
    // reformatted from ISO (constructor input) to "MMM dd yyyy" (display).
    @Test
    void toString_taskNotDone_showsEmptyCheckboxWithByDate() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 10, 15));

        assertEquals("[D][ ] return book (by: Oct 15 2019)", deadline.toString());
    }

    // mark() (inherited from Task) should flip the task to done.
    @Test
    void toString_taskMarkedDone_showsXCheckboxWithByDate() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 10, 15));

        deadline.mark();

        assertEquals("[D][X] return book (by: Oct 15 2019)", deadline.toString());
    }

    // The "MMM dd yyyy" display pattern pads single-digit days with a leading
    // zero - worth pinning down since it's easy to assume "dd" means "no padding".
    @Test
    void toString_singleDigitDay_padsDayWithLeadingZero() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 1, 5));

        assertEquals("[D][ ] return book (by: Jan 05 2019)", deadline.toString());
    }

    // toSaveFormat() should store the date in its raw ISO form (yyyy-MM-dd),
    // not the "MMM dd yyyy" display form.
    @Test
    void toSaveFormat_taskNotDone_hasTypeAndIsoDate() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 10, 15));

        assertEquals("D | N | return book | 2019-10-15", deadline.toSaveFormat());
    }

    // toSaveFormat() should switch to "D | Y |" once the task is marked done.
    @Test
    void toSaveFormat_taskMarkedDone_hasTypeAndYFlag() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 10, 15));

        deadline.mark();

        assertEquals("D | Y | return book | 2019-10-15", deadline.toSaveFormat());
    }

    // isScheduledOn() should return true when the queried date equals the deadline exactly.
    @Test
    void isScheduledOn_exactDate_returnsTrue() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 10, 15));

        assertTrue(deadline.isScheduledOn(LocalDate.of(2019, 10, 15)));
    }

    // isScheduledOn() should return false for the day before the deadline.
    @Test
    void isScheduledOn_dayBeforeDeadline_returnsFalse() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 10, 15));

        assertFalse(deadline.isScheduledOn(LocalDate.of(2019, 10, 14)));
    }

    // isScheduledOn() should return false for the day after the deadline.
    @Test
    void isScheduledOn_dayAfterDeadline_returnsFalse() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 10, 15));

        assertFalse(deadline.isScheduledOn(LocalDate.of(2019, 10, 16)));
    }

    // getSortDate() should return the deadline's own date.
    @Test
    void getSortDate_returnsDeadlineDate() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 10, 15));

        assertEquals(LocalDate.of(2019, 10, 15), deadline.getSortDate());
    }
}
