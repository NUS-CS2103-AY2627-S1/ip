package yawned.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

/** Tests task-list ordering, status updates, and list encapsulation. */
class TaskListTest {

    @Test
    void addGetAndDeleteTask_oneBasedNumbers_preserveTaskOrder() {
        TaskList tasks = new TaskList();
        Task firstTask = new ToDo("read book");
        Task secondTask = new ToDo("return book");
        Task thirdTask = new ToDo("buy bread");

        tasks.addTask(firstTask);
        tasks.addTask(secondTask);
        tasks.addTask(thirdTask);

        assertEquals(3, tasks.size());
        assertSame(secondTask, tasks.getTask(2));
        assertSame(secondTask, tasks.deleteTask(2));
        assertEquals(2, tasks.size());
        assertSame(firstTask, tasks.getTask(1));
        assertSame(thirdTask, tasks.getTask(2));
    }

    @Test
    void markAndUnmarkTask_changeOnlySelectedTaskStatus() {
        Task firstTask = new ToDo("read book");
        Task secondTask = new ToDo("return book");
        TaskList tasks = new TaskList(List.of(firstTask, secondTask));

        assertSame(secondTask, tasks.markTask(2));
        assertEquals(TaskStatus.NOT_DONE, firstTask.getStatus());
        assertEquals(TaskStatus.DONE, secondTask.getStatus());

        assertSame(secondTask, tasks.unmarkTask(2));
        assertEquals(TaskStatus.NOT_DONE, secondTask.getStatus());
    }

    @Test
    void taskList_constructorCopiesInputAndGetterIsReadOnly() {
        List<Task> initialTasks = new ArrayList<>();
        Task task = new ToDo("read book");
        initialTasks.add(task);
        TaskList tasks = new TaskList(initialTasks);

        initialTasks.clear();

        assertEquals(1, tasks.size());
        assertSame(task, tasks.getTask(1));
        assertThrows(UnsupportedOperationException.class, () -> tasks.getTasks().add(new ToDo("buy bread")));
    }

    @Test
    void emptyTaskList_reportsEmptyAndRejectsInvalidTaskNumbers() {
        TaskList tasks = new TaskList();

        assertTrue(tasks.isEmpty());
        assertEquals(0, tasks.size());
        assertThrows(IndexOutOfBoundsException.class, () -> tasks.getTask(0));
        assertThrows(IndexOutOfBoundsException.class, () -> tasks.deleteTask(1));
        assertFalse(tasks.getTasks().stream().findAny().isPresent());
    }

    @Test
    void findTasks_matchingAndAbsentKeyword_returnsMatchingTasksInListOrder() {
        Task firstTask = new ToDo("read book");
        Task secondTask = new ToDo("buy bread");
        Task thirdTask = new ToDo("return book");
        TaskList tasks = new TaskList(List.of(firstTask, secondTask, thirdTask));

        assertEquals(List.of(firstTask, thirdTask), tasks.findTasks("book"));
        assertTrue(tasks.findTasks("recipe").isEmpty());
    }
}
