package blud.model;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Tests common task state and equality behavior. */
public class TaskTest {
    /** Verifies that marking and unmarking change the task representation. */
    @Test
    public void markAndUnmark_task_updatesCompletionRepresentation() {
        Task task = new ToDo(new String[] {"todo read"});

        assertFalse(task.toString().contains("[X]"));
        task.mark();
        assertTrue(task.toString().contains("[X]"));
        task.unmark();
        assertFalse(task.toString().contains("[X]"));
    }

    /** Verifies that task equality includes type, description, and task details. */
    @Test
    public void hasSameDetails_differentTaskDetails_returnsExpectedResult() {
        Task first = new Deadline(new String[] {"deadline report", "by 1/6/2026 0900"});
        Task same = new Deadline(new String[] {"deadline report", "by 1/6/2026 0900"});
        Task differentType = new ToDo(new String[] {"todo report"});

        assertTrue(first.hasSameDetails(same));
        assertFalse(first.hasSameDetails(differentType));
        assertFalse(first.hasSameDetails(null));
    }
}
