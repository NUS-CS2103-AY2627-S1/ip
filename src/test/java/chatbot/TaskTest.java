package chatbot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Tests the state transitions of a Task.
 */
class TaskTest {

    /**
     * A newly created task should not be marked as complete.
     */
    @Test
    void markComplete_newTask_remainsIncompleteUntilCalled() {
        Task task = new Task("read book");
        assertEquals("[ ]", task.getCompletionStatus());
    }

    /**
     * Calling markComplete should change an incomplete task to complete.
     */
    @Test
    void markComplete_incompleteTask_marksTaskComplete() {
        Task task = new Task("read book");
        task.markComplete();
        assertEquals("[X]", task.getCompletionStatus());
    }

    /**
     * Calling markComplete repeatedly should not toggle a task back to incomplete.
     */
    @Test
    void markComplete_alreadyCompleteTask_remainsComplete() {
        Task task = new Task("read book");
        task.markComplete();
        task.markComplete();
        assertEquals("[X]", task.getCompletionStatus());
    }

    /**
     * markComplete should be able to complete a task again after it is unmarked.
     */
    @Test
    void markComplete_incompleteTaskAfterUnmark_marksTaskCompleteAgain() {
        Task task = new Task("read book");
        task.markComplete();
        task.markIncomplete();
        task.markComplete();
        assertEquals("[X]", task.getCompletionStatus());
    }

    /**
     * Null and blank descriptions should be rejected.
     */
    @Test
    void task_nullOrBlankDescription_throwsInvalidInputException() {
        assertThrows(InvalidInputException.class, () -> new Task(null));
        assertThrows(InvalidInputException.class, () -> new Task("   "));
    }
}
