package niulai.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Tests task state changes and the common task representation. */
class TaskTest {
    /** Verifies that a task starts pending and can be marked and unmarked. */
    @Test
    void taskStatus_markAndUnmark_updatesStatusAndIcon() {
        Task task = new Todo("read book");

        assertEquals(TaskStatus.PENDING, task.getStatus());
        assertEquals(" ", task.getStatusIcon());

        task.markAsDone();
        assertEquals(TaskStatus.COMPLETED, task.getStatus());
        assertEquals("X", task.getStatusIcon());

        task.markAsNotDone();
        assertEquals(TaskStatus.PENDING, task.getStatus());
        assertEquals(" ", task.getStatusIcon());
    }

    /** Verifies that descriptions are trimmed and special storage characters are escaped. */
    @Test
    void taskRepresentation_trimmedDescriptionAndEscapedStorage() {
        Task task = new Todo("  read | review \\ draft  ");

        assertEquals("read | review \\ draft", task.getDescription());
        assertEquals("[T][ ] read | review \\ draft", task.toString());
        assertEquals("T | 0 | read \\| review \\\\ draft", task.toStorageString());
    }

    /** Verifies that blank descriptions are rejected. */
    @Test
    void task_blankDescription_exceptionThrown() {
        assertThrows(IllegalArgumentException.class, () -> new Todo("  "));
        assertThrows(IllegalArgumentException.class, () -> new Todo(null));
    }

    /** Verifies that horizontal whitespace is canonicalized in task fields. */
    @Test
    void task_repeatedHorizontalWhitespace_collapsedToSingleSpaces() {
        Task task = new Todo("\t read   the\tbook  ");

        assertEquals("read the book", task.getDescription());
    }

    /** Verifies that line breaks and other control characters cannot enter task fields. */
    @Test
    void task_controlCharacterInDescription_exceptionThrown() {
        assertThrows(IllegalArgumentException.class, () -> new Todo("first\nsecond"));
        assertThrows(IllegalArgumentException.class, () -> new Todo("first\u0000second"));
    }

    /** Verifies that task identity accounts for null, type, description, and task details. */
    @Test
    void hasSameIdentity_taskVariants_returnsExpectedResult() {
        Task todo = new Todo("Read Book");

        assertTrue(todo.hasSameIdentity(new Todo("read book")));
        assertFalse(todo.hasSameIdentity(null));
        assertFalse(todo.hasSameIdentity(new Deadline("read book", "tomorrow")));
        assertFalse(todo.hasSameIdentity(new Todo("return book")));
    }

    /** Verifies all persisted task-type markers and rejects unknown markers. */
    @Test
    void taskTypeFromIcon_supportedAndUnknownIcons_returnsTypeOrThrows() {
        assertEquals(Task.TaskType.TODO, Task.TaskType.fromIcon("T"));
        assertEquals(Task.TaskType.DEADLINE, Task.TaskType.fromIcon("D"));
        assertEquals(Task.TaskType.EVENT, Task.TaskType.fromIcon("E"));
        assertThrows(IllegalArgumentException.class, () -> Task.TaskType.fromIcon("X"));
        assertThrows(IllegalArgumentException.class, () -> Task.TaskType.fromIcon(null));
    }
}
