package slotbot.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class TaskTest {

    @Test
    public void constructor_nullDescription_assertionError() {
        assertThrows(AssertionError.class, () -> new Task(null));
    }

    @Test
    public void constructor_emptyDescription_assertionError() {
        assertThrows(AssertionError.class, () -> new Task(""));
    }

    @Test
    public void constructor_whitespaceDescription_assertionError() {
        assertThrows(AssertionError.class, () -> new Task("   "));
    }

    @Test
    public void constructor_validDescription_preservesDescription() {
        Task task = new Task("read book");

        assertEquals("read book", task.getDescription());
    }

    @Test
    public void completionStatus_markAndUnmark_updatesStatusAndDisplay() {
        Task task = new Task("read book");

        assertFalse(task.isDone());
        assertEquals("[ ] read book", task.toString());

        task.markDone();
        assertTrue(task.isDone());
        assertEquals("[X] read book", task.toString());

        task.markUndone();
        assertFalse(task.isDone());
        assertEquals("[ ] read book", task.toString());
    }
}
