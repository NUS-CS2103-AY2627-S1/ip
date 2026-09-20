package duchess.task;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;

import org.junit.jupiter.api.Test;

/** Tests the shared state and display behavior of a task. */
public class TaskTest {
    /** Verifies that a new task starts unfinished with the expected type and display. */
    @Test
    public void task_constructor_setsTodoDefaults() {
        Task task = new Task("read book");

        assertAll(
                () -> assertEquals(TaskType.TODO, task.getType()),
                () -> assertFalse(task.isDone()),
                () -> assertEquals(" ", task.getStatusIcon()),
                () -> assertNull(task.getCompletedAt()),
                () -> assertEquals("[T][ ] read book", task.toString())
        );
    }

    /** Verifies that marking a task stores the first known completion time. */
    @Test
    public void task_markAsDone_preservesFirstCompletionTime() {
        Instant firstCompletion = Instant.parse("2026-09-01T00:00:00Z");
        Instant secondCompletion = Instant.parse("2026-09-11T00:00:00Z");
        Task task = new Task("read book");

        task.markAsDone(firstCompletion);
        task.markAsDone(secondCompletion);

        assertAll(
                () -> assertTrue(task.isDone()),
                () -> assertEquals("X", task.getStatusIcon()),
                () -> assertEquals(firstCompletion, task.getCompletedAt())
        );
    }

    /** Verifies that a known time can replace a missing legacy completion time. */
    @Test
    public void task_unknownCompletion_storesLaterKnownTime() {
        Instant knownCompletion = Instant.parse("2026-09-11T00:00:00Z");
        Task task = new Task("legacy task");
        task.markAsDone(null);

        task.markAsDone(knownCompletion);

        assertTrue(task.isDone());
        assertEquals(knownCompletion, task.getCompletedAt());
    }

    /** Verifies that unmarking a task clears both completion state and timestamp. */
    @Test
    public void task_markAsNotDone_clearsCompletionState() {
        Task task = new Task("read book");
        task.markAsDone(Instant.parse("2026-09-01T00:00:00Z"));

        task.markAsNotDone();

        assertAll(
                () -> assertFalse(task.isDone()),
                () -> assertEquals(" ", task.getStatusIcon()),
                () -> assertNull(task.getCompletedAt())
        );
    }

    /** Verifies assertion-based constructor preconditions. */
    @Test
    public void task_invalidConstructorArguments_throwAssertionError() {
        assertAll(
                () -> assertThrows(AssertionError.class, () -> new Task(null)),
                () -> assertThrows(AssertionError.class, () -> new Task("   ")),
                () -> assertThrows(AssertionError.class, () -> new Task("task", null))
        );
    }
}
