package jimbo;

import jimbo.task.Task;
import jimbo.task.Todo;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Tests generated using ChatGPT 5.6-Luna

class TaskListTest {
    @Test
    void constructor_emptyList_createsEmptyTaskList() {
        TaskList taskList = new TaskList();

        assertTrue(taskList.isEmpty());
        assertEquals(0, taskList.size());
        assertEquals("", taskList.listAll());
    }

    @Test
    void constructor_existingList_usesTasksInGivenOrder() {
        Task firstTask = new Todo("first task");
        Task secondTask = new Todo("second task");
        List<Task> tasks = new ArrayList<>(List.of(firstTask, secondTask));

        TaskList taskList = new TaskList(tasks);

        assertEquals(2, taskList.size());
        assertSame(firstTask, taskList.get(0));
        assertSame(secondTask, taskList.get(1));
    }

    @Test
    void add_task_appendsTaskToList() {
        TaskList taskList = new TaskList();
        Task task = new Todo("read book");

        taskList.add(task);

        assertFalse(taskList.isEmpty());
        assertEquals(1, taskList.size());
        assertSame(task, taskList.get(0));
    }

    @Test
    void add_multipleTasks_preservesInsertionOrder() {
        Task firstTask = new Todo("first task");
        Task secondTask = new Todo("second task");
        TaskList taskList = new TaskList();

        taskList.add(firstTask);
        taskList.add(secondTask);

        assertEquals(2, taskList.size());
        assertSame(firstTask, taskList.get(0));
        assertSame(secondTask, taskList.get(1));
    }

    @Test
    void delete_taskAtValidIndex_removesCorrectTask() {
        Task firstTask = new Todo("first task");
        Task secondTask = new Todo("second task");
        Task thirdTask = new Todo("third task");
        TaskList taskList = new TaskList(new ArrayList<>(List.of(firstTask, secondTask, thirdTask)));

        taskList.delete(1);

        assertEquals(2, taskList.size());
        assertSame(firstTask, taskList.get(0));
        assertSame(thirdTask, taskList.get(1));
    }

    @Test
    void delete_onlyTask_makesTaskListEmpty() {
        TaskList taskList = new TaskList(new ArrayList<>(List.of(new Todo("task"))));

        taskList.delete(0);

        assertTrue(taskList.isEmpty());
        assertEquals(0, taskList.size());
        assertEquals("", taskList.listAll());
    }

    @Test
    void get_validIndex_returnsTaskAtIndex() {
        Task task = new Todo("read book");
        TaskList taskList = new TaskList(new ArrayList<>(List.of(task)));

        assertSame(task, taskList.get(0));
    }

    @Test
    void get_invalidIndex_throwsIndexOutOfBoundsException() {
        TaskList taskList = new TaskList();

        assertThrows(IndexOutOfBoundsException.class, () -> taskList.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> taskList.get(0));
    }

    @Test
    void delete_invalidIndex_throwsIndexOutOfBoundsException() {
        TaskList taskList = new TaskList(new ArrayList<>(List.of(new Todo("task"))));

        assertThrows(IndexOutOfBoundsException.class, () -> taskList.delete(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> taskList.delete(1));
    }

    @Test
    void listAll_tasksArePresent_returnsNumberedTasksWithNewlines() {
        Task firstTask = new Todo("first task");
        Task secondTask = new Todo("second task");
        TaskList taskList = new TaskList(new ArrayList<>(List.of(firstTask, secondTask)));

        String expected = "1. " + firstTask + "\n"
                + "2. " + secondTask + "\n";

        assertEquals(expected, taskList.listAll());
    }

    @Test
    void getInternalList_taskListCreatedWithList_returnsSameList() {
        List<Task> tasks = new ArrayList<>();
        TaskList taskList = new TaskList(tasks);

        assertSame(tasks, taskList.getInternalList());
    }

    @Test
    void getInternalList_taskAddedToReturnedList_updatesTaskList() {
        TaskList taskList = new TaskList();
        Task task = new Todo("read book");

        taskList.getInternalList().add(task);

        assertEquals(1, taskList.size());
        assertSame(task, taskList.get(0));
    }
}
