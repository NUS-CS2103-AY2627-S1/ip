package altair.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link Deadline}.
 *
 * <p>{@code Deadline} overrides {@link Deadline#toString()} and
 * {@link Deadline#toFileString()} to append its {@code by} date. The tests
 * check both the {@code D} type marker and the two date formats:
 * {@code MMM dd yyyy} for display and ISO {@code yyyy-MM-dd} for the save
 * file.</p>
 */
public class DeadlineTest {

    /** A fixed date keeps the expected output stable regardless of when the test runs. */
    private static final LocalDate BY = LocalDate.of(2019, 10, 15);

    @Test
    public void toString_newDeadline_showsTypeStatusAndDisplayDate() {
        Deadline deadline = new Deadline("return book", BY);
        assertEquals("[D][ ] return book (by: Oct 15 2019)", deadline.toString());
    }

    @Test
    public void toString_doneDeadline_showsXStatus() {
        Deadline deadline = new Deadline("return book", BY);
        deadline.markAsDone();
        assertEquals("[D][X] return book (by: Oct 15 2019)", deadline.toString());
    }

    @Test
    public void toString_singleDigitDay_zeroPadsDayInDisplayDate() {
        Deadline deadline = new Deadline("submit form", LocalDate.of(2020, 1, 5));
        assertEquals("[D][ ] submit form (by: Jan 05 2020)", deadline.toString());
    }

    @Test
    public void isDuplicateOf_sameDescriptionAndDate_returnsTrue() {
        Deadline first = new Deadline("return book", BY);
        Deadline second = new Deadline("return book", BY);
        assertTrue(first.isDuplicateOf(second));
    }

    @Test
    public void isDuplicateOf_sameDescriptionDifferentDate_returnsFalse() {
        // Different due dates make these different commitments, not duplicates.
        Deadline first = new Deadline("return book", BY);
        Deadline second = new Deadline("return book", LocalDate.of(2020, 1, 5));
        assertFalse(first.isDuplicateOf(second));
    }

    @Test
    public void constructor_nullDate_throwsAssertionError() {
        // The constructor asserts the date was parsed before it was passed in.
        assertThrows(AssertionError.class, () -> new Deadline("return book", null));
    }

    @Test
    public void toFileString_newDeadline_appendsIsoDateAfterDescription() {
        Deadline deadline = new Deadline("return book", BY);
        assertEquals("D | 0 | return book | 2019-10-15", deadline.toFileString());
    }

    @Test
    public void toFileString_doneDeadline_usesOneStatus() {
        Deadline deadline = new Deadline("return book", BY);
        deadline.markAsDone();
        assertEquals("D | 1 | return book | 2019-10-15", deadline.toFileString());
    }
}
