package woofer.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;

/**
 * Tests the storage and manipulation of tasks in a task list.
 */
public class TaskListTest {
    /**
     * Verifies that tasks are added and retrieved in insertion order.
     */
    @Test
    public void addTaskStoresTasksInInsertionOrder() {
        TaskList taskList = new TaskList();
        Task firstTask = new Todo("first task");
        Task secondTask = new Todo("second task");

        assertTrue(taskList.addTask(firstTask));
        assertTrue(taskList.addTask(secondTask));

        assertEquals(2, taskList.size());
        assertEquals(firstTask, taskList.getTask(1));
        assertEquals(secondTask, taskList.getTask(2));
        assertEquals(List.of(firstTask, secondTask), taskList.getTasks());
    }

    /**
     * Verifies that deleting a task removes it and shifts later tasks forward.
     */
    @Test
    public void deleteTaskRemovesTaskAndShiftsRemainingTasks() {
        TaskList taskList = new TaskList();
        Task firstTask = new Todo("first task");
        Task secondTask = new Todo("second task");
        taskList.addTask(firstTask);
        taskList.addTask(secondTask);

        assertEquals(firstTask, taskList.deleteTask(1));
        assertEquals(1, taskList.size());
        assertEquals(secondTask, taskList.getTask(1));
        assertNull(taskList.deleteTask(2));
    }

    /**
     * Verifies that a deleted task can be restored at its original position.
     */
    @Test
    public void insertTaskRestoresOriginalPosition() {
        TaskList taskList = new TaskList();
        Task firstTask = new Todo("first task");
        Task secondTask = new Todo("second task");
        taskList.addTask(firstTask);
        taskList.addTask(secondTask);

        assertEquals(firstTask, taskList.deleteTask(1));
        assertTrue(taskList.insertTask(1, firstTask));
        assertEquals(List.of(firstTask, secondTask), taskList.getTasks());
    }

    /**
     * Verifies that task numbers outside the list return no task.
     */
    @Test
    public void getTaskReturnsNullForInvalidTaskNumbers() {
        TaskList taskList = new TaskList();
        taskList.addTask("read book");

        assertNull(taskList.getTask(0));
        assertNull(taskList.getTask(2));
        assertNull(taskList.getTask(-1));
    }

    /**
     * Verifies that finding tasks is case-insensitive and preserves insertion order.
     */
    @Test
    public void findTasksMatchesKeywordCaseInsensitively() {
        TaskList taskList = new TaskList();
        Task firstMatch = new Todo("Read a Book");
        Task nonMatch = new Todo("Write a report");
        Task secondMatch = new Todo("Return the book");
        taskList.addTask(firstMatch);
        taskList.addTask(nonMatch);
        taskList.addTask(secondMatch);

        assertEquals(List.of(firstMatch, secondMatch), taskList.findTasks("BOOK"));
        assertTrue(taskList.findTasks("holiday").isEmpty());
    }

    /**
     * Verifies that the task list rejects tasks beyond its capacity.
     */
    @Test
    public void addTaskRejectsTasksBeyondCapacity() {
        TaskList taskList = new TaskList();

        for (int index = 0; index < 100; index++) {
            assertTrue(taskList.addTask("task " + index));
        }

        assertFalse(taskList.addTask("one task too many"));
        assertEquals(100, taskList.size());
    }

    /** Checks insertion at both boundaries and rejection outside the valid range. */
    @Test
    public void insertionBoundariesAndCapacity() {
        TaskList tasks = new TaskList();
        Task first = new Todo("first");
        assertFalse(tasks.insertTask(0, first));
        assertFalse(tasks.insertTask(2, first));
        assertTrue(tasks.insertTask(1, first));
        Task last = new Todo("last");
        assertTrue(tasks.insertTask(2, last));
        assertEquals(List.of(first, last), tasks.getTasks());
        for (int i = 2; i < 100; i++) {
            assertTrue(tasks.addTask("task " + i));
        }
        assertFalse(tasks.insertTask(1, new Todo("overflow")));
        assertNull(tasks.deleteTask(0));
        assertNull(tasks.deleteTask(-1));
        assertNull(tasks.deleteTask(101));
        assertEquals(100, tasks.size());
        tasks.deleteTask(100);
        assertTrue(tasks.insertTask(100, last));
    }

    /** Checks list snapshots cannot be structurally mutated and do not grow with the source. */
    @Test
    public void snapshotsAndSearchResultsAreUnmodifiable() {
        TaskList tasks = new TaskList();
        tasks.addTask("walk dog");
        List<Task> snapshot = tasks.getTasks();
        List<Task> matches = tasks.findTasks("dog");
        assertThrows(UnsupportedOperationException.class, snapshot::clear);
        assertThrows(UnsupportedOperationException.class, matches::clear);
        tasks.addTask("feed dog");
        assertEquals(1, snapshot.size());
        assertEquals(1, matches.size());
        assertTrue(tasks.findTasks(null).isEmpty());
        assertTrue(tasks.findTasks("  ").isEmpty());
        assertTrue(tasks.findTasks("").isEmpty());
    }

    /** Checks search uses a stable locale even when the system language has different casing rules. */
    @Test
    public void searchIsIndependentOfDefaultLocale() {
        Locale original = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));
            TaskList tasks = new TaskList();
            tasks.addTask("FINISH homework");
            assertEquals(1, tasks.findTasks("finish").size());
        } finally {
            Locale.setDefault(original);
        }
    }

}
