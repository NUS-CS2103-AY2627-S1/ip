package luna.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests validation, status changes, and serialization in {@link Task}.
 */
public class TaskTest {
    @Test
    public void constructor_descriptionHasOuterWhitespace_trimsDescription() {
        Task task = new Task("  read book  ");

        assertEquals("read book", task.getDescription());
        assertEquals("[ ] read book", task.toString());
    }

    @Test
    public void constructor_blankDescription_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Task("   "));
        assertThrows(IllegalArgumentException.class, () -> new Task(null));
    }

    @Test
    public void constructor_descriptionExceedsLimit_throwsIllegalArgumentException() {
        String description = "a".repeat(501);

        assertThrows(IllegalArgumentException.class, () -> new Task(description));
    }

    @Test
    public void constructor_descriptionHasControlCharacter_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Task("read\tbook"));
    }

    @Test
    public void markAndUnmark_task_changesStatusAndStorageText() {
        Task task = new Task("read book");

        task.markAsDone();
        assertTrue(task.isDone());
        assertEquals("[X]", task.getStatusIcon());
        assertEquals("T | 1 | read book", task.toStorageString());

        task.markAsNotDone();
        assertFalse(task.isDone());
        assertEquals("T | 0 | read book", task.toStorageString());
    }

    @Test
    public void hasSameDetails_caseAndSpacingDiffer_returnsTrue() {
        Task firstTask = new Task("Read   Book");
        Task secondTask = new Task("read book");

        assertTrue(firstTask.hasSameDetails(secondTask));
    }

    @Test
    public void hasSameDetails_typeDescriptionOrNullDiffers_returnsFalse() {
        Task task = new Task("read book");

        assertFalse(task.hasSameDetails(new Todo("read book")));
        assertFalse(task.hasSameDetails(new Task("submit quiz")));
        assertFalse(task.hasSameDetails(null));
    }
}
