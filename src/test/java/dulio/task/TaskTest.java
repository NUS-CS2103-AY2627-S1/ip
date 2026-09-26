package dulio.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class TaskTest {
    @Test
    public void newTask_isNotDone_hasEmptyStatusIcon() {
        Task task = new Task("read book");

        assertFalse(task.isDone());
        assertEquals(" ", task.getStatusIcon());
    }

    @Test
    public void markAsDone_notDoneTask_marksTaskDone() {
        Task task = new Task("read book");

        task.markAsDone();

        assertTrue(task.isDone());
        assertEquals("X", task.getStatusIcon());
    }

    @Test
    public void markAsDone_doneTask_keepsTaskDone() {
        Task task = new Task("read book");

        task.markAsDone();
        task.markAsDone();

        assertTrue(task.isDone());
        assertEquals("X", task.getStatusIcon());
    }

    @Test
    public void markAsNotDone_doneTask_marksTaskNotDone() {
        Task task = new Task("read book");
        task.markAsDone();

        task.markAsNotDone();

        assertFalse(task.isDone());
        assertEquals(" ", task.getStatusIcon());
    }

    @Test
    public void markAsNotDone_notDoneTask_keepsTaskNotDone() {
        Task task = new Task("read book");

        task.markAsNotDone();

        assertFalse(task.isDone());
        assertEquals(" ", task.getStatusIcon());
    }

    @Test
    public void markAsDone_thenMarkAsNotDone_restoresInitialState() {
        Task task = new Task("read book");

        task.markAsDone();
        task.markAsNotDone();

        assertFalse(task.isDone());
        assertEquals(" ", task.getStatusIcon());
    }

    @Test
    public void statusChanges_doNotChangeTaskDetails() {
        Task task = new Task("read book");

        task.markAsDone();
        task.markAsNotDone();

        assertEquals("read book", task.getDescription());
        assertEquals("read book", task.toString());
        assertEquals("T", task.getTypeIcon());
    }

    @Test
    public void taskDescription_methodsReturnOriginalDescription() {
        Task task = new Task("read book");

        assertEquals("read book", task.getDescription());
        assertEquals("read book", task.toString());
        assertEquals("T", task.getTypeIcon());
    }

    @Test
    public void recurringTask_methodsReturnRecurrenceDetails() {
        RecurringTask task = new RecurringTask("project meeting", "week");

        assertEquals("project meeting", task.getDescription());
        assertEquals("week", task.getInterval());
        assertEquals("project meeting (every: week)", task.toString());
        assertEquals("R", task.getTypeIcon());
    }
}
