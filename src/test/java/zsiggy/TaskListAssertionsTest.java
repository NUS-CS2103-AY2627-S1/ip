package zsiggy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import zsiggy.task.Todo;

/**
 * Verifies internal task list assumptions with assertions enabled.
 */
public class TaskListAssertionsTest {
    @Test
    public void add_nullTask_rejectsInvalidState() {
        TaskList tasks = new TaskList();
        assertThrows(AssertionError.class, () -> tasks.add(null));
        assertEquals(0, tasks.getTaskCount());
    }

    @Test
    public void access_invalidIndex_rejectsUnusedSlots() {
        TaskList tasks = new TaskList();
        assertThrows(AssertionError.class, () -> tasks.get(0));
        assertThrows(AssertionError.class, () -> tasks.delete(0));
        assertThrows(AssertionError.class, () -> tasks.mark(-1));
        assertThrows(AssertionError.class, () -> tasks.unmark(0));
    }

    @Test
    public void add_fullList_preservesExistingTasks() {
        TaskList tasks = new TaskList();
        for (int i = 0; i < 100; i++) {
            tasks.add(new Todo("task " + i));
        }
        tasks.add(new Todo("overflow"));
        assertEquals(101, tasks.getTaskCount());
    }
}
