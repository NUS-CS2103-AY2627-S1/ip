package shrekanddonkey.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests task operations on a {@link TaskList}.
 */
public class TaskListTest {
    @Test
    public void remove_firstTask_returnsTaskAndUpdatesSize() {
        Task firstTask = new Task("first task");
        Task secondTask = new Task("second task");
        TaskList taskList = createTaskList(firstTask, secondTask);

        Task removedTask = taskList.remove(0);

        assertSame(firstTask, removedTask);
        assertEquals(1, taskList.size());
        assertSame(secondTask, taskList.get(0));
    }

    @Test
    public void remove_middleTask_preservesRemainingOrder() {
        Task firstTask = new Task("first task");
        Task middleTask = new Task("middle task");
        Task lastTask = new Task("last task");
        TaskList taskList = createTaskList(firstTask, middleTask, lastTask);

        Task removedTask = taskList.remove(1);

        assertSame(middleTask, removedTask);
        assertEquals(2, taskList.size());
        assertSame(firstTask, taskList.get(0));
        assertSame(lastTask, taskList.get(1));
    }

    @Test
    public void remove_onlyTask_returnsTaskAndLeavesEmptyList() {
        Task onlyTask = new Task("only task");
        TaskList taskList = createTaskList(onlyTask);

        assertSame(onlyTask, taskList.remove(0));
        assertEquals(0, taskList.size());
    }

    @Test
    public void remove_emptyList_throwsIndexOutOfBoundsException() {
        TaskList taskList = new TaskList();

        assertThrows(IndexOutOfBoundsException.class, () -> taskList.remove(0));
    }

    @Test
    public void remove_negativeIndex_throwsIndexOutOfBoundsException() {
        TaskList taskList = createTaskList(new Task("task"));

        assertThrows(IndexOutOfBoundsException.class, () -> taskList.remove(-1));
    }

    @Test
    public void remove_pastEndIndex_throwsIndexOutOfBoundsException() {
        TaskList taskList = createTaskList(new Task("task"));

        assertThrows(IndexOutOfBoundsException.class, () -> taskList.remove(1));
    }

    @Test
    public void findTasks_matchingKeyword_returnsMatchingTasks() {
        Task firstTask = new Task("read book");
        Task secondTask = new Task("return book");
        Task thirdTask = new Task("eat apple");
        TaskList taskList = createTaskList(firstTask, secondTask, thirdTask);

        List<Task> matchingTasks = taskList.findTasks("book");

        assertEquals(2, matchingTasks.size());
        assertSame(firstTask, matchingTasks.get(0));
        assertSame(secondTask, matchingTasks.get(1));
    }

    @Test
    public void findTasks_noMatchingKeyword_returnsEmptyList() {
        Task firstTask = new Task("read book");
        Task secondTask = new Task("return book");
        TaskList taskList = createTaskList(firstTask, secondTask);

        List<Task> matchingTasks = taskList.findTasks("apple");

        assertTrue(matchingTasks.isEmpty());
    }

    @Test
    public void findTasks_partialKeywordMatch_returnsMatchingTasks() {
        Task task = new Task("assignment submission");
        TaskList taskList = createTaskList(task);

        List<Task> matchingTasks = taskList.findTasks("sign");

        assertEquals(1, matchingTasks.size());
        assertSame(task, matchingTasks.get(0));
    }

    private TaskList createTaskList(Task... tasks) {
        TaskList taskList = new TaskList();
        for (Task task : tasks) {
            taskList.add(task);
        }
        return taskList;
    }
}
