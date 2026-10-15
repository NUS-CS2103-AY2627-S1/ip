package zikiai.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Tests the base task state and fallback representations.
 */
class TaskTest {

    @Test
    void constructor_nullDescription_assertionErrorThrown() {
        assertThrows(AssertionError.class, () -> new Task(null));
    }

    @Test
    void stateChanges_genericTask_displayAndStorageRemainConsistent() {
        Task task = new Task("generic task");

        assertEquals(" ", task.getStatusIcon());
        assertEquals("[ ] generic task", task.getDescription());
        assertEquals("[?][ ] | generic task", task.toDataString());

        task.markAsDone();
        task.setPriority(Priority.LOW);

        assertEquals("X", task.getStatusIcon());
        assertEquals("[LOW][X] generic task", task.getDescription());
        assertEquals("[?][X][L] | generic task", task.toDataString());
    }
}
