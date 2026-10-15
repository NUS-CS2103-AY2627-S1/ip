package jarvis.classes;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** Tests the behavior of {@link Task}. */
public class TaskTest {
    /** Verifies that a newly created task is undone and formatted correctly. */
    @Test
    public void constructor_createsUndoneTask() {
        Task task = new Task("Read book");

        assertEquals(Task.CompletionStatus.UNDONE, task.getStatus());
        assertEquals("[] Read book", task.toString());
    }

    /** Verifies that a task can be marked as done and displays the done marker. */
    @Test
    public void setCompletionStatus_doneTaskDisplaysDoneMarker() {
        Task task = new Task("Read book");

        task.setCompletionStatus(Task.CompletionStatus.DONE);

        assertEquals(Task.CompletionStatus.DONE, task.getStatus());
        assertEquals("[X] Read book", task.toString());
    }

    /** Verifies that a completed task can be marked undone again. */
    @Test
    public void setCompletionStatus_undoneTaskDisplaysUndoneMarker() {
        Task task = new Task("Read book");
        task.setCompletionStatus(Task.CompletionStatus.DONE);

        task.setCompletionStatus(Task.CompletionStatus.UNDONE);

        assertEquals(Task.CompletionStatus.UNDONE, task.getStatus());
        assertEquals("[] Read book", task.toString());
    }
}
