package duncan.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

public class TaskListTest {
    private static TaskList taskListOfSize(int size) {
        TaskList tasks = new TaskList();
        for (int i = 0; i < size; i++) {
            tasks.add(new Todo("task " + i));
        }
        return tasks;
    }

    // ---- TaskList() / TaskList(ArrayList<Task>) ----

    @Test
    public void constructorNoArgs_newTaskList_isEmpty() {
        TaskList tasks = new TaskList();

        assertEquals(0, tasks.size());
    }

    @Test
    public void constructorWithList_existingList_wrapsGivenListDirectly() {
        ArrayList<Task> existing = new ArrayList<>();
        existing.add(new Todo("read book"));

        TaskList tasks = new TaskList(existing);

        assertEquals(1, tasks.size());
        assertSame(existing, tasks.getTasks());
    }

    // ---- add ----

    @Test
    public void add_toEmptyList_sizeBecomesOne() {
        TaskList tasks = new TaskList();

        tasks.add(new Todo("read book"));

        assertEquals(1, tasks.size());
    }

    @Test
    public void add_multipleTasks_keepsInsertionOrder() {
        TaskList tasks = new TaskList();
        Task first = new Todo("first");
        Task second = new Todo("second");

        tasks.add(first);
        tasks.add(second);

        assertSame(first, tasks.get(0));
        assertSame(second, tasks.get(1));
    }

    // ---- get ----

    @Test
    public void get_validIndex_correctTaskReturned() {
        TaskList tasks = new TaskList();
        Task task = new Todo("read book");
        tasks.add(task);

        assertSame(task, tasks.get(0));
    }

    @Test
    public void get_indexOutOfRange_indexOutOfBoundsExceptionThrown() {
        TaskList tasks = taskListOfSize(2);

        assertThrows(IndexOutOfBoundsException.class, () -> tasks.get(5));
    }

    // ---- remove ----

    @Test
    public void remove_validIndex_taskReturnedAndSizeDecreases() {
        TaskList tasks = new TaskList();
        Task task = new Todo("read book");
        tasks.add(task);

        Task removed = tasks.remove(0);

        assertSame(task, removed);
        assertEquals(0, tasks.size());
    }

    @Test
    public void remove_middleIndex_remainingTasksShiftDown() {
        TaskList tasks = new TaskList();
        Task first = new Todo("first");
        Task second = new Todo("second");
        Task third = new Todo("third");
        tasks.add(first);
        tasks.add(second);
        tasks.add(third);

        tasks.remove(1);

        assertEquals(2, tasks.size());
        assertSame(first, tasks.get(0));
        assertSame(third, tasks.get(1));
    }

    @Test
    public void remove_indexOutOfRange_indexOutOfBoundsExceptionThrown() {
        TaskList tasks = taskListOfSize(2);

        assertThrows(IndexOutOfBoundsException.class, () -> tasks.remove(5));
    }

    // ---- size ----

    @Test
    public void size_afterAddsAndRemoves_reflectsCurrentCount() {
        TaskList tasks = new TaskList();
        assertEquals(0, tasks.size());

        tasks.add(new Todo("first"));
        tasks.add(new Todo("second"));
        assertEquals(2, tasks.size());

        tasks.remove(0);
        assertEquals(1, tasks.size());
    }

    // ---- isValidIndex ----

    @Test
    public void isValidIndex_emptyList_falseReturned() {
        TaskList tasks = taskListOfSize(0);

        assertFalse(tasks.isValidIndex(0));
    }

    @Test
    public void isValidIndex_negativeIndex_falseReturned() {
        TaskList tasks = taskListOfSize(3);

        assertFalse(tasks.isValidIndex(-1));
    }

    @Test
    public void isValidIndex_firstIndexInNonEmptyList_trueReturned() {
        TaskList tasks = taskListOfSize(3);

        assertTrue(tasks.isValidIndex(0));
    }

    @Test
    public void isValidIndex_lastIndexInNonEmptyList_trueReturned() {
        TaskList tasks = taskListOfSize(3);

        assertTrue(tasks.isValidIndex(2));
    }

    @Test
    public void isValidIndex_indexEqualToSize_falseReturned() {
        TaskList tasks = taskListOfSize(3);

        assertFalse(tasks.isValidIndex(3));
    }

    @Test
    public void isValidIndex_indexGreaterThanSize_falseReturned() {
        TaskList tasks = taskListOfSize(3);

        assertFalse(tasks.isValidIndex(100));
    }

    // ---- find ----

    @Test
    public void find_keywordInDifferentCase_matchingTaskReturned() {
        TaskList tasks = new TaskList();
        Task task = new Todo("Read Book");
        tasks.add(task);

        Map<Integer, Task> matches = tasks.find("rEAD");

        assertEquals(1, matches.size());
        assertSame(task, matches.get(0));
    }

    @Test
    public void find_keywordMatchingNoTask_emptyListReturned() {
        TaskList tasks = taskListOfSize(3);

        Map<Integer, Task> matches = tasks.find("book");

        assertTrue(matches.isEmpty());
    }

    @Test
    public void find_keywordMatchingSomeTasks_matchingTasksReturnedWithOriginalIndexInOrder() {
        TaskList tasks = new TaskList();
        Task firstBook = new Todo("borrow book");
        Task nonMatch = new Todo("buy milk");
        Task secondBook = new Todo("return book");
        tasks.add(firstBook);
        tasks.add(nonMatch);
        tasks.add(secondBook);

        Map<Integer, Task> matches = tasks.find("book");

        assertEquals(List.of(0, 2), List.copyOf(matches.keySet()));
        assertSame(firstBook, matches.get(0));
        assertSame(secondBook, matches.get(2));
    }

    // ---- getTasks ----

    @Test
    public void getTasks_afterAdds_containsAddedTasksInOrder() {
        TaskList tasks = new TaskList();
        Task first = new Todo("first");
        Task second = new Todo("second");
        tasks.add(first);
        tasks.add(second);

        ArrayList<Task> underlying = tasks.getTasks();

        assertEquals(2, underlying.size());
        assertSame(first, underlying.get(0));
        assertSame(second, underlying.get(1));
    }

    @Test
    public void getTasks_calledTwice_returnsSameListInstance() {
        TaskList tasks = new TaskList();

        assertSame(tasks.getTasks(), tasks.getTasks());
    }

    // ---- hasDuplicateOf ----

    @Test
    public void hasDuplicateOf_sameTaskInList_true() {
        TaskList tasks = taskListOfSize(3);

        assertTrue(tasks.hasDuplicateOf(new Todo("TASK 1")));
    }

    @Test
    public void hasDuplicateOf_noSameTaskInList_false() {
        TaskList tasks = taskListOfSize(3);

        assertFalse(tasks.hasDuplicateOf(new Todo("task 3")));
    }
}
