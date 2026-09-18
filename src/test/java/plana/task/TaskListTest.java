package plana.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.Test;

import plana.command.TaskAction;
import plana.exception.PlanaException;

/**
 * Tests the task-list operations used by Plana's commands.
 */
public class TaskListTest {

    /**
     * Verifies that tasks are added in order and can be retrieved by their
     * zero-based list index.
     */
    @Test
    public void addAndGet_tasksAddedInOrderAndSizeUpdated() {
        TaskList taskList = new TaskList();
        Task firstTask = new ToDo("first task");
        Task secondTask = new ToDo("second task");

        assertTrue(taskList.isEmpty());
        taskList.add(firstTask);
        taskList.add(secondTask);

        assertFalse(taskList.isEmpty());
        assertEquals(2, taskList.size());
        assertSame(firstTask, taskList.get(0));
        assertSame(secondTask, taskList.get(1));
    }

    /**
     * Verifies that adding a variable number of tasks preserves their supplied
     * order and supports an empty input.
     */
    @Test
    public void add_multipleTasksAddedInOrderAndEmptyInputIgnored() {
        TaskList taskList = new TaskList();
        Task firstTask = new ToDo("first task");
        Task secondTask = new ToDo("second task");

        taskList.add();
        taskList.add(firstTask, secondTask);

        assertEquals(2, taskList.size());
        assertSame(firstTask, taskList.get(0));
        assertSame(secondTask, taskList.get(1));
    }

    @Test
    public void addAtIndex_duplicateDetectionAndRestore_preserveTaskIdentity() throws PlanaException {
        Task firstTask = new ToDo("first task");
        Task insertedTask = new ToDo("inserted task");
        TaskList taskList = new TaskList(List.of(firstTask));

        taskList.add(0, insertedTask);
        List<Task> snapshot = taskList.copyTasks();
        taskList.remove(1);
        taskList.restoreTasks(snapshot);

        assertSame(insertedTask, taskList.get(0));
        assertSame(firstTask, taskList.get(1));
        assertTrue(taskList.containsSameTask(new ToDo("first task")));
        assertFalse(taskList.containsSameTask(new ToDo("different task")));
        assertFalse(taskList.isDone(TaskAction.MARK, "1"));
    }

    /**
     * Verifies that deletion uses one-based task numbers, returns the removed
     * task, and preserves the order of remaining tasks.
     */
    @Test
    public void delete_middleTask_removedAndRemainingTasksShifted() throws PlanaException {
        Task firstTask = new ToDo("first task");
        Task middleTask = new ToDo("middle task");
        Task lastTask = new ToDo("last task");
        TaskList taskList = new TaskList(List.of(firstTask, middleTask, lastTask));

        Task deletedTask = taskList.delete("2");

        assertSame(middleTask, deletedTask);
        assertEquals(2, taskList.size());
        assertSame(firstTask, taskList.get(0));
        assertSame(lastTask, taskList.get(1));
    }

    /**
     * Verifies that removing by zero-based index returns the selected task and
     * shifts the remaining tasks into the correct order.
     */
    @Test
    public void remove_validIndex_removedAndRemainingTasksShifted() {
        Task firstTask = new ToDo("first task");
        Task middleTask = new ToDo("middle task");
        Task lastTask = new ToDo("last task");
        TaskList taskList = new TaskList(List.of(firstTask, middleTask, lastTask));

        Task removedTask = taskList.remove(1);

        assertSame(middleTask, removedTask);
        assertEquals(2, taskList.size());
        assertSame(firstTask, taskList.get(0));
        assertSame(lastTask, taskList.get(1));
    }

    /**
     * Verifies that iteration exposes every task in insertion order.
     */
    @Test
    public void iterator_tasksAreReturnedInInsertionOrder() {
        TaskList taskList = new TaskList(List.of(
                new ToDo("first task"), new ToDo("second task"), new ToDo("third task")));
        Iterator<Task> iterator = taskList.iterator();
        List<Task> iteratedTasks = new ArrayList<>();

        iterator.forEachRemaining(iteratedTasks::add);

        assertEquals(List.of("[T][ ] first task", "[T][ ] second task", "[T][ ] third task"),
                iteratedTasks.stream().map(Task::toString).toList());
    }

    /**
     * Verifies that marking and unmarking a valid task update its completion
     * status and return the selected task.
     */
    @Test
    public void markAndUnmark_validTask_statusChangesAndTaskReturned() throws PlanaException {
        Task task = new ToDo("finish tests");
        TaskList taskList = new TaskList(List.of(task));

        assertEquals(" ", task.getStatusIcon());
        assertSame(task, taskList.mark("1"));
        assertEquals("X", task.getStatusIcon());
        assertSame(task, taskList.unmark("1"));
        assertEquals(" ", task.getStatusIcon());
    }

    /**
     * Verifies that invalid task numbers produce the appropriate guidance and
     * do not change the task list.
     */
    @Test
    public void taskNumberValidation_invalidInputs_exceptionAndStatePreserved() {
        Task task = new ToDo("unchanged task");
        TaskList taskList = new TaskList(List.of(task));

        PlanaException missingNumber = assertThrows(PlanaException.class, () -> taskList.mark(""));
        assertEquals("Oops, mark needs a task number. Try: mark <number>.", missingNumber.getMessage());

        PlanaException nonNumericNumber = assertThrows(PlanaException.class, () -> taskList.delete("abc"));
        assertEquals("Oops, 'abc' isn't a valid task number."
                + " Use a positive whole number, like delete 1.", nonNumericNumber.getMessage());

        PlanaException nonPositiveNumber = assertThrows(PlanaException.class, () -> taskList.unmark("0"));
        assertEquals("Oops, task numbers start at 1."
                + " Try unmark 1 or another number from list.", nonPositiveNumber.getMessage());

        PlanaException outOfRangeNumber = assertThrows(PlanaException.class, () -> taskList.delete("2"));
        assertEquals("Oops, task 2 doesn't exist yet."
                + " Type list to check the task numbers you have.", outOfRangeNumber.getMessage());

        assertEquals(1, taskList.size());
        assertSame(task, taskList.get(0));
        assertEquals(" ", task.getStatusIcon());
    }

    @Test
    public void constructor_nullTask_assertionFails() {
        List<Task> tasksWithNull = new ArrayList<>();
        tasksWithNull.add(null);

        assertThrows(AssertionError.class, () -> new TaskList(tasksWithNull));
    }

    @Test
    public void nullTaskInputs_assertionsFail() {
        TaskList taskList = new TaskList();

        assertThrows(AssertionError.class, () -> taskList.add((Task[]) null));
        assertThrows(AssertionError.class, () -> taskList.add(0, null));
        assertThrows(AssertionError.class, () -> taskList.restoreTasks(null));
    }
}
