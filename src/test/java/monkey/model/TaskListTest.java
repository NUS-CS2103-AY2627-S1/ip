package monkey.model;

import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TaskListTest {
    @Test
    void constructorAndAdd_validTasks_tracksSizeAndEmptiness() {
        TaskList tasks = new TaskList();

        assertTrue(tasks.isEmpty());
        tasks.add(new ToDos("task"));

        assertFalse(tasks.isEmpty());
        assertEquals(1, tasks.size());
    }

    @Test
    void markAndUnmark_validIndex_updatesTaskStatus() {
        Task task = new ToDos("revise JUnit");
        TaskList tasks = new TaskList(List.of(task));

        tasks.mark(0);
        assertTrue(task.isDone());

        tasks.unmark(0);
        assertFalse(task.isDone());
    }

    @Test
    void delete_validIndex_removesAndReturnsSelectedTask() {
        Task first = new ToDos("first task");
        Task second = new ToDos("second task");
        TaskList tasks = new TaskList(List.of(first, second));

        Task deleted = tasks.delete(0);

        assertSame(first, deleted);
        assertEquals(1, tasks.size());
        assertSame(second, tasks.get(0));
    }

    @Test
    void asList_returnsSnapshotThatDoesNotChangeTaskList() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDos("original task"));

        List<Task> snapshot = tasks.asList();
        snapshot.clear();

        assertEquals(1, tasks.size());
        assertEquals("original task", tasks.get(0).getDescription());
    }

    @Test
    void get_invalidIndex_reportsBrokenIndexAssumption() {
        TaskList tasks = new TaskList(List.of(new ToDos("task")));

        assertThrows(AssertionError.class, () -> tasks.get(1));
        assertThrows(AssertionError.class, () -> tasks.get(-1));
    }

    @Test
    void remove_validIndex_removesAndReturnsTask() {
        Task task = new ToDos("task");
        TaskList tasks = new TaskList(List.of(task));

        assertSame(task, tasks.remove(0));
        assertTrue(tasks.isEmpty());
    }

    @Test
    void containsEquivalent_matchingAndDifferentTasks_returnsExpectedResult() {
        TaskList tasks = new TaskList(List.of(new ToDos("Buy Milk")));

        assertTrue(tasks.containsEquivalent(new ToDos("buy   milk")));
        assertFalse(tasks.containsEquivalent(new ToDos("buy bread")));
    }

    @Test
    void constructorOrAdd_nullTask_reportsBrokenCallerAssumption() {
        assertThrows(AssertionError.class, () -> new TaskList(null));

        TaskList tasks = new TaskList();
        assertThrows(AssertionError.class, () -> tasks.add(null));
    }

    @Test
    void mutatingOperations_invalidIndex_reportBrokenCallerAssumption() {
        TaskList tasks = new TaskList(List.of(new ToDos("task")));

        assertThrows(AssertionError.class, () -> tasks.remove(1));
        assertThrows(AssertionError.class, () -> tasks.delete(1));
        assertThrows(AssertionError.class, () -> tasks.mark(1));
        assertThrows(AssertionError.class, () -> tasks.unmark(1));
    }
}
