package coco;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class DeadlineTest {

    @Test
    public void isRecurring_noRecurrenceGiven_returnsFalse() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 10, 15));

        assertFalse(deadline.isRecurring());
    }

    @Test
    public void isRecurring_recurrenceGiven_returnsTrue() {
        Deadline deadline = new Deadline("standup", LocalDate.of(2019, 10, 15), Recurrence.DAILY);

        assertTrue(deadline.isRecurring());
    }

    @Test
    public void advanceToNextOccurrence_daily_movesByOneDay() {
        Deadline deadline = new Deadline("standup", LocalDate.of(2019, 10, 15), Recurrence.DAILY);

        deadline.advanceToNextOccurrence();

        assertEquals("[D][ ] standup (by: Oct 16 2019) (every: daily)", deadline.toString());
    }

    @Test
    public void advanceToNextOccurrence_monthly_movesByOneMonth() {
        Deadline deadline = new Deadline("rent", LocalDate.of(2019, 1, 31), Recurrence.MONTHLY);

        deadline.advanceToNextOccurrence();

        // LocalDate.plusMonths clamps a day that doesn't exist in the target
        // month (Jan 31 -> Feb has no 31st) down to that month's last day.
        assertEquals("[D][ ] rent (by: Feb 28 2019) (every: monthly)", deadline.toString());
    }

    @Test
    public void advanceToNextOccurrence_notRecurring_throwsIllegalStateException() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 10, 15));

        assertThrows(IllegalStateException.class, deadline::advanceToNextOccurrence);
    }
}
