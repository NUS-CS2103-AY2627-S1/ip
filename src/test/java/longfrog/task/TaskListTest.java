package longfrog.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

class TaskListTest {
    @Test
    void constructor_nullLoadedTasks_createsEmptyList() {
        TaskList taskList = new TaskList(null);

        assertEquals(0, taskList.getCount());
        assertTrue(taskList.getTasks().isEmpty());
    }

    @Test
    void constructor_loadedTasksListIsModified_doesNotChangeTaskList() {
        ArrayList<Task> loadedTasks = new ArrayList<>();
        loadedTasks.add(new Todo("read book"));
        TaskList taskList = new TaskList(loadedTasks);

        loadedTasks.clear();

        assertEquals(1, taskList.getCount());
    }

    @Test
    void addTask_addsTaskAndMakesItRetrievable() {
        TaskList taskList = new TaskList();
        Todo todo = new Todo("read book");

        taskList.addTask(todo);

        assertEquals(1, taskList.getCount());
        assertSame(todo, taskList.getTask(0));
        assertEquals(1, taskList.getTasks().size());
    }

    @Test
    void getTasks_attemptToModifyReturnedList_throwsException() {
        TaskList taskList = new TaskList();
        taskList.addTask(new Todo("read book"));

        assertThrows(UnsupportedOperationException.class, () -> taskList.getTasks().add(new Todo("write notes")));
        assertEquals(1, taskList.getCount());
    }

    @Test
    void addTask_nullTask_violatesTaskListInvariant() {
        TaskList taskList = new TaskList();

        AssertionError error = assertThrows(AssertionError.class, () -> taskList.addTask(null));

        assertEquals("Task list must not contain null entries", error.getMessage());
        assertTrue(taskList.getTasks().isEmpty());
    }

    @Test
    void constructor_loadedTasksContainingNull_violatesTaskListInvariant() {
        List<Task> loadedTasks = Arrays.asList(new Todo("read book"), null);

        AssertionError error = assertThrows(AssertionError.class, () -> new TaskList(loadedTasks));

        assertEquals("Loaded task list must not contain null entries", error.getMessage());
    }

    @Test
    void taskExistsAndGetTask_boundaryIndexes_reportCorrectResult() {
        TaskList taskList = new TaskList(new ArrayList<>());
        Todo first = new Todo("first");
        Todo second = new Todo("second");
        taskList.addTask(first);
        taskList.addTask(second);

        assertFalse(taskList.taskExists(-1));
        assertTrue(taskList.taskExists(0));
        assertTrue(taskList.taskExists(1));
        assertFalse(taskList.taskExists(2));
        assertSame(first, taskList.getTask(0));
        assertSame(second, taskList.getTask(1));
        assertNull(taskList.getTask(-1));
        assertNull(taskList.getTask(2));
    }

    @Test
    void removeTask_middleIndex_removesTaskAndReindexesFollowingTasks() {
        TaskList taskList = new TaskList();
        Todo first = new Todo("first");
        Todo middle = new Todo("middle");
        Todo last = new Todo("last");
        taskList.addTask(first);
        taskList.addTask(middle);
        taskList.addTask(last);

        Task removedTask = taskList.removeTask(1);

        assertSame(middle, removedTask);
        assertEquals(2, taskList.getCount());
        assertSame(first, taskList.getTask(0));
        assertSame(last, taskList.getTask(1));
    }

    @Test
    void removeTask_invalidIndex_returnsNullWithoutChangingList() {
        TaskList taskList = new TaskList();
        Todo todo = new Todo("read book");
        taskList.addTask(todo);

        assertNull(taskList.removeTask(-1));
        assertNull(taskList.removeTask(1));
        assertEquals(1, taskList.getCount());
        assertSame(todo, taskList.getTask(0));
    }

    @Test
    void findDuplicateIndex_nullProposedTask_violatesTaskListInvariant() {
        TaskList taskList = new TaskList();

        assertThrows(AssertionError.class, () -> taskList.findDuplicateIndex(null));
    }

    @Test
    void constructor_immutableLoadedTasks_copiesTasksWithoutQueryingForNull() {
        Todo task = new Todo("read book");

        TaskList taskList = new TaskList(List.of(task));

        assertEquals(List.of(task), taskList.getTasks());
    }

    @Test
    void findDuplicateIndex_multipleMatches_returnsFirstMatch() {
        TaskList taskList = new TaskList();
        taskList.addTask(new Todo("first"));
        taskList.addTask(new Todo("Read Book"));
        taskList.addTask(new Todo("read   book"));

        assertEquals(1, taskList.findDuplicateIndex(new Todo("READ BOOK")));
        assertEquals(-1, taskList.findDuplicateIndex(new Todo("different")));
    }

    @Test
    void countDuplicateEntries_multipleGroups_countsOccurrencesBeyondFirst() {
        LocalDateTime deadlineTime = LocalDateTime.of(2024, 1, 2, 9, 0);
        TaskList taskList = new TaskList(new ArrayList<>(List.of(
                new Todo("Read Book"),
                new Todo("read   book"),
                new Todo("READ BOOK"),
                new Deadline("submit", deadlineTime),
                new Deadline("Submit", deadlineTime),
                new Deadline("submit", deadlineTime.plusDays(1)))));

        assertEquals(3, taskList.countDuplicateEntries());
    }
}
