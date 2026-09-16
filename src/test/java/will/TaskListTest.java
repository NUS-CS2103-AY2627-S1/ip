package will;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Iterator;

import org.junit.jupiter.api.Test;

import will.task.Task;
import will.task.TaskType;
import will.task.Todo;

public class TaskListTest {

    @Test
    public void size_newTaskList_isZero() {
        TaskList tasks = new TaskList();
        assertEquals(0, tasks.size());
    }

    @Test
    public void add_oneTask_increasesSizeByOne() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        assertEquals(1, tasks.size());
    }

    @Test
    public void get_afterAdd_returnsTheSameTask() {
        TaskList tasks = new TaskList();
        Task todo = new Todo("read book");
        tasks.add(todo);
        assertEquals(todo, tasks.get(0));
    }

    @Test
    public void remove_existingIndex_returnsRemovedTaskAndShrinksList() {
        TaskList tasks = new TaskList();
        Task first = new Todo("read book");
        Task second = new Todo("buy milk");
        tasks.add(first);
        tasks.add(second);

        Task removed = tasks.remove(0);

        assertEquals(first, removed);
        assertEquals(1, tasks.size());
        assertEquals(second, tasks.get(0));
    }

    @Test
    public void getTasks_afterAdd_containsAddedTask() {
        TaskList tasks = new TaskList();
        Task todo = new Todo("read book");
        tasks.add(todo);
        assertTrue(tasks.getTasks().contains(todo));
    }

    @Test
    public void iterator_multipleTasks_visitsEachInInsertionOrder() {
        TaskList tasks = new TaskList();
        Task first = new Todo("read book");
        Task second = new Todo("buy milk");
        tasks.add(first);
        tasks.add(second);

        Iterator<Task> iterator = tasks.iterator();

        assertTrue(iterator.hasNext());
        assertEquals(first, iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals(second, iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test
    public void constructor_wrappingExistingList_startsPopulated() {
        java.util.ArrayList<Task> existing = new java.util.ArrayList<>();
        existing.add(new Todo("read book"));
        TaskList tasks = new TaskList(existing);
        assertEquals(1, tasks.size());
        assertEquals(TaskType.TODO, tasks.get(0).getType());
    }
}
