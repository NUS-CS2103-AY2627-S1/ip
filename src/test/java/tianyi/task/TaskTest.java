package tianyi.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests the base task's completion state and default date behavior.
 */
public class TaskTest {
    @Test
    public void constructor_newTask_isIncomplete() {
        Task task = new Task("read book");

        assertEquals("0 | read book", task.getData());
        assertEquals("[ ] read book", task.toString());
    }

    @Test
    public void markAsDone_incompleteTask_updatesStatus() {
        Task task = new Task("read book");

        task.markAsDone();

        assertEquals("1 | read book", task.getData());
        assertEquals("[X] read book", task.toString());
    }

    @Test
    public void unmarkAsDone_completedTask_updatesStatus() {
        Task task = new Task("read book");
        task.markAsDone();

        task.unmarkAsDone();

        assertEquals("0 | read book", task.getData());
        assertEquals("[ ] read book", task.toString());
    }

    @Test
    public void isOccurringOn_anyDate_returnsFalse() {
        Task task = new Task("read book");

        assertFalse(task.isOccurringOn(new TaskTime("2-12-2019")));
    }

    @Test
    public void matchesKeyword_descriptionContainsKeyword_returnsTrue() {
        Task task = new Task("read book");

        assertTrue(task.matchesKeyword("book"));
    }

    @Test
    public void matchesKeyword_descriptionDoesNotContainKeyword_returnsFalse() {
        Task task = new Task("read book");

        assertFalse(task.matchesKeyword("milk"));
    }
}
