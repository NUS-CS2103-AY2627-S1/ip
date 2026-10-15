package jaylen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Tests the common state and display behavior shared by all task types. */
public class TaskTest {
    @Test
    public void constructor_validDescription_createsIncompleteTodo() {
        Task task = new Todo("read book");

        assertFalse(task.isDone());
        assertEquals(TaskType.TODO, task.getType());
        assertEquals("[T][ ] read book", task.toString());
    }

    @Test
    public void constructor_blankDescription_assertionThrown() {
        assertThrows(AssertionError.class, () -> new Todo(" "));
    }

    @Test
    public void constructor_descriptionContainingDelimiter_throwsException() {
        JaylenException exception = assertThrows(JaylenException.class, () ->
                new Todo("compare A | B"));

        assertEquals("Task descriptions cannot contain the | character.", exception.getMessage());
    }

    @Test
    public void markAsDoneAndUndone_validTask_updatesCompletionState() {
        Task task = new Todo("read book");

        task.markAsDone();
        assertTrue(task.isDone());
        assertEquals("X", task.getStatusIcon());

        task.markAsUndone();
        assertFalse(task.isDone());
        assertEquals(" ", task.getStatusIcon());
    }
}
