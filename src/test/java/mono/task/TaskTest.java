package mono.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Tests a task's completion state and representations.
 */
public class TaskTest {
    @Test
    public void constructor_newTask_hasIncompleteRepresentations() {
        Task task = new Task("read a book");

        assertEquals("[ ] read a book", task.toString());
        assertEquals("T | 0 | read a book", task.toFileString());
    }

    @Test
    public void markDone_incompleteTask_hasCompletedRepresentations() {
        Task task = new Task("read a book");

        task.markDone();

        assertEquals("[X] read a book", task.toString());
        assertEquals("T | 1 | read a book", task.toFileString());
    }

    @Test
    public void unmarkDone_completedTask_hasIncompleteRepresentations() {
        Task task = new Task("read a book");
        task.markDone();

        task.unmarkDone();

        assertEquals("[ ] read a book", task.toString());
        assertEquals("T | 0 | read a book", task.toFileString());
    }
}
