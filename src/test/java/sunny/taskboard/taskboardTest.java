package sunny.taskboard;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import task.Task;
import task.ToDo;

public class TaskboardTest {
    private Taskboard taskboard = Taskboard.getInstance();

    @BeforeEach
    public void resetTaskboard() {
        taskboard.clearTasks();
    }

    @Test
    public void newTaskboardHasZeroTasks() {
        assertEquals(0, taskboard.getTaskCount());
    }

    @Test
    public void addTaskIncreasesTaskCount() {
        Task task = new ToDo("buy milk");

        taskboard.addTask(task);

        assertEquals(1, taskboard.getTaskCount());
    }

    @Test
    public void addTaskCanBeRetrieved() {
        Task task = new ToDo("buy milk");

        taskboard.addTask(task);

        assertEquals(task, taskboard.getTask(0));
    }

    @Test
    public void multipleTasksCanBeAdded() {
        Task first = new ToDo("buy milk");
        Task second = new ToDo("do homework");

        taskboard.addTask(first);

        taskboard.addTask(second);

        assertEquals(2, taskboard.getTaskCount());
        assertEquals(first, taskboard.getTask(0));
        assertEquals(second, taskboard.getTask(1));
    }

    @Test
    public void removeTaskRemovesCorrectTask() {
        Task first = new ToDo("buy milk");
        Task second = new ToDo("do homework");

        taskboard.addTask(first);

        taskboard.addTask(second);

        taskboard.removeTask(0);

        assertEquals(1, taskboard.getTaskCount());
        assertEquals(second, taskboard.getTask(0));
    }

    @Test
    public void getTasksReturnsAllTasks() {
        Task first = new ToDo("buy milk");
        Task second = new ToDo("do homework");

        taskboard.addTask(first);

        taskboard.addTask(second);

        assertEquals(2, taskboard.getTasks().size());
        assertEquals(first, taskboard.getTasks().get(0));
        assertEquals(second, taskboard.getTasks().get(1));
    }

    @Test
    public void singletonReturnsSameInstance() {
        Taskboard first = Taskboard.getInstance();
        Taskboard second = Taskboard.getInstance();

        assertSame(first, second);
    }

    @Test
    public void getTaskWithInvalidIndexThrowsException() {
        assertThrows(
                IndexOutOfBoundsException.class,
                () -> taskboard.getTask(0)
        );
    }

    @Test
    public void removeTaskFromEmptyBoardThrowsException() {
        assertThrows(
                IndexOutOfBoundsException.class,
                () -> taskboard.removeTask(0)
        );
    }

    @Test
    public void getTaskBeyondEndThrowsException() {
        Task task = new ToDo("test");

        taskboard.addTask(task);

        assertThrows(
                IndexOutOfBoundsException.class,
                () -> taskboard.getTask(1)
        );
    }

    @Test
    public void addTaskUpdatesTaskCount() {
        taskboard.addTask(new ToDo("hello"));

        assertEquals(0, taskboard.getTaskCount());
    }
}
