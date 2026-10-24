package kiki.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Comparator;

import org.junit.jupiter.api.Test;

import kiki.exception.KikiException;

public class TaskListTest {

    @Test
    public void add_singleTask_sizeIncreasesAndTaskStored() {
        TaskList taskList = new TaskList();
        Task task = new ToDos("read book");

        taskList.add(task);

        assertEquals(1, taskList.size());
        assertSame(task, taskList.get(0));
    }

    @Test
    public void remove_middleTask_shiftsLaterTasksUp() {
        TaskList taskList = new TaskList();
        Task first = new ToDos("first");
        Task second = new ToDos("second");
        Task third = new ToDos("third");
        taskList.add(first);
        taskList.add(second);
        taskList.add(third);

        Task removed = taskList.remove(1);

        assertSame(second, removed);
        assertEquals(2, taskList.size());
        assertSame(first, taskList.get(0));
        assertSame(third, taskList.get(1));
    }

    @Test
    public void remove_lastTask_sizeDecreases() {
        TaskList taskList = new TaskList();
        Task only = new ToDos("only task");
        taskList.add(only);

        Task removed = taskList.remove(0);

        assertSame(only, removed);
        assertEquals(0, taskList.size());
    }

    @Test
    public void sort_threeTasks_reordersTasks() {
        TaskList taskList = new TaskList();
        Task first = new ToDos("first");
        Task second = new ToDos("second");
        Task third = new ToDos("third");
        taskList.add(first);
        taskList.add(second);
        taskList.add(third);

        taskList.sort(Comparator.comparing(Task::getDescription).reversed());

        assertSame(third, taskList.get(0));
        assertSame(second, taskList.get(1));
        assertSame(first, taskList.get(2));
    }

    @Test
    public void size_emptyList_returnsZero() {
        TaskList taskList = new TaskList();

        assertEquals(0, taskList.size());
    }

    @Test
    public void isFull_belowCapacity_returnsFalse() {
        TaskList taskList = new TaskList();
        taskList.add(new ToDos("read book"));

        assertFalse(taskList.isFull());
    }

    @Test
    public void isFull_atCapacity_returnsTrue() {
        TaskList taskList = new TaskList();

        for (int i = 0; i < taskList.getCapacity(); i++) {
            taskList.add(new ToDos("task " + i));
        }

        assertTrue(taskList.isFull());
    }

    @Test
    public void ensureCanAdd_belowCapacity_doesNotThrow() throws KikiException {
        TaskList taskList = new TaskList();

        taskList.ensureCanAdd();
    }

    @Test
    public void ensureCanAdd_atCapacity_exceptionThrown() {
        TaskList taskList = new TaskList();

        for (int i = 0; i < taskList.getCapacity(); i++) {
            taskList.add(new ToDos("task " + i));
        }

        KikiException exception = assertThrows(KikiException.class, taskList::ensureCanAdd);
        assertEquals("Your task list is full.", exception.getMessage());
    }
}
