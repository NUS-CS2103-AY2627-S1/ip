package friday.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests the common behavior inherited by all task types.
 */
class TaskTest {
    /**
     * Verifies that task completion changes the displayed status icon.
     */
    @Test
    void markAndUnmark_updatesStatusIconAndDisplay() {
        Task task = new Task("review notes");

        assertEquals(" ", task.getStatusIcon());
        assertEquals("[ ] review notes", task.toString());

        task.markAsDone();
        assertEquals("X", task.getStatusIcon());
        assertEquals("[X] review notes", task.toString());

        task.unmarkAsDone();
        assertEquals(" ", task.getStatusIcon());
    }

    /**
     * Verifies that the base task serialization escapes file delimiters.
     */
    @Test
    void toFileString_escapesBackslashesAndPipes() {
        Task task = new Task("path\\notes | review");

        assertEquals("0 | path\\\\notes \\| review", task.toFileString());
    }

    /**
     * Verifies that task detail comparisons require the same type and description.
     */
    @Test
    void hasSameDetails_requiresSameTypeAndDescription() {
        Task task = new Task("same");

        assertTrue(task.hasSameDetails(new Task("same")));
        assertFalse(task.hasSameDetails(new Task("different")));
        assertFalse(task.hasSameDetails(null));
        assertFalse(task.hasSameDetails(new ToDo("same")));
    }
}
