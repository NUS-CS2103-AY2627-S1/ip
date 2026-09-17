package notmarth.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Iterator;
import java.util.List;

import notmarth.exception.NotMarthException;
import org.junit.jupiter.api.Test;

/** Tests task-list capacity, ordering, completion, and deletion behavior. */
class TaskListTest {
    /** Verifies that task operations preserve state and display order. */
    @Test
    void taskOperations_validTasks_maintainStateAndOrder() throws NotMarthException {
        Task first = new ToDo("first");
        Task second = new ToDo("second");
        TaskList tasks = new TaskList(List.of(), 2);

        tasks.add(first);
        tasks.add(second);
        assertEquals(2, tasks.size());
        assertEquals(first, tasks.get(0));
        assertEquals(second, tasks.get(1));

        assertEquals(first, tasks.mark(1));
        assertTrue(first.isDone());
        assertEquals(first, tasks.unmark(1));
        assertFalse(first.isDone());
        assertEquals(first, tasks.delete(1));
        assertEquals(1, tasks.size());
        assertEquals(second, tasks.get(0));
    }

    /** Verifies that the configured task-list capacity is enforced. */
    @Test
    void add_taskBeyondConfiguredCapacity_throwsNotMarthException() throws NotMarthException {
        TaskList tasks = new TaskList(List.of(), 1);
        tasks.add(new ToDo("only task"));

        NotMarthException exception = assertThrows(NotMarthException.class,
                () -> tasks.add(new ToDo("extra task")));

        assertEquals(
                "Your task list is full. Remove a task before adding another one.", exception.getMessage());
        assertEquals(1, tasks.size());
    }

    /** Verifies that duplicate task details are rejected without changing the list. */
    @Test
    void add_duplicateTaskDetails_rejectsSecondTask() throws NotMarthException {
        TaskList tasks = new TaskList(List.of(), 2);
        tasks.add(new ToDo("same task"));

        NotMarthException exception = assertThrows(NotMarthException.class,
                () -> tasks.add(new ToDo("same task")));

        assertEquals("That task is already in your battle plan.", exception.getMessage());
        assertEquals(1, tasks.size());
    }

    /** Verifies that empty and out-of-range task numbers are rejected. */
    @Test
    void taskOperations_emptyOrOutOfRangeNumber_throwNotMarthException() {
        TaskList empty = new TaskList(List.of(), 2);
        assertThrows(NotMarthException.class, () -> empty.mark(1));
        assertThrows(NotMarthException.class, () -> empty.unmark(1));
        assertThrows(NotMarthException.class, () -> empty.delete(1));

        TaskList tasks = new TaskList(List.of(new ToDo("task")), 2);
        assertThrows(NotMarthException.class, () -> tasks.mark(0));
        assertThrows(NotMarthException.class, () -> tasks.unmark(2));
        assertThrows(NotMarthException.class, () -> tasks.delete(-1));
    }

    /** Verifies that construction and the read-only list view protect invariants. */
    @Test
    void taskListApi_invalidConstructionOrMutation_protectsInvariants() throws NotMarthException {
        assertThrows(IllegalArgumentException.class, () -> new TaskList(List.of(), 0));
        assertThrows(IllegalArgumentException.class,
                () -> new TaskList(List.of(new ToDo("one")), 0));

        TaskList tasks = new TaskList(List.of(new ToDo("one")), 2);
        assertFalse(tasks.isEmpty());
        assertThrows(UnsupportedOperationException.class,
                () -> tasks.asList().add(new ToDo("not allowed")));
        tasks.delete(1);
        assertTrue(tasks.isEmpty());
    }

    /** Verifies that construction rejects null and duplicate initial tasks. */
    @Test
    void taskListConstruction_nullOrDuplicateTasks_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new TaskList(null, 2));
        assertThrows(IllegalArgumentException.class,
                () -> new TaskList(List.of(new ToDo("same"), new ToDo("same")), 2));
    }

    /** Verifies that the varargs constructor accepts an initial task sequence. */
    @Test
    void taskListConstruction_varargsInput_acceptsInitialTasks() {
        Task first = new ToDo("first");
        Task second = new ToDo("second");

        TaskList tasks = new TaskList(2, first, second);

        assertEquals(2, tasks.size());
        assertSame(first, tasks.get(0));
        assertSame(second, tasks.get(1));
    }

    /** Verifies that iteration follows the task list's display order. */
    @Test
    void iterator_tasksAdded_returnsTasksInDisplayOrder() throws NotMarthException {
        Task first = new ToDo("first");
        Task second = new ToDo("second");
        TaskList tasks = new TaskList(List.of(), 2);
        tasks.add(first);
        tasks.add(second);

        Iterator<Task> iterator = tasks.iterator();

        assertTrue(iterator.hasNext());
        assertSame(first, iterator.next());
        assertTrue(iterator.hasNext());
        assertSame(second, iterator.next());
        assertFalse(iterator.hasNext());
    }
}
