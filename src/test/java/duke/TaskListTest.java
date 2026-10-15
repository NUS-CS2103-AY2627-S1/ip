package duke;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Tests task-list ownership and one-based task operations. */
public class TaskListTest {
    @Test
    public void addAndGet_returnsTaskAtOneBasedPosition() {
        TaskList tasks = new TaskList();
        Task task = new Todo("read book");

        assertTrue(tasks.add(task));
        assertTrue(tasks.contains(1));
        assertEquals(task, tasks.get(1));
        assertEquals(1, tasks.size());
    }

    @Test
    public void remove_decreasesSizeAndInvalidatesPosition() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Todo("write report"));

        assertEquals("read book", tasks.remove(1).getDescription());
        assertEquals(1, tasks.size());
        assertFalse(tasks.contains(2));
    }

    @Test
    public void add_rejectsTasksAfterCapacityLimit() {
        TaskList tasks = new TaskList();
        for (int i = 0; i < 100; i++) {
            assertTrue(tasks.add(new Todo("task " + i)));
        }

        assertFalse(tasks.add(new Todo("overflow")));
        assertEquals(100, tasks.size());
    }

    @Test
    public void containsEquivalent_sameTaskIgnoringCase_returnsTrue() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("Read Book"));

        assertTrue(tasks.containsEquivalent(new Todo("read book")));
        assertFalse(tasks.containsEquivalent(new Todo("write report")));
    }
}
