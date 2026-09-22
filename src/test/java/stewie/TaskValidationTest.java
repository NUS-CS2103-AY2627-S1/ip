package stewie;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import stewie.model.Deadline;
import stewie.model.Event;
import stewie.model.Task;
import stewie.model.ToDo;

/**
 * Verifies task invariants shared by creation, updates, and storage loading.
 */
public class TaskValidationTest {
    /**
     * Verifies equal and reversed event boundaries are rejected.
     */
    @Test
    public void createEvent_requiresIncreasingDates() {
        assertThrows(IllegalArgumentException.class, () -> new Event("trip", "2026-01-01", "2026-01-01"));
        assertThrows(IllegalArgumentException.class, () -> new Event("trip", "2026-01-02", "2026-01-01"));
    }

    /**
     * Verifies descriptions cannot inject extra fields or records into storage.
     */
    @Test
    public void createTask_rejectsStorageSeparators() {
        for (String description : new String[] {"", "   ", "a|b", "a\nb", "a\u0000b"}) {
            assertThrows(IllegalArgumentException.class, () -> new ToDo(description));
        }
    }

    /**
     * Verifies identity uses normalized details and dates but ignores completion status.
     */
    @Test
    public void compareTasks_usesDetails() {
        Task completed = new ToDo("Read  Book");
        completed.markAsDone();
        assertTrue(completed.hasSameDetails(new ToDo("read book")));
        assertTrue(new Deadline("report", "2026-01-01")
                .hasSameDetails(new Deadline("report", "1/1/2026")));
        assertFalse(new Deadline("report", "2026-01-01")
                .hasSameDetails(new Deadline("report", "2026-01-02")));
        assertFalse(completed.hasSameDetails(new Deadline("read book", "2026-01-01")));
    }
}
