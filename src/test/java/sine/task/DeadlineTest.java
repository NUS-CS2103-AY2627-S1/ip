package sine.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests deadline-specific date access and user-facing formatting.
 */
public class DeadlineTest {
    /** Tests that the stored deadline date can be retrieved unchanged. */
    @Test
    public void getBy_deadlineHasDate_returnsSameDate() {
        LocalDate date = LocalDate.of(2026, 8, 30);
        Deadline deadline = new Deadline("return book", date);

        assertEquals(date, deadline.getBy());
    }

    /** Tests the display of an incomplete deadline. */
    @Test
    public void toString_incompleteDeadline_formatsStatusDescriptionAndDate() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2026, 8, 30));

        assertEquals("[D][ ] return book (by: Aug 30 2026)", deadline.toString());
    }

    /** Tests the display of a completed deadline. */
    @Test
    public void toString_completedDeadline_formatsCompletedStatus() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2026, 8, 30));
        deadline.markAsDone();

        assertEquals("[D][X] return book (by: Aug 30 2026)", deadline.toString());
    }

    /** Tests that single-digit days are padded to two digits. */
    @Test
    public void toString_singleDigitMonthAndDay_zeroPadsDay() {
        Deadline deadline = new Deadline("submit report", LocalDate.of(2026, 1, 5));

        assertEquals("[D][ ] submit report (by: Jan 05 2026)", deadline.toString());
    }
}
