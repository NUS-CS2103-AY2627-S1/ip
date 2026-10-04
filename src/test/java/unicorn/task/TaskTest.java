package unicorn.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Checks the completion status displayed by a task.
 */
public class TaskTest {
    /**
     * Verifies that a task keeps its description and starts incomplete.
     */
    @Test
    void taskCreated_descriptionAndInitialStateAreAvailable() {
        Task task = new Task("read book");

        assertEquals("read book", task.getDescription(), "A task should retain its description.");
        assertFalse(task.isDone(), "A new task should not be complete.");
        assertEquals(" ", task.getStatusIcon(), "A new task should have an empty status icon.");
        assertEquals("[ ] read book", task.toString(), "A new task should use the incomplete display format.");
    }

    /**
     * Verifies that programmers cannot create tasks without meaningful descriptions.
     */
    @Test
    void constructor_nullOrBlankDescription_assertionErrorThrown() {
        assertThrows(AssertionError.class, () -> new Task(null),
                "A null task description violates the task invariant.");
        assertThrows(AssertionError.class, () -> new Task("   "),
                "A blank task description violates the task invariant.");
    }

    /**
     * Verifies that completing a task updates all completion-related behaviour.
     */
    @Test
    void markAsDone_incompleteTask_taskIsDisplayedAsComplete() {
        Task task = new Task("read book");

        task.markAsDone();

        assertTrue(task.isDone(), "A completed task should report that it is done.");
        assertEquals("X", task.getStatusIcon(), "A completed task should have an X status icon.");
        assertEquals("[X] read book", task.toString(), "A completed task should use the complete display format.");
    }

    /**
     * Verifies that reopening a completed task restores its incomplete state.
     */
    @Test
    void markAsUndone_completedTask_taskIsDisplayedAsIncomplete() {
        Task task = new Task("read book");
        task.markAsDone();

        task.markAsUndone();

        assertFalse(task.isDone(), "A reopened task should not report that it is done.");
        assertEquals(" ", task.getStatusIcon(), "A reopened task should have an empty status icon.");
        assertEquals("[ ] read book", task.toString(), "A reopened task should use the incomplete display format.");
    }

    /**
     * Verifies that duplicate comparison ignores status, case, and cosmetic whitespace.
     */
    @Test
    void hasSameDetailsAs_cosmeticDifferences_tasksConsideredEquivalent() {
        Task firstTask = new TodoTask("Read   Book");
        Task secondTask = new TodoTask("read book");
        secondTask.markAsDone();

        assertTrue(firstTask.hasSameDetailsAs(secondTask),
                "Cosmetic text differences and status should not make a distinct task.");
        assertFalse(firstTask.hasSameDetailsAs(new EventTask("read book", "2pm", "3pm")),
                "Different task types should remain distinct.");
    }
}
