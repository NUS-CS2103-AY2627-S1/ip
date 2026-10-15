package sioet.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class RecurringTaskTest {

    @Test
    public void toString_incompleteRecurringTask_showsCorrectFormat() {
        RecurringTask task = new RecurringTask("project meeting", "week");

        assertEquals(
                "[R][ ] project meeting (every week)",
                task.toString()
        );
    }

    @Test
    public void toString_completedRecurringTask_showsCompletedStatus() {
        RecurringTask task = new RecurringTask("project meeting", "week");
        task.markAsDone();

        assertEquals(
                "[R][X] project meeting (every week)",
                task.toString()
        );
    }

    @Test
    public void getRecurrence_returnsCorrectRecurrence() {
        RecurringTask task = new RecurringTask("project meeting", "week");

        assertEquals("week", task.getRecurrence());
    }

    @Test
    public void toString_monthlyTask_showsCorrectFormat() {
        RecurringTask task = new RecurringTask("pay bills", "month");

        assertEquals(
                "[R][ ] pay bills (every month)",
                task.toString()
        );
    }
}
