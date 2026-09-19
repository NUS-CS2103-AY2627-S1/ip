package benji;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;


public class TaskListTest {
    @Test
    public void addTask_taskAppearsInList() {
        TaskList tasks = new TaskList();
        Todo task = new Todo("Testing");
        tasks.add(task);
        assertEquals(1, tasks.size());
        assertEquals(task, tasks.get(0));
    }

    @Test
    public void deleteTask_removesCorrectTaskAndKeepsOrder() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("first task"));
        tasks.add(new Todo("second task"));
        tasks.add(new Todo("third task"));

        Task deletedTask = tasks.delete(1);

        assertEquals("[T][ ] second task", deletedTask.toString());
        assertEquals(2, tasks.size());
        assertEquals("first task", tasks.get(0).getDescription());
        assertEquals("third task", tasks.get(1).getDescription());
    }
}
