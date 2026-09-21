package caitlyn;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Tests task state changes, display output, and escaped storage output. */
class TaskTest {
    @Test
    void task_stateChangesAreReflectedInDisplayAndStorage() {
        Task task = new Task("finish report");

        assertEquals("finish report", task.getDescription());
        assertFalse(task.isDone());
        assertEquals("[T][ ] finish report", task.toString());
        assertEquals("T | 0 | finish report", task.toStorageString());

        task.markAsDone();

        assertTrue(task.isDone());
        assertEquals("[T][X] finish report", task.toString());
        assertEquals("T | 1 | finish report", task.toStorageString());

        task.markAsNotDone();

        assertFalse(task.isDone());
        assertEquals("[T][ ] finish report", task.toString());
    }

    @Test
    void toStorageString_escapesCharactersThatHaveStorageMeaning() {
        String description = "review | notes\\today\nnext\rline";
        Task task = new Task(description);

        assertEquals("T | 0 | review \\| notes\\\\today\\nnext\\rline",
                task.toStorageString());
    }

    @Test
    void task_rejectsNullDescription() {
        assertThrows(IllegalArgumentException.class, () -> new Task(null));
    }

    @Test
    void escapeStorageField_nullField_rejectsMissingData() {
        assertThrows(IllegalArgumentException.class, () -> Task.escapeStorageField(null));
    }
}
