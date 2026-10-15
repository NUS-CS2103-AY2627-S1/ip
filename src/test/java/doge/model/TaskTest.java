package doge.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests the public behavior of {@link Task}.
 *
 * <p>Each test follows the same simple pattern: arrange the objects,
 * perform an action, and assert the result.</p>
 */
class TaskTest {

    /** Verifies that a new task starts incomplete. */
    @Test
    void constructor_newTask_startsIncomplete() {
        Task task = new Task("Read about JUnit");

        assertFalse(task.isDone());
    }

    /** Verifies the status icon for an incomplete task. */
    @Test
    void getStatusIcon_taskNotDone_returnsBlankIcon() {
        Task task = new Task("Read about JUnit");

        assertEquals(" ", task.getStatusIcon());
    }

    /** Verifies the status icon for a completed task. */
    @Test
    void getStatusIcon_taskDone_returnsXIcon() {
        Task task = new Task("Read about JUnit");
        task.markDone();

        assertEquals("X", task.getStatusIcon());
    }

    /** Verifies the display of an incomplete task. */
    @Test
    void toString_taskNotDone_includesBlankStatusAndDescription() {
        Task task = new Task("Read about JUnit");

        assertEquals("[ ] Read about JUnit", task.toString());
    }

    /** Verifies the display of a completed task. */
    @Test
    void toString_taskDone_includesXStatusAndDescription() {
        Task task = new Task("Read about JUnit");
        task.markDone();

        assertEquals("[X] Read about JUnit", task.toString());
    }

    /** Verifies that marking an incomplete task completes it. */
    @Test
    void markDone_incompleteTask_marksTaskComplete() {
        Task task = new Task("Read about JUnit");

        task.markDone();

        assertTrue(task.isDone());
    }

    /** Verifies that marking an already completed task has no adverse effect. */
    @Test
    void markDone_alreadyCompleteTask_remainsComplete() {
        Task task = new Task("Read about JUnit");
        task.markDone();

        task.markDone();

        assertTrue(task.isDone());
    }

    /** Verifies that an explicitly completed task can be made incomplete. */
    @Test
    void unmarkDone_completeTask_marksTaskIncomplete() {
        Task task = new Task("Read about JUnit");
        task.markDone();

        task.unmarkDone();

        assertFalse(task.isDone());
    }

    /** Verifies that unmarking an incomplete task leaves it incomplete. */
    @Test
    void unmarkDone_incompleteTask_remainsIncomplete() {
        Task task = new Task("Read about JUnit");

        task.unmarkDone();

        assertFalse(task.isDone());
    }

    /** Verifies that a task returns its original description. */
    @Test
    void getDescription_taskCreatedWithDescription_returnsDescription() {
        Task task = new Task("Read about JUnit");

        assertEquals("Read about JUnit", task.getDescription());
    }

    /** Verifies the completion status of a new task. */
    @Test
    void isDone_newTask_returnsFalse() {
        Task task = new Task("Read about JUnit");

        assertFalse(task.isDone());
    }

    /** Verifies the completion status after marking a task done. */
    @Test
    void isDone_taskMarkedDone_returnsTrue() {
        Task task = new Task("Read about JUnit");
        task.markDone();

        assertTrue(task.isDone());
    }

    /** Verifies that a new task has no priority. */
    @Test
    void getPriority_newTask_returnsNoPriority() {
        Task task = new Task("Read about JUnit");

        assertEquals(0, task.getPriority());
    }

    /** Verifies that a priority level can be attached to a task. */
    @Test
    void setPriority_taskProvided_setsPriorityLevel() {
        Task task = new Task("Read about JUnit");

        task.setPriority(1);

        assertEquals(1, task.getPriority());
        assertEquals("[ ][P1] Read about JUnit", task.toString());
    }

    /** Verifies the persisted representation of an incomplete task. */
    @Test
    void toStorageString_incompleteTask_returnsTaskStorageFormat() {
        Task task = new Task("Read about JUnit");

        assertEquals("T | 0 | Read about JUnit", task.toStorageString());
    }

    /** Verifies the persisted representation of a completed task. */
    @Test
    void toStorageString_completeTask_returnsTaskStorageFormat() {
        Task task = new Task("Read about JUnit");
        task.markDone();

        assertEquals("T | 1 | Read about JUnit", task.toStorageString());
    }

    /** Verifies that a set priority is included in the persisted representation. */
    @Test
    void toStorageString_prioritizedTask_includesPriorityLevel() {
        Task task = new Task("Read about JUnit");
        task.setPriority(1);

        assertEquals("T | 0 | Read about JUnit | 1", task.toStorageString());
    }

    /** Verifies that a null description is rejected even when assertions are disabled. */
    @Test
    void constructor_nullDescription_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Task(null));
    }

    /** Verifies that a negative priority is rejected. */
    @Test
    void setPriority_negativePriority_throwsIllegalArgumentException() {
        Task task = new Task("Read about JUnit");

        assertThrows(IllegalArgumentException.class, () -> task.setPriority(-1));
    }
}
