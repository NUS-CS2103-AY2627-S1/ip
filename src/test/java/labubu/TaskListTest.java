package labubu;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

public class TaskListTest {
    @Test
    public void testAdd() {
        TaskList taskList = new TaskList();
        ToDo todo = new ToDo("task");

        taskList.addTask(todo);

        assertEquals(new ArrayList<>(List.of("T|INCOMPLETE|task")), taskList.toSaveFormat());
    }

    @Test
    public void testDelete() {
        TaskList taskList = new TaskList();
        ToDo todo = new ToDo("task");

        taskList.addTask(todo);
        taskList.removeTask(0);

        assertEquals(new ArrayList<>(), taskList.toSaveFormat());
    }

    @Test
    public void testFindAndGet() {
        TaskList taskList = new TaskList(List.of(new ToDo("read book"), new ToDo("write report")));

        assertEquals(2, taskList.getTaskListSize());
        assertEquals("read book", taskList.getTask(0).getTaskTitle());
        assertEquals(List.of("read book"),
                taskList.findTaskByKeyword("book").stream()
                        .map(Task::getTaskTitle)
                        .toList());
    }
}
