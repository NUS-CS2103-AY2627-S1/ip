package odysseus;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Tests task completion state rendering.
 */
class TaskTest {
    @Test
    void markAndUnmark_changeRenderedCompletionState() {
        Task task = new Todo("read book");

        task.markAsDone();
        assertEquals("[T][X] read book", task.toString());

        task.markAsNotDone();
        assertEquals("[T][ ] read book", task.toString());
    }
}
