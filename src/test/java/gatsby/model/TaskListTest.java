package gatsby.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

/** Tests ordering, mutation, and read-only access in {@link TaskList}. */
class TaskListTest {

    /** Verifies that the no-argument constructor creates an empty list. */
    @Test
    void constructor_withoutInitialTasks_createsEmptyList() {
        TaskList tasks = new TaskList();

        assertTrue(tasks.isEmpty());
        assertEquals(0, tasks.size());
        assertEquals(List.of(), tasks.asList());
    }

    /** Verifies that adding tasks preserves order and makes them retrievable. */
    @Test
    void add_tasks_preservesInsertionOrder() {
        Task first = new Todo("first");
        Task second = new Todo("second");
        TaskList tasks = new TaskList();

        tasks.add(first);
        tasks.add(second);

        assertFalse(tasks.isEmpty());
        assertEquals(2, tasks.size());
        assertSame(first, tasks.get(0));
        assertSame(second, tasks.get(1));
    }

    /** Verifies that removing a task returns it and updates the remaining order. */
    @Test
    void remove_task_returnsRemovedTaskAndUpdatesList() {
        Task first = new Todo("first");
        Task second = new Todo("second");
        TaskList tasks = new TaskList(List.of(first, second));

        Task removed = tasks.remove(0);

        assertSame(first, removed);
        assertEquals(1, tasks.size());
        assertSame(second, tasks.get(0));
    }

    /** Verifies that recovered tasks are copied instead of sharing the input list. */
    @Test
    void constructor_withInitialTasks_copiesInputList() {
        List<Task> initialTasks = new ArrayList<>();
        initialTasks.add(new Todo("saved task"));

        TaskList tasks = new TaskList(initialTasks);
        initialTasks.clear();

        assertEquals(1, tasks.size());
        assertEquals("saved task", tasks.get(0).getTaskName());
    }

    /** Verifies that callers cannot mutate the list through the persistence view. */
    @Test
    void asList_returnedViewIsReadOnly() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read-only task"));

        List<Task> view = tasks.asList();

        assertThrows(UnsupportedOperationException.class, () ->
                view.add(new Todo("should not be added")));
        assertEquals(1, tasks.size());
    }
}
