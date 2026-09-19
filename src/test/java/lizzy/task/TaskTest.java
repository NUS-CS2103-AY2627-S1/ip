package lizzy.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests shared task completion state and the default non-scheduled behavior.
 */
public class TaskTest {
    @Test
    void markAndUnmark_completionStatusChangesAccordingly() {
        Task task = new Task("read chapter");

        assertFalse(task.isDone());
        assertEquals("read chapter", task.getDescription());
        assertEquals("[ ] read chapter", task.toString());
        task.markAsDone();
        assertTrue(task.isDone());
        assertEquals("X", task.getStatusIcon());
        assertEquals("[X] read chapter", task.toString());

        task.markAsNotDone();
        assertFalse(task.isDone());
        assertEquals(" ", task.getStatusIcon());
    }

    @Test
    void occursOn_plainTask_falseForAnyDate() {
        Task task = new Task("read chapter");

        assertFalse(task.occursOn(LocalDate.of(2026, 9, 3)));
    }

    @Test
    void constructor_blankOrNullDescription_exceptionThrown() {
        assertThrows(IllegalArgumentException.class, () -> new Task("   "));
        assertThrows(IllegalArgumentException.class, () -> new Task(null));
    }
}
