package koko;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests the task management operations in TaskList.
 */
public class TaskListTest {

    /**
     * Tests that adding a task increases the size of the task list
     * and stores the task correctly.
     */
    @Test
    void add_newTask_taskIsStored() {
        TaskList taskList = new TaskList();
        Todo todo = new Todo("read book");

        taskList.add(todo);

        assertEquals(1, taskList.size());
        assertSame(todo, taskList.get(0));
    }

    /**
     * Tests that removing a task decreases the size and returns
     * the correct task.
     */
    @Test
    void remove_existingTask_taskIsReturnedAndRemoved() {
        TaskList taskList = new TaskList();
        Todo todo = new Todo("read book");

        taskList.add(todo);

        Task removedTask = taskList.remove(0);

        assertSame(todo, removedTask);
        assertEquals(0, taskList.size());
    }

    /**
     * Tests that multiple tasks retain their insertion order.
     */
    @Test
    void add_multipleTasks_tasksRemainInInsertionOrder() {
        TaskList taskList = new TaskList();
        Todo first = new Todo("first task");
        Todo second = new Todo("second task");

        taskList.add(first);
        taskList.add(second);

        assertEquals(2, taskList.size());
        assertSame(first, taskList.get(0));
        assertSame(second, taskList.get(1));
    }

    /**
     * Tests that finding tasks returns every matching task in insertion order.
     */
    @Test
    void find_matchingDescriptions_returnsMatchingTasksInInsertionOrder() {
        TaskList taskList = new TaskList();
        Todo firstMatch = new Todo("borrow book");
        Todo nonMatch = new Todo("buy groceries");
        Todo secondMatch = new Todo("return book");

        taskList.add(firstMatch);
        taskList.add(nonMatch);
        taskList.add(secondMatch);

        assertEquals(List.of(firstMatch, secondMatch), taskList.find("book"));
    }

    /**
     * Tests that replacing a task retains its position in the list.
     */
    @Test
    void set_existingTask_replacesTaskAtSameIndex() {
        TaskList taskList = new TaskList();
        Todo original = new Todo("read book");
        Todo replacement = new Todo("read chapter 3");
        taskList.add(original);

        taskList.set(0, replacement);

        assertEquals(1, taskList.size());
        assertSame(replacement, taskList.get(0));
    }
}
