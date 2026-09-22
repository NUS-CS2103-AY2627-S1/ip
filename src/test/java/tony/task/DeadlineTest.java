package tony.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests the validation, date matching, and formatting behavior of {@link Deadline}.
 */
public class DeadlineTest {
    private static final LocalDate DUE_DATE = LocalDate.of(2026, 8, 12);

    /** Verifies that a deadline rejects a null due date. */
    @Test
    public void constructor_nullDueDate_throwsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new Deadline("Submit report", null));

        assertEquals("A deadline date cannot be null", exception.getMessage());
    }

    /** Verifies that a deadline matches its due date. */
    @Test
    public void isOnDate_dateOnDeadline_returnsTrue() {
        Deadline deadline = new Deadline("Submit report", DUE_DATE);

        assertTrue(deadline.isOnDate(DUE_DATE));
    }

    /** Verifies that a deadline does not match a different date. */
    @Test
    public void isOnDate_dateNotOnDeadline_returnsFalse() {
        Deadline deadline = new Deadline("Submit report", DUE_DATE);

        assertFalse(deadline.isOnDate(DUE_DATE.minusDays(1)));
    }

    /** Verifies that a deadline does not match a null date. */
    @Test
    public void isOnDate_nullDate_returnsFalse() {
        Deadline deadline = new Deadline("Submit report", DUE_DATE);

        assertFalse(deadline.isOnDate(null));
    }

    /** Verifies deadline identity includes both description and due date. */
    @Test
    public void hasSameDetails_variedDeadlines_comparesDescriptionAndDueDate() {
        Deadline deadline = new Deadline("Submit Report", DUE_DATE);

        assertTrue(deadline.hasSameDetails(new Deadline("submit report", DUE_DATE)));
        assertFalse(deadline.hasSameDetails(new Deadline("submit report", DUE_DATE.plusDays(1))));
        assertFalse(deadline.hasSameDetails(new Deadline("different report", DUE_DATE)));
        assertFalse(deadline.hasSameDetails(new Todo("Submit Report")));
    }

    /** Verifies the storage format of an incomplete deadline. */
    @Test
    public void toDataString_incompleteDeadline_returnsStorageFormat() {
        Deadline deadline = new Deadline("Submit report", DUE_DATE);

        assertEquals("D | 0 | Submit report | 2026-08-12", deadline.toDataString());
    }

    /** Verifies that storage formatting records completion and escapes special characters. */
    @Test
    public void toDataString_completedDeadlineWithSpecialCharacters_returnsEscapedStorageFormat() {
        Deadline deadline = new Deadline("Review A | B at C:\\notes", DUE_DATE);
        deadline.markAsDone();

        assertEquals("D | 1 | Review A \\| B at C:\\\\notes | 2026-08-12",
                deadline.toDataString());
    }

    /** Verifies the display format of an incomplete deadline. */
    @Test
    public void toString_incompleteDeadline_returnsDisplayFormat() {
        Deadline deadline = new Deadline("Submit report", DUE_DATE);

        assertEquals("[D][ ] Submit report (by: Aug 12 2026)", deadline.toString());
    }

    /** Verifies that display formatting shows a completed deadline. */
    @Test
    public void toString_completedDeadline_returnsCompletedStatus() {
        Deadline deadline = new Deadline("Submit report", DUE_DATE);
        deadline.markAsDone();

        assertEquals("[D][X] Submit report (by: Aug 12 2026)", deadline.toString());
    }
}
