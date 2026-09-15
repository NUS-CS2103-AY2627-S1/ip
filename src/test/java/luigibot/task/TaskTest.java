package luigibot.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests completion state and common representations in {@link Task}.
 */
public class TaskTest {

    @Test
    public void constructor_nullDescription_assertionThrown() {
        assertThrows(AssertionError.class, () -> new Task(null));
    }

    @Test
    public void constructor_blankDescription_assertionThrown() {
        assertThrows(AssertionError.class, () -> new Task("   "));
    }

    @Test
    public void markAndUnmark_validTransitions_statusIconUpdated() {
        Task task = new Task("read book");

        assertEquals("[ ]", task.getStatusIcon());

        task.mark();
        assertEquals("[X]", task.getStatusIcon());

        task.unmark();
        assertEquals("[ ]", task.getStatusIcon());
    }

    @Test
    public void toString_incompleteAndCompletedTask_correctDisplayReturned() {
        Task task = new Task("read book");

        assertEquals("[ ] read book", task.toString());

        task.mark();

        assertEquals("[X] read book", task.toString());
    }

    @Test
    public void toFileString_incompleteAndCompletedTask_correctStorageFormatReturned() {
        Task task = new Task("read book");

        assertEquals("T | 0 | read book", task.toFileString());

        task.mark();

        assertEquals("T | 1 | read book", task.toFileString());
    }

    @Test
    public void occursOn_anyDate_falseReturned() {
        Task task = new Task("read book");

        assertFalse(task.occursOn(LocalDate.of(2026, 8, 27)));
    }
}
