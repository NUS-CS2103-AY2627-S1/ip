package otaku.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Tests the completion state and display behavior shared by all tasks. */
public class TaskTest {
    @Test
    public void markAsDone_incompleteTask_taskBecomesDone() {
        Task task = new Todo("read book");

        task.markAsDone();

        assertTrue(task.isDone());
        assertEquals("X", task.getStatusIcon());
        assertEquals("[T][X] read book", task.toString());
    }

    @Test
    public void unmarkAsDone_completedTask_taskBecomesIncomplete() {
        Task task = new Todo("read book");
        task.markAsDone();

        task.unmarkAsDone();

        assertFalse(task.isDone());
        assertEquals(" ", task.getStatusIcon());
        assertEquals("[T][ ] read book", task.toString());
    }

    @Test
    public void containsKeyword_matchingAndNonMatchingKeywords_returnsExpectedResult() {
        Task task = new Todo("read book");

        assertTrue(task.containsKeyword("book"));
        assertFalse(task.containsKeyword("Book"));
        assertFalse(task.containsKeyword("magazine"));
    }

    @Test
    public void constructor_nullOrBlankDescription_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> new Todo(null));
        assertThrows(AssertionError.class, () -> new Todo("   "));
    }
}
