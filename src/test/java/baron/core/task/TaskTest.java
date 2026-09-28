package baron.core.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import baron.core.exception.BaronException;

/**
 * Tests shared task behavior through {@link Todo}.
 */
class TaskTest {

    /**
     * Verifies that a task's completion state and status icon change together.
     */
    @Test
    void markAndUnmarkAsDone_taskWithoutDependencies_statusChanged() throws BaronException {
        Todo task = todo("00000000-0000-0000-0000-000000000001", "read notes");

        assertSame(task, task.markAsDone());
        assertTrue(task.isDone());
        assertEquals("X", task.getStatusIcon());
        assertSame(task, task.markAsNotDone());
        assertFalse(task.isDone());
        assertEquals(" ", task.getStatusIcon());
    }

    /**
     * Verifies that keyword matching uses the task description and is case-sensitive.
     */
    @Test
    void hasKeyword_matchingAndNonMatchingKeywords_expectedResultReturned() {
        Todo task = todo("00000000-0000-0000-0000-000000000001", "Read CS2103 notes");

        assertTrue(task.hasKeyword("CS2103"));
        assertTrue(task.hasKeyword(""));
        assertFalse(task.hasKeyword("cs2103"));
        assertFalse(task.hasKeyword("slides"));
    }

    /**
     * Verifies that an incomplete required task prevents completion of its unlocked task.
     */
    @Test
    void markAsDone_incompleteRequiredTask_baronExceptionThrown() throws BaronException {
        Todo requiredTask = todo("00000000-0000-0000-0000-000000000001", "required task");
        Todo unlockedTask = todo("00000000-0000-0000-0000-000000000002", "unlocked task");
        unlockedTask.setRequiredTasks(
                new TaskList(List.of(requiredTask, unlockedTask)), new TaskList(List.of(requiredTask)));

        TaskList tasks = new TaskList(List.of(requiredTask, unlockedTask));
        assertThrows(BaronException.class, () -> tasks.markTask(1));
        assertFalse(unlockedTask.isDone());
    }

    /**
     * Verifies that a task's serialized state includes its identifier, type, completion state, and
     * description.
     */
    @Test
    void toFileString_taskMarkedDone_expectedPersistentFormatReturned() throws BaronException {
        Todo task = todo("00000000-0000-0000-0000-000000000001", "read notes");
        task.markAsDone();

        assertEquals(
                "00000000-0000-0000-0000-000000000001 | T | 1 | read notes | ",
                task.toFileString());
    }

    /**
     * Creates a to-do task with a predictable identifier for test assertions.
     */
    private Todo todo(String uuid, String description) {
        return new Todo(UUID.fromString(uuid), description);
    }
}
