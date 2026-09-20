package fifi.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/**
 * Tests the base task behavior shared by all task types.
 */
public class TaskTest {

    @Test
    public void mark_unmarkedTask_taskMarked() {
        LocalDateTime oldCompletionTime = LocalDateTime.of(2025, 1, 1, 12, 0);
        Task task = new TestTask(false, "read book", LocalDateTime.of(2025, 1, 1, 9, 0), oldCompletionTime);

        task.mark();

        assertTrue(task.isMarked());
        assertTrue(task.getLastMarkedAt().orElseThrow().isAfter(oldCompletionTime));
        assertEquals("[X] read book", task.toString());
        assertEquals("1 | read book", task.toFileString());
    }

    @Test
    public void unmark_markedTask_taskUnmarked() {
        LocalDateTime completionTime = LocalDateTime.of(2025, 1, 1, 12, 0);
        Task task = new TestTask(true, "read book", LocalDateTime.of(2025, 1, 1, 9, 0), completionTime);

        task.unmark();

        assertFalse(task.isMarked());
        assertEquals(completionTime, task.getLastMarkedAt().orElseThrow());
        assertEquals("[ ] read book", task.toString());
        assertEquals("0 | read book", task.toFileString());
    }

    @Test
    public void toString_unmarkedTask_unmarkedTaskStringReturned() {
        Task task = new TestTask(false, "read book");

        assertEquals("[ ] read book", task.toString());
    }

    @Test
    public void toFileString_markedTask_markedStorageStringReturned() {
        Task task = new TestTask(true, "read book");

        assertEquals("1 | read book", task.toFileString());
    }

    @Test
    public void mark_unmarkedAgainThenRemarked_completionTimeReplaced() {
        Task task = new TestTask(false, "read book", LocalDateTime.of(2025, 1, 1, 9, 0), null);

        task.mark();
        LocalDateTime firstCompletionTime = task.getLastMarkedAt().orElseThrow();
        task.unmark();
        task.mark();

        assertNotEquals(LocalDateTime.of(2025, 1, 1, 9, 0), task.getLastMarkedAt().orElseThrow());
        assertFalse(task.getLastMarkedAt().orElseThrow().isBefore(firstCompletionTime));
    }

    @Test
    public void occursOn_basicTask_falseForAnyDate() {
        Task task = new TestTask(false, "read book");

        assertFalse(task.occursOn(LocalDate.of(2019, 12, 2)));
    }

    @Test
    public void constructor_invalidDescriptionOrTimestampOrder_rejectedAndValidTaskStillWorks() {
        LocalDateTime createdAt = LocalDateTime.of(2026, 9, 20, 9, 0);
        String[] descriptions = {"", "  ", "work|book", "work\nbook", "work\u0000book"};
        for (String description : descriptions) {
            assertThrows(IllegalArgumentException.class, () -> new ToDo(false, description));
            Task validTask = new ToDo(false, "ordinary work");
            validTask.mark();
            assertTrue(validTask.isMarked());
        }
        assertThrows(IllegalArgumentException.class, () ->
                new ToDo(true, "work", createdAt, createdAt.minusSeconds(1)));
        Task task = new ToDo(true, "work", createdAt, createdAt);
        assertEquals(createdAt, task.getLastMarkedAt().orElseThrow());
    }

    @Test
    public void constructor_invalidTaskDates_rejectedWhileSameDayEventsRemainValid() {
        LocalDate date = LocalDate.of(2026, 9, 20);
        assertThrows(IllegalArgumentException.class, () -> new Deadline(false, "work", LocalDate.of(0, 1, 1)));
        assertThrows(IllegalArgumentException.class, () -> new Deadline(false, "work", LocalDate.of(10000, 1, 1)));
        assertThrows(IllegalArgumentException.class, () -> new Event(false, "work", date, date.minusDays(1)));
        assertTrue(new Event(false, "work", date, date).occursOn(date));
    }

    /**
     * Provides the smallest concrete task needed to test shared task behavior.
     */
    private static class TestTask extends Task {
        private TestTask(boolean isMarked, String name) {
            super(isMarked, name);
        }

        private TestTask(boolean isMarked, String name, LocalDateTime createdAt, LocalDateTime lastMarkedAt) {
            super(isMarked, name, createdAt, lastMarkedAt);
        }

        @Override
        public String toFileString() {
            return String.format("%s | %s", getMarkedStatus(), getDescription());
        }

        @Override
        public Task copy() {
            return new TestTask(isMarked(), getDescription(), getCreatedAt().orElse(null),
                    getLastMarkedAt().orElse(null));
        }
    }
}
