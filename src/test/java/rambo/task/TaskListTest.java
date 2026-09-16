package rambo.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import rambo.exception.RamboException;

/**
 * Tests adding, updating, deleting, and retrieving tasks from {@link TaskList}.
 */
public class TaskListTest {
    @Test
    void constructor_mutableInitialList_copiesInitialTasks() {
        List<Task> initialTasks = new ArrayList<>();
        initialTasks.add(new Task("buy milk"));
        TaskList taskList = new TaskList(initialTasks);

        initialTasks.add(new Task("read book"));

        assertEquals(1, taskList.getTasks().size());
    }

    @Test
    void constructor_nullInitialList_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> new TaskList(null));
    }

    @Test
    void add_task_appendsTask() {
        TaskList taskList = new TaskList(List.of());

        taskList.add(new Task("buy milk"));

        assertEquals("buy milk", taskList.getTasks().get(0).getTaskName());
    }

    @Test
    void add_nullTask_throwsAssertionError() {
        TaskList taskList = new TaskList(List.of());

        assertThrows(AssertionError.class, () -> taskList.add(null));
    }

    @Test
    void toggle_validOneBasedTaskNumber_togglesSelectedTask() {
        Task firstTask = new Task("buy milk");
        Task secondTask = new Task("read book");
        TaskList taskList = new TaskList(List.of(firstTask, secondTask));

        taskList.toggle(2);

        assertFalse(firstTask.isDone());
        assertTrue(secondTask.isDone());
    }

    @Test
    void toggle_zeroTaskNumber_throwsRamboException() {
        TaskList taskList = new TaskList(List.of(new Task("buy milk")));

        assertThrows(RamboException.class, () -> taskList.toggle(0));
    }

    @Test
    void toggle_taskNumberBeyondList_throwsRamboException() {
        TaskList taskList = new TaskList(List.of(new Task("buy milk")));

        assertThrows(RamboException.class, () -> taskList.toggle(2));
    }

    @Test
    void setPriority_validTaskNumber_updatesSelectedTask() {
        Task firstTask = new Task("buy milk");
        Task secondTask = new Task("read book");
        TaskList taskList = new TaskList(List.of(firstTask, secondTask));

        taskList.setPriority(2, 1);

        assertFalse(firstTask.hasPriority());
        assertEquals(1, secondTask.getPriorityLevel());
    }

    @Test
    void setPriority_invalidTaskNumberOrLevel_throwsRamboException() {
        TaskList taskList = new TaskList(List.of(new Task("buy milk")));

        assertThrows(RamboException.class, () -> taskList.setPriority(2, 1));
        assertThrows(RamboException.class, () -> taskList.setPriority(1, 4));
    }

    @Test
    void delete_validOneBasedTaskNumber_removesSelectedTask() {
        TaskList taskList = new TaskList(List.of(new Task("buy milk"), new Task("read book")));

        taskList.delete(1);

        assertEquals(1, taskList.getTasks().size());
        assertEquals("read book", taskList.getTasks().get(0).getTaskName());
    }

    @Test
    void delete_invalidTaskNumber_throwsRamboException() {
        TaskList taskList = new TaskList(List.of());

        assertThrows(RamboException.class, () -> taskList.delete(1));
    }

    @Test
    void getTasks_returnedList_cannotBeModified() {
        TaskList taskList = new TaskList(List.of(new Task("buy milk")));
        List<Task> returnedTasks = taskList.getTasks();

        assertThrows(UnsupportedOperationException.class, () -> returnedTasks.add(new Task("read book")));
    }

    @Test
    void getTask_validOneBasedTaskNumber_returnsSelectedTask() {
        Task expectedTask = new Task("read book");
        TaskList taskList = new TaskList(List.of(new Task("buy milk"), expectedTask));

        assertEquals(expectedTask, taskList.getTask(2));
    }
}
