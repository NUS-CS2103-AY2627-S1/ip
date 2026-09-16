package bobby;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Tests the state, identity, and representations shared by all tasks. */
public class TaskTest {

    /** Verifies that marking and unmarking update every completion representation. */
    @Test
    public void completionState_markAndUnmark_representationsStayConsistent() {
        Task task = new Task("read book");

        assertFalse(task.isDone());
        assertEquals(" ", task.getStatusIcon());
        assertEquals("0", task.getStorageStatus());

        task.markAsDone();

        assertTrue(task.isDone());
        assertEquals("X", task.getStatusIcon());
        assertEquals("1", task.getStorageStatus());

        task.unmarkAsDone();

        assertFalse(task.isDone());
        assertEquals(" ", task.getStatusIcon());
        assertEquals("0", task.getStorageStatus());
    }

    /** Verifies the base task's description, display text, and storage text. */
    @Test
    public void representations_newAndPrioritizedTask_expectedTextReturned() {
        Task task = new Task("read book");

        assertEquals("read book", task.getDescription());
        assertEquals("[ ] read book", task.toString());
        assertEquals("T | 0 | read book | NONE", task.toStorageString());

        task.setPriority(Priority.MEDIUM);
        task.markAsDone();

        assertEquals("[X][P: MEDIUM] read book", task.toString());
        assertEquals("T | 1 | read book | MEDIUM", task.toStorageString());
    }

    /** Verifies that task identity ignores case, whitespace, completion, and priority. */
    @Test
    public void hasSameDetails_equivalentBaseTasks_trueReturned() {
        Task firstTask = new Task("  Read   Book ");
        Task secondTask = new Task("read book");
        secondTask.markAsDone();
        secondTask.setPriority(Priority.HIGH);

        assertTrue(firstTask.hasSameDetails(secondTask));
        assertTrue(secondTask.hasSameDetails(firstTask));
    }

    /** Verifies that null, another task type, and another description are not equivalent. */
    @Test
    public void hasSameDetails_differentTasks_falseReturned() {
        Task task = new Task("read book");

        assertFalse(task.hasSameDetails(null));
        assertFalse(task.hasSameDetails(new ToDo("read book")));
        assertFalse(task.hasSameDetails(new Task("return book")));
    }

    /** Verifies that a null priority violates the task contract. */
    @Test
    public void setPriority_nullPriority_assertionErrorThrown() {
        Task task = new Task("read book");

        assertThrows(AssertionError.class, () -> task.setPriority(null));
    }
}
