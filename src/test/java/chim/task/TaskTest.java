package chim.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class TaskTest {

    @Test
    public void constructor_newTask_defaultsToNotDoneAndNoPriority() {
        Task task = new Task("sample", TaskType.TODO);
        assertFalse(task.getStatusIcon().equals("X"));
        assertEquals("none", task.getPriority());
    }

    @Test
    public void markAsDone_notDoneTask_statusIconBecomesX() {
        Task task = new Task("sample", TaskType.TODO);
        task.markAsDone();
        assertEquals("X", task.getStatusIcon());
    }

    @Test
    public void markAsNotDone_doneTask_statusIconBecomesBlank() {
        Task task = new Task("sample", TaskType.TODO);
        task.markAsDone();
        task.markAsNotDone();
        assertEquals(" ", task.getStatusIcon());
    }

    @Test
    public void setPriority_validValue_getPriorityReturnsIt() {
        Task task = new Task("sample", TaskType.TODO);
        task.setPriority("high");
        assertEquals("high", task.getPriority());
    }

    @Test
    public void toString_priorityNone_noPriorityLabelShown() {
        Task task = new Task("sample", TaskType.TODO);
        assertTrue(task.toString().equals("[ ] sample"));
    }

    @Test
    public void toString_priorityHigh_priorityLabelShown() {
        Task task = new Task("sample", TaskType.TODO);
        task.setPriority("high");
        assertEquals("[ ][P:high] sample", task.toString());
    }

    @Test
    public void toFileFormat_defaultPriority_includesNone() {
        Task task = new Task("sample", TaskType.TODO);
        assertEquals("T | 0 | none | sample", task.toFileFormat());
    }

    @Test
    public void getDescription_returnsGivenDescription() {
        Task task = new Task("sample", TaskType.TODO);
        assertEquals("sample", task.getDescription());
    }

    @Test
    public void getType_returnsGivenType() {
        Task task = new Task("sample", TaskType.EVENT);
        assertEquals(TaskType.EVENT, task.getType());
    }
}
