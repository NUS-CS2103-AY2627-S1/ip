package maggigorengayam.tasklist;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import maggigorengayam.task.Task;

public class TaskListTest {

    @Test
    public void construct_default_createsEmptyList() {
        TaskList taskList = new TaskList();

        assertEquals(0, taskList.size());
        assertTrue(taskList.getAll().isEmpty());
    }

    /**
     * TaskList(List) stores the given list directly rather than copying it,
     * so getAll() returns that same list instance - an external mutation to
     * the original list is visible through the TaskList, not isolated from
     * it.
     */
    @Test
    public void construct_withExistingList_wrapsGivenListInstanceWithoutCopying() {
        List<Task> existing = new ArrayList<>();
        existing.add(new Task("preloaded"));

        TaskList taskList = new TaskList(existing);

        assertSame(existing, taskList.getAll());
        assertEquals(1, taskList.size());

        existing.add(new Task("added after construction"));

        assertEquals(2, taskList.size());
    }

    @Test
    public void add_singleTask_sizeIncrementsAndTaskAppearsAtEnd() {
        TaskList taskList = new TaskList();
        Task task = new Task("read book");

        taskList.add(task);

        assertEquals(1, taskList.size());
        assertSame(task, taskList.get(0));
    }

    @Test
    public void add_multipleTasks_appendedInInsertionOrder() {
        TaskList taskList = new TaskList();
        Task first = new Task("first");
        Task second = new Task("second");

        taskList.add(first);
        taskList.add(second);

        assertEquals(2, taskList.size());
        assertSame(first, taskList.get(0));
        assertSame(second, taskList.get(1));
    }

    @Test
    public void get_validIndex_returnsCorrectTask() {
        TaskList taskList = new TaskList();
        taskList.add(new Task("first"));
        Task second = new Task("second");
        taskList.add(second);

        assertSame(second, taskList.get(1));
    }

    @Test
    public void get_negativeIndex_exceptionThrown() {
        TaskList taskList = new TaskList();
        taskList.add(new Task("task"));

        assertThrows(IndexOutOfBoundsException.class, () -> taskList.get(-1));
    }

    @Test
    public void get_indexEqualToSize_exceptionThrown() {
        TaskList taskList = new TaskList();
        taskList.add(new Task("task"));

        assertThrows(IndexOutOfBoundsException.class, () -> taskList.get(1));
    }

    @Test
    public void get_fromEmptyList_exceptionThrown() {
        TaskList taskList = new TaskList();

        assertThrows(IndexOutOfBoundsException.class, () -> taskList.get(0));
    }

    @Test
    public void size_newEmptyList_returnsZero() {
        TaskList taskList = new TaskList();

        assertEquals(0, taskList.size());
    }

    @Test
    public void size_afterAddsAndRemoves_reflectsCurrentCount() {
        TaskList taskList = new TaskList();
        taskList.add(new Task("a"));
        taskList.add(new Task("b"));
        taskList.add(new Task("c"));

        taskList.remove(1);

        assertEquals(2, taskList.size());
    }

    @Test
    public void getAll_emptyList_returnsEmptyList() {
        TaskList taskList = new TaskList();

        assertTrue(taskList.getAll().isEmpty());
    }

    @Test
    public void getAll_returnsTasksInInsertionOrder() {
        TaskList taskList = new TaskList();
        Task first = new Task("first");
        Task second = new Task("second");
        taskList.add(first);
        taskList.add(second);

        assertEquals(List.of(first, second), taskList.getAll());
    }

    @Test
    public void remove_middleIndex_returnsRemovedTaskAndShiftsLaterTasksDown() {
        TaskList taskList = new TaskList();
        Task first = new Task("first");
        Task second = new Task("second");
        Task third = new Task("third");
        taskList.add(first);
        taskList.add(second);
        taskList.add(third);

        Task removed = taskList.remove(1);

        assertSame(second, removed);
        assertEquals(2, taskList.size());
        assertSame(first, taskList.get(0));
        assertSame(third, taskList.get(1));
    }

    @Test
    public void remove_firstIndex_returnsFirstTaskAndRemainingTasksShiftDown() {
        TaskList taskList = new TaskList();
        Task first = new Task("first");
        Task second = new Task("second");
        taskList.add(first);
        taskList.add(second);

        Task removed = taskList.remove(0);

        assertSame(first, removed);
        assertEquals(1, taskList.size());
        assertSame(second, taskList.get(0));
    }

    @Test
    public void remove_lastIndex_returnsLastTaskAndSizeDecrements() {
        TaskList taskList = new TaskList();
        Task first = new Task("first");
        Task second = new Task("second");
        taskList.add(first);
        taskList.add(second);

        Task removed = taskList.remove(1);

        assertSame(second, removed);
        assertEquals(1, taskList.size());
        assertSame(first, taskList.get(0));
    }

    @Test
    public void remove_onlyTaskInList_listBecomesEmpty() {
        TaskList taskList = new TaskList();
        taskList.add(new Task("only task"));

        Task removed = taskList.remove(0);

        assertEquals("only task", removed.getDescription());
        assertEquals(0, taskList.size());
        assertTrue(taskList.getAll().isEmpty());
    }

    @Test
    public void remove_negativeIndex_exceptionThrown() {
        TaskList taskList = new TaskList();
        taskList.add(new Task("task"));

        assertThrows(IndexOutOfBoundsException.class, () -> taskList.remove(-1));
    }

    @Test
    public void remove_indexEqualToSize_exceptionThrown() {
        TaskList taskList = new TaskList();
        taskList.add(new Task("task"));

        assertThrows(IndexOutOfBoundsException.class, () -> taskList.remove(1));
    }

    @Test
    public void remove_fromEmptyList_exceptionThrown() {
        TaskList taskList = new TaskList();

        assertThrows(IndexOutOfBoundsException.class, () -> taskList.remove(0));
    }
}
