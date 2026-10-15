package et.task;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests the shared task status display.
 */
class TaskTest {
    @Test
    void toString_taskIsMarked_displaysTickStatus() {
        Task task = new Todo("phone home");

        task.markAsDone();

        assertEquals("[T][✓] phone home", task.toString());
    }
}
