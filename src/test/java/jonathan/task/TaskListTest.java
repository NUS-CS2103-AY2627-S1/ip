package jonathan.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

public class TaskListTest {

    @Test
    public void addTask_validTask_increasesSize() {
        TaskList taskList = new TaskList();
        ToDo todo = new ToDo("read book");

        taskList.addTask(todo);

        assertEquals(1, taskList.getSize());
        assertSame(todo, taskList.getTask(0));
    }

    @Test
    public void deleteTask_existingTask_removesTask() {
        TaskList taskList = new TaskList();
        ToDo first = new ToDo("first");
        ToDo second = new ToDo("second");

        taskList.addTask(first);
        taskList.addTask(second);

        Task deleted = taskList.deleteTask(0);

        assertSame(first, deleted);
        assertEquals(1, taskList.getSize());
        assertSame(second, taskList.getTask(0));
    }

    @Test
    public void emptyTaskList_getTask_throwsAssertionError() {
        TaskList taskList = new TaskList();

        assertThrows(AssertionError.class, () -> {
            taskList.getTask(0);
        });
    }

    @Test
    public void getTasksByPriority_returnsOnlyMatchingTasks() {
        TaskList taskList = new TaskList();
        ToDo high = new ToDo("high task");
        ToDo low = new ToDo("low task");
        high.setPriority(Priority.HIGH);
        low.setPriority(Priority.LOW);

        taskList.addTask(high);
        taskList.addTask(low);

        assertEquals(List.of(high),
                taskList.getTasksByPriority(Priority.HIGH));
    }

    @Test
    public void getTasksSortedByPriority_ordersHighBeforeLowAndNone() {
        TaskList taskList = new TaskList();
        ToDo none = new ToDo("no priority task");
        ToDo low = new ToDo("low task");
        ToDo high = new ToDo("high task");
        ToDo medium = new ToDo("medium task");
        low.setPriority(Priority.LOW);
        high.setPriority(Priority.HIGH);
        medium.setPriority(Priority.MEDIUM);

        taskList.addTask(none);
        taskList.addTask(low);
        taskList.addTask(high);
        taskList.addTask(medium);
        List<Task> sortedTasks = taskList.getTasksSortedByPriority();

        assertSame(high, sortedTasks.get(0));
        assertSame(medium, sortedTasks.get(1));
        assertSame(low, sortedTasks.get(2));
        assertSame(none, sortedTasks.get(3));
        assertSame(none, taskList.getTask(0));
    }

    @Test
    public void getTasksSortedByPriority_preservesEqualPriorityOrder() {
        TaskList taskList = new TaskList();
        ToDo first = new ToDo("first high task");
        ToDo second = new ToDo("second high task");

        first.setPriority(Priority.HIGH);
        second.setPriority(Priority.HIGH);
        taskList.addTask(first);
        taskList.addTask(second);

        assertEquals(List.of(first, second),
                taskList.getTasksSortedByPriority());
    }
}
