package doge.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests the public behavior of {@link TaskList}.
 */
class TaskListTest {

    /** Verifies that a new task list starts empty. */
    @Test
    void constructor_newTaskList_hasNoTasks() {
        TaskList tasks = new TaskList();

        assertEquals(0, tasks.size());
    }

    /** Verifies that adding a task increases the list size. */
    @Test
    void add_taskProvided_increasesListSize() {
        TaskList tasks = new TaskList();

        tasks.add(new Todo("Read about JUnit"));

        assertEquals(1, tasks.size());
    }

    /** Verifies that tasks are retrieved using one-based indexes. */
    @Test
    void get_taskIndexProvided_returnsTaskAtOneBasedIndex() {
        TaskList tasks = new TaskList();
        Task firstTask = new Todo("First task");
        Task secondTask = new Todo("Second task");
        tasks.add(firstTask);
        tasks.add(secondTask);

        assertSame(firstTask, tasks.get(1));
        assertSame(secondTask, tasks.get(2));
    }

    /** Verifies that deleting a task removes and returns the selected task. */
    @Test
    void delete_taskIndexProvided_removesAndReturnsTask() {
        TaskList tasks = new TaskList();
        Task firstTask = new Todo("First task");
        Task secondTask = new Todo("Second task");
        tasks.add(firstTask);
        tasks.add(secondTask);

        Task deletedTask = tasks.delete(1);

        assertSame(firstTask, deletedTask);
        assertEquals(1, tasks.size());
        assertSame(secondTask, tasks.get(1));
    }

    /** Verifies that tasks are returned in insertion order. */
    @Test
    void getTasks_taskListHasTasks_returnsTasksInInsertionOrder() {
        TaskList tasks = new TaskList();
        Task firstTask = new Todo("First task");
        Task secondTask = new Todo("Second task");
        tasks.add(firstTask);
        tasks.add(secondTask);

        assertEquals(2, tasks.getTasks().size());
        assertSame(firstTask, tasks.getTasks().get(0));
        assertSame(secondTask, tasks.getTasks().get(1));
    }

    /** Verifies that the selected task can be marked as done. */
    @Test
    void markDone_taskIndexProvided_marksTaskAsDone() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("Read about JUnit"));

        tasks.markDone(1);

        assertTrue(tasks.get(1).isDone());
    }

    /** Verifies that the selected task can be marked as not done. */
    @Test
    void unmarkDone_taskIndexProvided_marksTaskAsNotDone() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("Read about JUnit"));
        tasks.markDone(1);

        tasks.unmarkDone(1);

        assertFalse(tasks.get(1).isDone());
    }

    @Test
    void find_keywordProvided_returnsCaseInsensitiveDescriptionMatches() {
        TaskList tasks = new TaskList();
        Task matchingTodo = new Todo("Read a book");
        Task nonMatchingTask = new Todo("Buy groceries");
        Task matchingDeadline = new Todo("Return book");
        tasks.add(matchingTodo);
        tasks.add(nonMatchingTask);
        tasks.add(matchingDeadline);

        List<Task> matchingTasks = tasks.find("BOOK");

        assertEquals(List.of(matchingTodo, matchingDeadline), matchingTasks);
    }

    @Test
    void find_keywordHasNoMatches_returnsEmptyList() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("Read a book"));

        assertTrue(tasks.find("concert").isEmpty());
    }

    /** Verifies the display of an empty task list. */
    @Test
    void toString_emptyTaskList_returnsHeaderOnly() {
        TaskList tasks = new TaskList();

        assertEquals("Here are the tasks in your list:", tasks.toString());
    }

    /** Verifies the numbered display of a non-empty task list. */
    @Test
    void toString_taskListHasTasks_returnsNumberedTaskList() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("First task"));
        tasks.add(new Todo("Second task"));

        assertEquals("Here are the tasks in your list:\n"
                + "1.[T][ ] First task\n"
                + "2.[T][ ] Second task", tasks.toString());
    }

    /** Verifies that invalid one-based indexes are rejected explicitly. */
    @Test
    void get_invalidIndex_throwsIndexOutOfBoundsException() {
        TaskList tasks = new TaskList();

        assertThrows(IndexOutOfBoundsException.class, () -> tasks.get(0));
        assertThrows(IndexOutOfBoundsException.class, () -> tasks.get(1));
    }

    /** Verifies that a null task and null search keyword are rejected explicitly. */
    @Test
    void addOrFind_nullArgument_throwsIllegalArgumentException() {
        TaskList tasks = new TaskList();

        assertThrows(IllegalArgumentException.class, () -> tasks.add(null));
        assertThrows(IllegalArgumentException.class, () -> tasks.find(null));
    }
}
