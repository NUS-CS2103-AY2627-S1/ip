package waddles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class RecurrenceTest {

    @Test
    public void deadlineRecur_weekly_advancesByDateAndResetsDoneStatus() {
        Deadline deadline = new Deadline("pay rent", "2024-01-01");
        deadline.setRecurrence(Recurrence.WEEKLY);
        deadline.markAsDone();

        deadline.recur();

        assertEquals("2024-01-08", deadline.getBy());
        assertFalse(deadline.isDone());
    }

    @Test
    public void eventRecur_daily_advancesBothDates() {
        Event event = new Event("standup", "2024-01-01", "2024-01-01");
        event.setRecurrence(Recurrence.DAILY);
        event.markAsDone();

        event.recur();

        assertEquals("2024-01-02", event.getFrom());
        assertEquals("2024-01-02", event.getTo());
        assertFalse(event.isDone());
    }

    @Test
    public void deadline_withoutRecurrence_isNotRecurring() {
        Deadline deadline = new Deadline("pay rent", "2024-01-01");
        assertFalse(deadline.isRecurring());
    }

    @Test
    public void deadlineCopy_recurring_preservesRecurrence() {
        Deadline deadline = new Deadline("pay rent", "2024-01-01");
        deadline.setRecurrence(Recurrence.MONTHLY);

        Task copy = deadline.copy();

        assertTrue(copy.isRecurring());
        assertEquals(Recurrence.MONTHLY, ((Deadline) copy).getRecurrence());
    }

    @Test
    public void eventCopy_recurring_preservesRecurrence() {
        Event event = new Event("standup", "2024-01-01", "2024-01-01");
        event.setRecurrence(Recurrence.DAILY);

        Task copy = event.copy();

        assertTrue(copy.isRecurring());
        assertEquals(Recurrence.DAILY, ((Event) copy).getRecurrence());
    }
}
