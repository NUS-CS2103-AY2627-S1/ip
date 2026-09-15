package bob.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import bob.exception.BobException;

/**
 * Tests task operations performed by {@link TaskList}.
 */
class TaskListTest {
    /**
     * Tests that the varargs constructor accepts no tasks.
     */
    @Test
    void constructor_noVarargs_emptyTaskListCreated() {
        TaskList taskList = new TaskList(new Task[0]);

        assertTrue(taskList.getTasks().isEmpty());
    }

    /**
     * Tests that the varargs constructor accepts one task.
     */
    @Test
    void constructor_oneVararg_taskListContainingTaskCreated() {
        Task task = new ToDo("read book");

        TaskList taskList = new TaskList(task);

        assertEquals(List.of(task), taskList.getTasks());
    }

    /**
     * Tests that the varargs constructor preserves multiple tasks in order.
     */
    @Test
    void constructor_multipleVarargs_orderedTaskListCreated() {
        Task firstTask = new ToDo("read book");
        Task secondTask = new ToDo("write report");

        TaskList taskList = new TaskList(firstTask, secondTask);

        assertEquals(List.of(firstTask, secondTask), taskList.getTasks());
    }

    /**
     * Tests that a valid one-based index marks and returns the selected task.
     *
     * @throws BobException If the valid index is unexpectedly rejected.
     */
    @Test
    void markTask_validOneBasedIndex_selectedTaskMarkedAndReturned() throws BobException {
        Task firstTask = new ToDo("read book");
        Task secondTask = new ToDo("write report");
        TaskList taskList = new TaskList(List.of(firstTask, secondTask));

        Task markedTask = taskList.markTask(2);

        assertFalse(firstTask.isDone());
        assertTrue(secondTask.isDone());
        assertSame(secondTask, markedTask);
    }

    /**
     * Tests that an index below the valid range is rejected.
     */
    @Test
    void markTask_indexBelowValidRange_exceptionThrown() {
        TaskList taskList = new TaskList(List.of(new ToDo("read book")));

        assertThrows(BobException.class, () -> taskList.markTask(0));
    }

    /**
     * Tests that an index above the valid range is rejected.
     */
    @Test
    void markTask_indexAboveValidRange_exceptionThrown() {
        TaskList taskList = new TaskList(List.of(new ToDo("read book")));

        assertThrows(BobException.class, () -> taskList.markTask(2));
    }

    /**
     * Tests that deleting a valid index removes only the selected task.
     *
     * @throws BobException If the valid index is unexpectedly rejected.
     */
    @Test
    void deleteTask_validIndex_selectedTaskDeleted() throws BobException {
        Task remainingTask = new ToDo("eat food");
        Task deletedTask = new ToDo("eat food again");
        TaskList taskList = new TaskList(List.of(remainingTask, deletedTask));

        taskList.deleteTask(2);

        assertEquals(1, taskList.getTasks().size());
        assertSame(remainingTask, taskList.getTasks().get(0));
    }

    @Test
    void replaceTask_validIndex_onlySelectedTaskReplaced() throws BobException {
        Task unchangedTask = new ToDo("read book");
        Task replacedTask = new ToDo("write report");
        Task replacementTask = new ToDo("write final report");
        TaskList taskList = new TaskList(unchangedTask, replacedTask);

        taskList.replaceTask(2, replacementTask);

        assertEquals(List.of(unchangedTask, replacementTask), taskList.getTasks());
    }

    @Test
    void replaceTask_invalidIndex_exceptionThrown() {
        TaskList taskList = new TaskList(new ToDo("read book"));

        assertThrows(BobException.class, () -> taskList.replaceTask(2, new ToDo("write report")));
    }

    /**
     * Tests that partial, case-insensitive matches are returned in task order.
     */
    @Test
    void findTasks_matchingDescriptions_matchingTasksReturnedInOrder() {
        Task firstTask = new ToDo("Read Book");
        Task secondTask = new ToDo("buy bookmark");
        Task nonMatchingTask = new ToDo("write report");
        TaskList taskList = new TaskList(List.of(firstTask, secondTask, nonMatchingTask));

        List<Task> matchingTasks = taskList.findTasks("BOOK");

        assertEquals(List.of(firstTask, secondTask), matchingTasks);
    }

    /**
     * Tests that no matches produces an empty result.
     */
    @Test
    void findTasks_noMatchingDescription_emptyListReturned() {
        TaskList taskList = new TaskList(List.of(new ToDo("write report")));

        assertTrue(taskList.findTasks("book").isEmpty());
    }

    /**
     * Tests that searching an empty task list produces an empty result.
     */
    @Test
    void findTasks_emptyTaskList_emptyListReturned() {
        TaskList taskList = new TaskList();

        assertTrue(taskList.findTasks("book").isEmpty());
    }

    /**
     * Tests that task dates are excluded from keyword matching.
     */
    @Test
    void findTasks_keywordOnlyInTaskDate_emptyListReturned() {
        Task deadline = new Deadline("return notes", LocalDate.of(2026, 9, 1));
        TaskList taskList = new TaskList(List.of(deadline));

        assertTrue(taskList.findTasks("2026").isEmpty());
    }
}
