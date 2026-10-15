package tblade.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import tblade.exception.TBladeException;

/**
 * Tests TaskList's add/remove/get/size operations, its 100-task capacity limit, and its
 * rejection of duplicate tasks.
 */
public class TaskListTest {
    @Test
    public void add_belowCapacity_appendsTask() throws TBladeException {
        TaskList tasks = new TaskList();

        tasks.add(new Todo("read book"));

        assertEquals(1, tasks.size());
        assertEquals("read book", tasks.get(0).getDescription());
    }

    @Test
    public void add_atCapacity_throwsTBladeException() throws TBladeException {
        TaskList tasks = new TaskList();
        for (int i = 0; i < 100; i++) {
            tasks.add(new Todo("task " + i));
        }

        TBladeException exception = assertThrows(TBladeException.class, () -> tasks.add(new Todo("one too many")));

        assertEquals("The task list already has 100 tasks. You cannot add another task in this version.",
                exception.getMessage());
        assertEquals(100, tasks.size());
    }

    @Test
    public void add_duplicateTask_throwsTBladeException() throws TBladeException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        TBladeException exception = assertThrows(TBladeException.class, () -> tasks.add(new Todo("read book")));

        assertEquals("This task is already in your list: [T][ ] read book. "
                + "Use `list` to see it, or give this one a different description.", exception.getMessage());
        assertEquals(1, tasks.size());
    }

    @Test
    public void add_sameDescriptionDifferentTaskType_doesNotThrow() throws TBladeException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("return book"));

        tasks.add(new Deadline("return book", LocalDate.of(2026, 3, 15)));

        assertEquals(2, tasks.size());
    }

    @Test
    public void of_multipleTasks_containsThemInOrder() {
        TaskList tasks = TaskList.of(new Todo("first"), new Todo("second"));

        assertEquals(2, tasks.size());
        assertEquals("first", tasks.get(0).getDescription());
        assertEquals("second", tasks.get(1).getDescription());
    }

    @Test
    public void remove_existingIndex_returnsAndDeletesTask() {
        TaskList tasks = TaskList.of(new Todo("first"), new Todo("second"));

        Task removed = tasks.remove(0);

        assertEquals("first", removed.getDescription());
        assertEquals(1, tasks.size());
        assertEquals("second", tasks.get(0).getDescription());
    }

    @Test
    public void constructor_wrapsExistingList_sharesUnderlyingTasks() {
        List<Task> existing = new ArrayList<>();
        existing.add(new Todo("loaded task"));

        TaskList tasks = new TaskList(existing);

        assertEquals(1, tasks.size());
        assertSame(existing, tasks.getAll());
    }

    @Test
    public void find_matchingKeyword_returnsOnlyMatchingTasksInOrder() {
        TaskList tasks = TaskList.of(new Todo("read book"), new Todo("return laptop"), new Todo("return book"));

        TaskList matches = tasks.find("book");

        assertEquals(2, matches.size());
        assertEquals("read book", matches.get(0).getDescription());
        assertEquals("return book", matches.get(1).getDescription());
    }

    @Test
    public void find_noMatch_returnsEmptyTaskList() {
        TaskList tasks = TaskList.of(new Todo("read book"));

        TaskList matches = tasks.find("laptop");

        assertEquals(0, matches.size());
    }
}
