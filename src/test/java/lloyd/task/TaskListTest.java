package lloyd.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests the ordered task mutations performed by {@link TaskList}.
 */
public class TaskListTest {

    /** Verifies that a task list rejects a null initial collection. */
    @Test
    public void constructor_nullTasks_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new TaskList(null));
    }

    /** Verifies that construction copies tasks while preserving their order. */
    @Test
    public void constructor_existingTasks_copiesTasksInOriginalOrder() {
        Task first = new Todo("first");
        Task second = new Todo("second");
        ArrayList<Task> source = new ArrayList<>(List.of(first, second));

        TaskList taskList = new TaskList(source);
        source.clear();

        assertEquals(2, taskList.size());
        assertSame(first, taskList.get(0));
        assertSame(second, taskList.get(1));
    }

    /** Verifies that appending tasks updates both the size and last task. */
    @Test
    public void add_tasks_appendsTasksAndUpdatesLastTask() {
        TaskList taskList = new TaskList();
        Task first = new Todo("first");
        Task second = new Todo("second");

        taskList.add(first);
        taskList.add(second);

        assertEquals(2, taskList.size());
        assertSame(first, taskList.get(0));
        assertSame(second, taskList.getLast());
    }

    /** Verifies that indexed insertion preserves the surrounding task order. */
    @Test
    public void add_atIndex_insertsTaskAndPreservesOrder() {
        Task first = new Todo("first");
        Task second = new Todo("second");
        Task inserted = new Todo("inserted");
        TaskList taskList = new TaskList(List.of(first, second));

        taskList.add(1, inserted);

        assertEquals(3, taskList.size());
        assertSame(first, taskList.get(0));
        assertSame(inserted, taskList.get(1));
        assertSame(second, taskList.get(2));
    }

    /** Verifies that indexed removal returns the task and closes the list gap. */
    @Test
    public void remove_existingTask_returnsTaskAndClosesGap() {
        Task first = new Todo("first");
        Task second = new Todo("second");
        Task third = new Todo("third");
        TaskList taskList = new TaskList(List.of(first, second, third));

        Task removed = taskList.remove(1);

        assertSame(second, removed);
        assertEquals(2, taskList.size());
        assertSame(first, taskList.get(0));
        assertSame(third, taskList.get(1));
    }

    /** Verifies that removing the final task exposes the preceding task as last. */
    @Test
    public void removeLast_nonEmptyList_removesLastTask() {
        Task first = new Todo("first");
        TaskList taskList = new TaskList(List.of(first, new Todo("second")));

        taskList.removeLast();

        assertEquals(1, taskList.size());
        assertSame(first, taskList.getLast());
    }

    /** Verifies that callers cannot modify the list returned for read-only use. */
    @Test
    public void find_matchingKeyword_returnsAllMatchesInOriginalOrder() {
        Task first = new Todo("read book");
        Task second = new Todo("return book");
        TaskList taskList = new TaskList(List.of(first, new Todo("write essay"), second));

        List<Task> matches = taskList.find("book");

        assertEquals(List.of(first, second), matches);
    }

    @Test
    public void find_keywordWithDifferentCase_returnsNoMatches() {
        TaskList taskList = new TaskList(List.of(new Todo("read book")));

        assertEquals(List.of(), taskList.find("Book"));
    }

    @Test
    public void find_blankKeyword_throwsIllegalArgumentException() {
        TaskList taskList = new TaskList();

        assertThrows(IllegalArgumentException.class, () -> taskList.find(" "));
    }

    @Test
    public void asList_returnedView_cannotBeModified() {
        Task task = new Todo("task");
        TaskList taskList = new TaskList(List.of(task));
        List<Task> readOnlyTasks = taskList.asList();

        assertEquals(List.of(task), readOnlyTasks);
        assertThrows(UnsupportedOperationException.class, () ->
                readOnlyTasks.add(new Todo("another task")));
    }
}
