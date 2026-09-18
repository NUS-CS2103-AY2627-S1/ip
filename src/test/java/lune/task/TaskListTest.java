package lune.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

/**
 * Tests every operation TaskList exposes: add, remove, get, size,
 * iterator, clear, and stream.
 */
public class TaskListTest {

    @Test
    public void constructor_noArgs_emptyList() {
        TaskList tasks = new TaskList();
        assertEquals(0, tasks.size());
    }

    @Test
    public void constructor_wrappingExistingList_containsThoseTasks() {
        ArrayList<Task> initial = new ArrayList<>();
        initial.add(new Todo("read book"));
        TaskList tasks = new TaskList(initial);
        assertEquals(1, tasks.size());
        assertEquals("read book", tasks.get(0).getDescription());
    }

    @Test
    public void add_singleTask_sizeIncreasesAndTaskRetrievable() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        assertEquals(1, tasks.size());
        assertEquals("read book", tasks.get(0).getDescription());
    }

    @Test
    public void add_multipleTasks_appendedInOrder() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("first"));
        tasks.add(new Todo("second"));
        assertEquals("first", tasks.get(0).getDescription());
        assertEquals("second", tasks.get(1).getDescription());
    }

    @Test
    public void remove_validIndex_taskRemovedAndReturned() {
        TaskList tasks = new TaskList();
        Task task = new Todo("read book");
        tasks.add(task);
        Task removed = tasks.remove(0);
        assertSame(task, removed);
        assertEquals(0, tasks.size());
    }

    @Test
    public void remove_middleOfThree_remainingTasksShiftDown() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("first"));
        tasks.add(new Todo("second"));
        tasks.add(new Todo("third"));
        tasks.remove(1);
        assertEquals(2, tasks.size());
        assertEquals("first", tasks.get(0).getDescription());
        assertEquals("third", tasks.get(1).getDescription());
    }

    @Test
    public void get_validIndex_correctTaskReturned() {
        TaskList tasks = new TaskList();
        Task task = new Todo("read book");
        tasks.add(task);
        assertSame(task, tasks.get(0));
    }

    @Test
    public void size_emptyList_zero() {
        assertEquals(0, new TaskList().size());
    }

    @Test
    public void size_afterAddsAndRemoves_reflectsCurrentCount() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("a"));
        tasks.add(new Todo("b"));
        tasks.remove(0);
        assertEquals(1, tasks.size());
    }

    @Test
    public void iterator_multipleTasks_iteratesInInsertionOrder() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("first"));
        tasks.add(new Todo("second"));
        Iterator<Task> it = tasks.iterator();
        assertTrue(it.hasNext());
        assertEquals("first", it.next().getDescription());
        assertTrue(it.hasNext());
        assertEquals("second", it.next().getDescription());
        assertFalse(it.hasNext());
    }

    @Test
    public void clear_nonEmptyList_becomesEmpty() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("first"));
        tasks.add(new Todo("second"));
        tasks.clear();
        assertEquals(0, tasks.size());
    }

    @Test
    public void clear_emptyList_staysEmpty() {
        TaskList tasks = new TaskList();
        tasks.clear();
        assertEquals(0, tasks.size());
    }

    @Test
    public void stream_multipleTasks_reflectsInsertionOrder() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("first"));
        tasks.add(new Todo("second"));
        List<String> descriptions = tasks.stream().map(Task::getDescription).collect(Collectors.toList());
        assertEquals(List.of("first", "second"), descriptions);
    }

    @Test
    public void stream_emptyList_emptyStream() {
        assertEquals(0, new TaskList().stream().count());
    }
}
