import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import tardt.task.Task;
import tardt.task.TaskList;
import tardt.task.ToDo;

/**
 * Unit tests for methods in the TaskList class
 */
public class TaskListTest {
    @Test
    public void testAdd() {
        TaskList taskList = new TaskList();
        taskList.add(new ToDo("eat"));
        assertEquals(1, taskList.size());
    }

    @Test
    public void testRemove() {
        TaskList taskList = new TaskList();
        taskList.add(new ToDo("eat"));
        taskList.add(new ToDo("sleep"));
        assertEquals(2, taskList.size());
        taskList.delete(0);
        assertEquals(1, taskList.size());
    }

    @Test
    public void delete_validIndex_returnsCorrectTaskAndShiftsRemaining() {
        TaskList taskList = new TaskList();
        taskList.add(new ToDo("eat"));
        taskList.add(new ToDo("sleep"));

        Task removed = taskList.delete(0);

        assertEquals("eat", removed.getDescription());
        assertEquals(1, taskList.size());
        // confirm "sleep" shifted into index 0, not left dangling or duplicated
        assertEquals("sleep", taskList.get(0).getDescription());
    }

    @Test
    public void delete_indexOutOfBounds_throwsIndexOutOfBoundsException() {
        TaskList taskList = new TaskList();
        taskList.add(new ToDo("eat"));

        assertThrows(IndexOutOfBoundsException.class, () -> taskList.delete(5));
    }

    @Test
    public void constructor_withExistingList_wrapsProvidedTasks() {
        List<Task> initialTasks = new ArrayList<>();
        initialTasks.add(new ToDo("eat"));
        initialTasks.add(new ToDo("sleep"));

        TaskList taskList = new TaskList(initialTasks);

        assertEquals(2, taskList.size());
        assertEquals("eat", taskList.get(0).getDescription());
        assertEquals("sleep", taskList.get(1).getDescription());
    }


}
