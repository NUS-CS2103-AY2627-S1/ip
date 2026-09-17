package lebron.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

public class RecurringTaskTest {
    private final LocalDateTime firstDue = LocalDateTime.of(2019, 10, 15, 18, 0);

    @Test
    public void equals_sameDescriptionDateAndInterval_returnsTrue() {
        assertEquals(new RecurringTask("project meeting", firstDue, RecurrenceInterval.WEEKLY),
                new RecurringTask("project meeting", firstDue, RecurrenceInterval.WEEKLY));
    }

    @Test
    public void equals_differentInterval_returnsFalse() {
        RecurringTask other = new RecurringTask("project meeting", firstDue, RecurrenceInterval.DAILY);
        assertNotEquals(new RecurringTask("project meeting", firstDue, RecurrenceInterval.WEEKLY), other);
    }

    @Test
    public void toString_newTask_showsNextDueDateAndInterval() {
        RecurringTask task = new RecurringTask("project meeting", firstDue, RecurrenceInterval.WEEKLY);
        assertEquals("[R][ ] project meeting (every week, next due: Oct 15 2019, 6:00 PM)", task.toString());
    }

    @Test
    public void setStatus_markedDone_advancesToNextOccurrenceAndStaysNotDone() {
        RecurringTask task = new RecurringTask("project meeting", firstDue, RecurrenceInterval.WEEKLY);

        task.setStatus(true);

        assertEquals("[R][ ] project meeting (every week, next due: Oct 22 2019, 6:00 PM)", task.toString());
    }

    @Test
    public void setStatus_unmarked_hasNoEffect() {
        RecurringTask task = new RecurringTask("project meeting", firstDue, RecurrenceInterval.DAILY);

        task.setStatus(false);

        assertEquals("[R][ ] project meeting (every day, next due: Oct 15 2019, 6:00 PM)", task.toString());
    }

    @Test
    public void reformat_correctSaveFormatWithIntervalName() {
        RecurringTask task = new RecurringTask("project meeting", firstDue, RecurrenceInterval.MONTHLY);
        assertEquals("R | 0 | project meeting | 2019-10-15T18:00 | MONTHLY", task.reformat());
    }
}
