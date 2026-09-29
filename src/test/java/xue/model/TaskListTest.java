package xue.model;

import org.junit.jupiter.api.Test;
import xue.XueException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** Tests the core collection and task-limit behavior of {@link TaskList}. */
class TaskListTest {

    @Test
    void newTaskList_isEmpty() {
        TaskList taskList = new TaskList();

        assertEquals(0, taskList.size());
        assertEquals(List.of(), taskList.asList());
    }

    @Test
    void taskList_savedTasks_copiesInitialOrder() {
        Task first = new Task("first");
        Task second = new Task("second");
        List<Task> savedTasks = new ArrayList<>(List.of(first, second));
        TaskList taskList = new TaskList(savedTasks);

        savedTasks.clear();

        assertEquals(2, taskList.size());
        assertSame(first, taskList.get(0));
        assertSame(second, taskList.get(1));
    }

    @Test
    void add_tasks_keepsInsertionOrderAndReturnsThemByIndex() {
        TaskList taskList = new TaskList();
        Task first = new Task("first");
        Task second = new Task("second");

        taskList.add(first);
        taskList.add(second);

        assertEquals(2, taskList.size());
        assertSame(first, taskList.get(0));
        assertSame(second, taskList.get(1));
    }

    @Test
    void delete_validIndex_removesAndReturnsSelectedTask() {
        TaskList taskList = new TaskList(List.of(new Task("first"), new Task("second")));

        Task deleted = taskList.delete(0);

        assertEquals("first", deleted.getDescription());
        assertEquals(1, taskList.size());
        assertEquals("second", taskList.get(0).getDescription());
    }

    @Test
    void asList_mutatingReturnedList_doesNotChangeTaskList() {
        TaskList taskList = new TaskList(List.of(new Task("first")));
        List<Task> copy = taskList.asList();

        copy.clear();

        assertEquals(1, taskList.size());
    }

    @Test
    void add_moreThanMaximumTasks_throwsAndKeepsMaximumSize() {
        TaskList taskList = new TaskList();
        for (int i = 0; i < 100; i++) {
            taskList.add(new Task("task " + i));
        }

        assertThrows(XueException.class, () -> taskList.add(new Task("one too many")));
        assertEquals(100, taskList.size());
    }

    @Test
    void get_invalidIndex_throwsIndexOutOfBoundsException() {
        TaskList taskList = new TaskList();

        assertThrows(IndexOutOfBoundsException.class, () -> taskList.get(0));
    }

    @Test
    void delete_invalidIndex_throwsIndexOutOfBoundsException() {
        TaskList taskList = new TaskList(List.of(new Task("only task")));

        assertThrows(IndexOutOfBoundsException.class, () -> taskList.delete(1));
    }
}
