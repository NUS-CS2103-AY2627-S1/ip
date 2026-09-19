package nova;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

public class TaskListTest {
    @Test
    public void add_task_increasesSize() {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("a"));
        taskList.add(new Deadline("b", LocalDate.of(2026, 8, 28)));
        assertEquals(2, taskList.size());
    }

    @Test
    public void removeCompletedTasks_removesOnlyDoneTasks() {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("a"));
        Todo done = new Todo("b");
        done.markDone();
        taskList.add(done);
        ArrayList<Task> archived = taskList.removeCompletedTasks();
        assertEquals(1, archived.size());
        assertEquals("b", archived.get(0).getDescription());
        assertEquals(1, taskList.size());
        assertEquals("a", taskList.get(0).getDescription());
    }

    @Test
    public void remove_task_returnsRemovedTaskAndShrinks() {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("a"));
        taskList.add(new Todo("b"));
        Task removed = taskList.remove(0);
        assertEquals("a", removed.getDescription());
        assertEquals(1, taskList.size());
        assertEquals("b", taskList.get(0).getDescription());
    }
}
