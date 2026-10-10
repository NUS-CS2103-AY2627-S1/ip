package zsiggy;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import zsiggy.task.Task;
import zsiggy.task.Todo;

/**
 * Verifies search behavior without depending on its implementation.
 */
public class TaskListTest {
    @Test
    public void find_emptyList_returnsEmptyArray() {
        assertArrayEquals(new Task[0], new TaskList().find("book"));
    }

    @Test
    public void find_mixedTasks_preservesOrderAndCaseSensitivity() {
        TaskList tasks = new TaskList();
        Task first = new Todo("read book");
        Task second = new Todo("buy book");
        tasks.add(first);
        tasks.add(new Todo("Book flight"));
        tasks.add(second);
        assertArrayEquals(new Task[]{first, second}, tasks.find("book"));
        assertArrayEquals(new Task[0], tasks.find("missing"));
        assertEquals(3, tasks.getTaskCount());
    }

    @Test
    public void find_deletedTask_excludesUnusedSlots() {
        TaskList tasks = new TaskList();
        Task kept = new Todo("book flight");
        tasks.add(new Todo("read book"));
        tasks.add(kept);
        tasks.delete(0);
        assertArrayEquals(new Task[]{kept}, tasks.find("book"));
    }
}
