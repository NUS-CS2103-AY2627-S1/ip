package bingus.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests common behaviour defined by {@link Task}.
 */
class TaskTest {

    /**
     * Verifies that a base task displays its description and never matches a date.
     */
    @Test
    void getDisplayString_andMatchesDate_returnsExpectedValues() {
        Task task = new Task("read book", TaskType.TODO);

        assertEquals("read book", task.getDescription());
        assertEquals(TaskType.TODO, task.getType());
        assertEquals("[T][○] read book", task.getDisplayString());
        assertFalse(task.matchesDate(LocalDate.of(2026, 9, 8)));
    }
}
