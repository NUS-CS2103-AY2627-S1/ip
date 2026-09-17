package kia.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for task completion state transitions.
 */
class TaskTest {
    /** Verifies that task construction asserts the required description invariant. */
    @Test
    void taskConstructor_nullDescription_assertsInvariant() {
        assertThrows(AssertionError.class, () -> new Task(null));
    }

    /** Verifies that a deadline asserts the presence of a parsed date. */
    @Test
    void deadlineConstructor_nullDate_assertsInvariant() {
        assertThrows(AssertionError.class, () -> new Deadline("submit report", (LocalDate) null));
    }

    /** Verifies that an event asserts non-empty start and end values. */
    @Test
    void eventConstructor_blankBoundary_assertsInvariant() {
        assertThrows(AssertionError.class, () -> new Event("meeting", " ", "4pm"));
    }

    /** Verifies that a new task reports the pending status. */
    @Test
    void getStatusIcon_newTask_returnsPendingIcon() {
        Task task = new Task("read book");

        assertEquals(TaskStatus.PENDING, task.getStatus());
        assertEquals(" ", task.getStatusIcon());
    }

    /** Verifies that to-do tasks expose the to-do type icon. */
    @Test
    void getTypeIcon_todoTask_returnsTodoIcon() {
        Task task = new Task("read book");

        assertEquals("T", task.getTypeIcon());
    }

    /** Verifies that keyword matching ignores case and supports substrings. */
    @Test
    void matchesDescription_keywordMatchesIgnoringCase_returnsTrue() {
        Task task = new Task("Read the BOOK");

        assertTrue(task.matchesDescription("book"));
        assertTrue(task.matchesDescription("READ"));
    }

    /** Verifies that an absent keyword does not match the description. */
    @Test
    void matchesDescription_keywordAbsent_returnsFalse() {
        Task task = new Task("read book");

        assertFalse(task.matchesDescription("bread"));
    }

    /** Verifies that null and blank keywords do not match every task. */
    @Test
    void matchesDescription_nullOrBlankKeyword_returnsFalse() {
        Task task = new Task("read book");

        assertFalse(task.matchesDescription(null));
        assertFalse(task.matchesDescription("   "));
    }

    /** Verifies the transition from pending to done. */
    @Test
    void markAsDone_pendingTask_marksItDone() {
        Task task = new Task("read book");

        task.markAsDone();

        assertEquals(TaskStatus.DONE, task.getStatus());
        assertEquals("X", task.getStatusIcon());
    }

    /** Verifies that marking an already-done task is idempotent. */
    @Test
    void markAsDone_doneTask_remainsDone() {
        Task task = new Task("read book");
        task.markAsDone();

        task.markAsDone();

        assertEquals(TaskStatus.DONE, task.getStatus());
        assertEquals("X", task.getStatusIcon());
    }

    /** Verifies the transition from done back to pending. */
    @Test
    void markAsUndone_doneTask_marksItPending() {
        Task task = new Task("read book");
        task.markAsDone();

        task.markAsUndone();

        assertEquals(TaskStatus.PENDING, task.getStatus());
        assertEquals(" ", task.getStatusIcon());
    }

    /** Verifies that unmarking an already-pending task is idempotent. */
    @Test
    void markAsUndone_pendingTask_remainsPending() {
        Task task = new Task("read book");

        task.markAsUndone();

        assertEquals(TaskStatus.PENDING, task.getStatus());
        assertEquals(" ", task.getStatusIcon());
    }

    /** Verifies that explicit status restoration updates the task state. */
    @Test
    void setStatus_doneStatus_updatesTaskState() {
        Task task = new Task("read book");

        task.setStatus(TaskStatus.DONE);

        assertEquals(TaskStatus.DONE, task.getStatus());
        assertEquals("X", task.getStatusIcon());
    }

    /** Verifies that a task cannot be restored to an undefined status. */
    @Test
    void setStatus_nullStatus_assertsInvariant() {
        Task task = new Task("read book");

        assertThrows(AssertionError.class, () -> task.setStatus(null));
    }

    /** Verifies the pending task display format. */
    @Test
    void toString_pendingTask_includesPendingMarkerAndDescription() {
        Task task = new Task("read book");

        assertEquals("[ ] read book", task.toString());
    }

    /** Verifies the completed task display format. */
    @Test
    void toString_doneTask_includesDoneMarkerAndDescription() {
        Task task = new Task("read book");
        task.markAsDone();

        assertEquals("[X] read book", task.toString());
    }

    /** Verifies serialization of an incomplete task. */
    @Test
    void toStorageString_pendingTask_usesPendingStatusRecord() {
        Task task = new Task("read book");

        assertEquals("T | 0 | read book", task.toStorageString());
    }

    /** Verifies serialization of a completed task. */
    @Test
    void toStorageString_doneTask_usesDoneStatusRecord() {
        Task task = new Task("read book");
        task.markAsDone();

        assertEquals("T | 1 | read book", task.toStorageString());
    }
}
