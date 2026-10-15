package skynet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests task validation, status changes, and copying.
 */
public class TaskTest {

    @Test
    void task_emptyDescription_isRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Todo(null));
        assertThrows(IllegalArgumentException.class, () -> new Todo(""));
        assertThrows(IllegalArgumentException.class, () -> new Todo("   "));
    }

    @Test
    void task_saveFileDelimiterOrLineBreak_isRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Todo("a | b"));
        assertThrows(IllegalArgumentException.class, () -> new Todo("line\nbreak"));
        assertThrows(IllegalArgumentException.class, () -> new Todo("line\rbreak"));
    }

    @Test
    void task_statusCanBeMarkedAndUnmarked() {
        Task task = new Todo("test task");

        assertFalse(task.isDone());
        assertTrue(task.getStatusIcon().contains(" "));

        task.markAsDone();
        assertTrue(task.isDone());

        task.markAsNotDone();
        assertFalse(task.isDone());
    }

    @Test
    void task_copy_preservesDetailsAndStatusButIsIndependent() {
        Task original = new Todo("test task");
        original.markAsDone();

        Task copy = original.copy();

        assertNotSame(original, copy);
        assertTrue(copy.isDone());
        assertTrue(copy.toString().contains("test task"));

        copy.markAsNotDone();
        assertTrue(original.isDone());
    }

    @Test
    void todo_toString_containsTypeStatusAndDescription() {
        assertEquals("[T][ ] test task", new Todo("test task").toString());
    }
}
