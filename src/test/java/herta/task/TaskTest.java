package herta.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/**
 * Tests the common completion, scheduling, and formatting behavior of a base task.
 */
class TaskTest {

    @Test
    void task_nullOrBlankDescription_rejectsInputWithoutAssertions() {
        assertThrows(NullPointerException.class, () -> new TestTask(null));
        assertThrows(IllegalArgumentException.class, () -> new TestTask("   "));
    }

    @Test
    void task_invalidDescriptionCharacters_rejectAtDomainBoundary() {
        String overlongDescription = "a".repeat(TaskDescriptionValidator.MAX_DESCRIPTION_LENGTH + 1);

        assertThrows(IllegalArgumentException.class, () -> new TestTask("bad | task"));
        assertThrows(IllegalArgumentException.class, () -> new TestTask("bad\u0000task"));
        assertThrows(IllegalArgumentException.class, () -> new TestTask(overlongDescription));
        assertThrows(IllegalArgumentException.class, () -> new TestTask("bad\uD800"));
        assertThrows(IllegalArgumentException.class, () -> new TestTask("bad\uDC00"));
    }

    @Test
    void taskStatusAndFormatting_changeAfterCompletion() {
        Task task = new TestTask("plain task");

        assertEquals("plain task", task.getDescription());
        assertEquals(" ", task.getStatusIcon());
        assertFalse(task.isCompleted());
        assertFalse(task.occursOn(LocalDate.of(2019, 10, 15)));
        assertTrue(task.getScheduledDateTime().isEmpty());
        assertFalse(task.isUpcoming(LocalDateTime.MIN, LocalDateTime.MAX));
        assertEquals("T | 0 | plain task", task.toStorageString());
        assertEquals("[ ] plain task", task.toString());

        task.markAsDone();

        assertTrue(task.isCompleted());
        assertEquals("X", task.getStatusIcon());
        assertEquals("T | 1 | plain task", task.toStorageString());
        assertEquals("[X] plain task", task.toString());

        task.markAsNotDone();
        assertFalse(task.isCompleted());
    }

    @Test
    void isUpcoming_windowBoundariesAndUnscheduledTask_followDocumentedRules() {
        LocalDateTime windowStart = LocalDateTime.of(2025, 1, 1, 10, 0);
        LocalDateTime windowEnd = LocalDateTime.of(2025, 1, 2, 10, 0);
        Deadline deadline = new Deadline("deadline", windowStart);

        assertTrue(deadline.isUpcoming(windowStart, windowEnd));
        assertFalse(deadline.isUpcoming(windowEnd, windowEnd));
        assertFalse(deadline.isUpcoming(windowStart, windowStart));
        assertFalse(new Todo("unscheduled").isUpcoming(windowStart, windowEnd));
        assertThrows(NullPointerException.class, () -> deadline.isUpcoming(null, windowEnd));
        assertThrows(NullPointerException.class, () -> deadline.isUpcoming(windowStart, null));
        assertThrows(IllegalArgumentException.class, () ->
                deadline.isUpcoming(windowEnd, windowStart));
    }

    /**
     * Provides a concrete implementation for testing the abstract base class.
     */
    private static final class TestTask extends Task {
        TestTask(String description) {
            super(description);
        }

        @Override
        public String toStorageString() {
            return "T | " + getCompletionStatusCode() + " | " + getDescription();
        }
    }
}
