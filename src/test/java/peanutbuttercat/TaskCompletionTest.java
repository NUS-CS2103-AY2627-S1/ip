package peanutbuttercat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/** Tests transitions between incomplete and completed task states. */
class TaskCompletionTest {
    @Test
    void markAsDone_incompleteTask_recordsCompletionDate() {
        Task task = new Todo("read book");

        task.markAsDone(LocalDate.of(2026, 9, 9));

        assertTrue(task.isDone());
        assertEquals(LocalDate.of(2026, 9, 9), task.getCompletionDate());
    }

    @Test
    void markAsDone_completedTask_preservesOriginalCompletionDate() {
        Task task = new Todo("read book");
        task.markAsDone(LocalDate.of(2026, 9, 8));

        task.markAsDone(LocalDate.of(2026, 9, 9));

        assertEquals(LocalDate.of(2026, 9, 8), task.getCompletionDate());
    }

    @Test
    void markAsNotDone_completedTask_clearsCompletionDate() {
        Task task = new Todo("read book");
        task.markAsDone(LocalDate.of(2026, 9, 8));

        task.markAsNotDone();

        assertFalse(task.isDone());
        assertNull(task.getCompletionDate());
    }
}
