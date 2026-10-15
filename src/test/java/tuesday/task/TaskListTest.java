package tuesday.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

/**
 * Tests duplicate lookup and replacement in a task list.
 */
public class TaskListTest {
    @Test
    public void indexOfDuplicate_multipleMatches_returnsFirstMatch() {
        TaskList tasks = new TaskList();
        Task firstTask = new Todo("Read Book");
        Task secondTask = new Todo("read    book");
        tasks.add(firstTask);
        tasks.add(secondTask);

        assertEquals(0, tasks.indexOfDuplicate(new Todo("read book")));
    }

    @Test
    public void indexOfDuplicate_noMatch_returnsNegativeOne() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        assertEquals(-1, tasks.indexOfDuplicate(new Todo("write book")));
    }

    @Test
    public void replace_validIndex_replacesInPlace() {
        TaskList tasks = new TaskList();
        Task firstTask = new Todo("first");
        Task oldTask = new Todo("old");
        Task newTask = new Todo("new");
        tasks.add(firstTask);
        tasks.add(oldTask);

        assertSame(oldTask, tasks.replace(1, newTask));
        assertSame(firstTask, tasks.get(0));
        assertSame(newTask, tasks.get(1));
        assertEquals(2, tasks.size());
    }
}
