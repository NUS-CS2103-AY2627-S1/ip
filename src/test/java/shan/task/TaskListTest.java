package shan.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

class TaskListTest {
    @Test
    void containsTaskNumber_emptyList_returnsFalse() {
        TaskList tasks = new TaskList();

        boolean result = tasks.containsTaskNumber(1);

        assertFalse(result);
    }

    @Test
    void add_newTask_appendsTask() {
        TaskList tasks = new TaskList();
        Task task = new ToDo("read book");

        tasks.add(task);

        assertEquals(1, tasks.size());
        assertSame(task, tasks.get(1));
    }

    @Test
    void containsTaskNumber_boundaryNumbers_returnsExpectedValues() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("first task"));
        tasks.add(new ToDo("second task"));

        assertFalse(tasks.containsTaskNumber(0));
        assertTrue(tasks.containsTaskNumber(1));
        assertTrue(tasks.containsTaskNumber(2));
        assertFalse(tasks.containsTaskNumber(3));
    }

    @Test
    void delete_existingTask_removesTaskAndRenumbersRemainingTasks() {
        TaskList tasks = new TaskList();
        Task firstTask = new ToDo("first task");
        Task secondTask = new ToDo("second task");
        tasks.add(firstTask);
        tasks.add(secondTask);

        Task deletedTask = tasks.delete(1);

        assertSame(firstTask, deletedTask);
        assertEquals(1, tasks.size());
        assertSame(secondTask, tasks.get(1));
    }

    @Test
    void restore_deletedTask_returnsTaskToOriginalPosition() {
        TaskList tasks = new TaskList();
        Task firstTask = new ToDo("first task");
        Task secondTask = new ToDo("second task");
        tasks.add(firstTask);
        tasks.add(secondTask);
        Task deletedTask = tasks.delete(1);

        tasks.restore(1, deletedTask);

        assertEquals(2, tasks.size());
        assertSame(firstTask, tasks.get(1));
        assertSame(secondTask, tasks.get(2));
    }

    @Test
    void removeLast_nonEmptyList_removesAndReturnsLastTask() {
        TaskList tasks = new TaskList();
        Task firstTask = new ToDo("first task");
        Task secondTask = new ToDo("second task");
        tasks.add(firstTask);
        tasks.add(secondTask);

        Task removedTask = tasks.removeLast();

        assertSame(secondTask, removedTask);
        assertEquals(1, tasks.size());
        assertSame(firstTask, tasks.get(1));
    }

    @Test
    void get_missingTask_assertionThrown() {
        TaskList tasks = new TaskList();

        assertThrows(AssertionError.class, () -> tasks.get(1));
    }

    @Test
    void restore_positionOutsideList_assertionThrown() {
        TaskList tasks = new TaskList();

        assertThrows(AssertionError.class, () -> tasks.restore(2, new ToDo("read book")));
    }

    @Test
    void removeLast_emptyList_assertionThrown() {
        TaskList tasks = new TaskList();

        assertThrows(AssertionError.class, tasks::removeLast);
    }

    @Test
    void replaceAll_sourceListChanged_taskListIsUnaffected() {
        Task task = new ToDo("read book");
        ArrayList<Task> sourceTasks = new ArrayList<>(List.of(task));
        TaskList tasks = new TaskList();
        tasks.replaceAll(sourceTasks);

        sourceTasks.clear();

        assertEquals(1, tasks.size());
        assertSame(task, tasks.get(1));
    }

    @Test
    void snapshot_taskListChanged_snapshotIsImmutableAndUnchanged() {
        TaskList tasks = new TaskList();
        Task firstTask = new ToDo("first task");
        tasks.add(firstTask);
        List<Task> snapshot = tasks.snapshot();

        tasks.add(new ToDo("second task"));

        assertEquals(1, snapshot.size());
        assertSame(firstTask, snapshot.get(0));
        assertThrows(
                UnsupportedOperationException.class, () -> snapshot.add(new ToDo("third task")));
    }

    @Test
    void clear_nonEmptyList_emptiesList() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("read book"));

        tasks.clear();

        assertEquals(0, tasks.size());
        assertFalse(tasks.containsTaskNumber(1));
    }

    @Test
    void find_matchingDescriptions_returnsMatchesInOriginalOrder() {
        Task firstMatch = new ToDo("read book");
        Task nonMatch = new ToDo("write report");
        Task secondMatch = new ToDo("return BOOK");
        TaskList tasks = new TaskList();
        tasks.replaceAll(List.of(firstMatch, nonMatch, secondMatch));

        List<Task> matches = tasks.find("book");

        assertEquals(List.of(firstMatch, secondMatch), matches);
        assertThrows(
                UnsupportedOperationException.class, () -> matches.add(new ToDo("book flight")));
    }

    @Test
    void find_noMatchingDescription_returnsEmptyList() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("read book"));

        assertTrue(tasks.find("report").isEmpty());
    }

}
