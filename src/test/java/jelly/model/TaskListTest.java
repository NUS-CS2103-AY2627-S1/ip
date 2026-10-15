package jelly.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

/** Tests task storage and list operations performed by {@link TaskList}. */
class TaskListTest {

    @Test
    void constructors_rejectInvalidTaskData() {
        assertThrows(IllegalArgumentException.class, () -> new Todo(" "));
        assertThrows(IllegalArgumentException.class, () -> new Todo("bad | data"));
        assertThrows(IllegalArgumentException.class, () -> new Deadline("task", null));
        assertThrows(IllegalArgumentException.class, () -> new Event("task",
                LocalDateTime.of(2026, 1, 1, 10, 0), LocalDateTime.of(2026, 1, 1, 10, 0)));
    }

    @Test
    void addTask_taskIsAddedAndSizeIncreases() {
        TaskList taskList = new TaskList();
        Task task = new Todo("buy groceries");

        taskList.addTask(task);

        assertEquals(1, taskList.size());
        assertEquals(task, taskList.getTask(0));
    }

    @Test
    void deleteTask_taskIsRemovedAndReturned() {
        Task firstTask = new Todo("buy groceries");
        Task secondTask = new Todo("read book");
        TaskList taskList = new TaskList(List.of(firstTask, secondTask));

        Task deletedTask = taskList.deleteTask(0);

        assertEquals(firstTask, deletedTask);
        assertEquals(1, taskList.size());
        assertEquals(secondTask, taskList.getTask(0));
    }

    @Test
    void asList_returnedListCannotBeModified() {
        TaskList taskList = new TaskList();
        taskList.addTask(new Todo("buy groceries"));

        List<Task> tasks = taskList.asList();

        assertThrows(UnsupportedOperationException.class, () -> tasks.add(new Todo("read book")));
        assertEquals(1, taskList.size());
    }

    @Test
    void getTask_invalidIndex_throwsException() {
        TaskList taskList = new TaskList();

        assertThrows(IndexOutOfBoundsException.class, () -> taskList.getTask(0));
    }

    @Test
    void deleteTask_invalidIndex_throwsException() {
        TaskList taskList = new TaskList();

        assertThrows(IndexOutOfBoundsException.class, () -> taskList.deleteTask(0));
    }

    @Test
    void findMatchingTaskNumbers_returnsOneBasedMatchingPositions() {
        TaskList taskList = new TaskList(List.of(
                new Todo("buy groceries"),
                new Todo("read book"),
                new Todo("buy a book")));

        assertEquals(List.of(1, 3), taskList.findMatchingTaskNumbers("BUY"));
    }
}
