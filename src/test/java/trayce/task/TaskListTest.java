package trayce.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

/** Tests task-list operations that control the order and contents of saved tasks. */
class TaskListTest {
    @Test
    void delete_middleTask_remainingTasksStayInOrder() {
        TaskList taskList = new TaskList();
        Task firstTask = new Task("first");
        Task middleTask = new Task("middle");
        Task lastTask = new Task("last");
        taskList.add(firstTask);
        taskList.add(middleTask);
        taskList.add(lastTask);

        Task deletedTask = taskList.delete(1);

        assertEquals(middleTask, deletedTask);
        assertEquals(2, taskList.size());
        assertEquals(firstTask, taskList.get(0));
        assertEquals(lastTask, taskList.get(1));
    }

    @Test
    void getAndDelete_invalidIndexes_returnNullWithoutChangingList() {
        TaskList taskList = new TaskList();
        Task task = new Task("read book");
        taskList.add(task);

        assertNull(taskList.get(-1));
        assertNull(taskList.get(1));
        assertNull(taskList.delete(-1));
        assertNull(taskList.delete(1));
        assertEquals(1, taskList.size());
        assertEquals(task, taskList.get(0));
    }
}
