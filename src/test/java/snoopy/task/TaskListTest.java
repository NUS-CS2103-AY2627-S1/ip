package snoopy.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Collections;

import org.junit.jupiter.api.Test;

/**
 * Tests ownership and operations of the task collection.
 */
public class TaskListTest {
    @Test
    public void constructor_sourceChanges_doNotChangeTaskList() {
        ArrayList<Task> source = new ArrayList<>();
        source.add(new Todo("original"));
        TaskList tasks = new TaskList(source);

        source.clear();

        assertEquals(1, tasks.size());
        assertEquals("[T][ ] original", tasks.get(0).toString());
    }

    @Test
    public void addAndRemove_tasks_preserveOrderAndReturnRemovedTask() {
        Task first = new Todo("first");
        Task second = new Todo("second");
        TaskList tasks = new TaskList();

        tasks.add(first);
        tasks.add(second);
        Task removed = tasks.remove(0);
        tasks.add(0, first);

        assertSame(first, removed);
        assertSame(first, tasks.get(0));
        assertSame(second, tasks.get(1));
    }

    @Test
    public void find_matches_returnsIndependentListInOriginalOrder() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Todo("call Alice"));
        tasks.add(new Todo("return book"));

        TaskList matches = tasks.find("book");
        matches.remove(0);

        assertEquals(3, tasks.size());
        assertEquals(1, matches.size());
        assertEquals("[T][ ] return book", matches.get(0).toString());
        assertEquals(3, tasks.stream().count());
    }

    @Test
    public void constructorAndAdd_nullTasks_failAssertions() {
        assertThrows(AssertionError.class, () -> new TaskList(null));
        assertThrows(AssertionError.class, () ->
                new TaskList(Collections.singletonList(null)));
        assertThrows(AssertionError.class, () -> new TaskList().add(null));
        assertThrows(AssertionError.class, () -> new TaskList().add(0, null));
    }
}
