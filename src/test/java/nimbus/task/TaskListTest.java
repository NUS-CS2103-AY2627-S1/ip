package nimbus.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class TaskListTest {

    @Test
    public void addTask_addsTaskToList() throws Exception {
        TaskList taskList = new TaskList();
        Todo todo = new Todo("read book");
        taskList.addTask(todo);
        assertEquals(1, taskList.getSize());
        assertEquals(todo, taskList.getTask(0));
    }

    @Test
    public void removeTask_removesTaskFromList() throws Exception {
        TaskList taskList = new TaskList();
        Todo todo = new Todo("read book");
        taskList.addTask(todo);
        taskList.removeTask(0);
        assertEquals(0, taskList.getSize());
    }

    @Test
    public void getSize_returnsCorrectSize() throws Exception {
        TaskList taskList = new TaskList();
        assertEquals(0, taskList.getSize());
        taskList.addTask(new Todo("read book"));
        assertEquals(1, taskList.getSize());
    }
}
