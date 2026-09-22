package mono.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import mono.exception.NonExistentException;
import mono.testutil.StorageTestSupport;

/**
 * Tests task-list mutations, one-based IDs, and persistence.
 */
public class TaskListTest {
    @BeforeEach
    public void resetStorage() throws IOException {
        StorageTestSupport.resetStorage();
    }

    @Test
    public void addTask_task_addsReturnsAndPersistsTask() {
        TaskList taskList = new TaskList();
        Task task = new ToDo("read a book");

        Task addedTask = taskList.addTask(task);

        assertSame(task, addedTask);
        assertEquals(List.of(task), taskList.getTasks());
        assertEquals(
                List.of("T | 0 | read a book"),
                new TaskList().getTasks().stream().map(Task::toFileString).toList());
    }

    @Test
    public void delete_validOneBasedId_returnsRemovesAndPersistsTask()
            throws NonExistentException {
        TaskList taskList = new TaskList();
        Task firstTask = new ToDo("first task");
        Task secondTask = new ToDo("second task");
        taskList.addTask(firstTask);
        taskList.addTask(secondTask);

        Task deletedTask = taskList.delete(1);

        assertSame(firstTask, deletedTask);
        assertEquals(List.of(secondTask), taskList.getTasks());
        assertEquals(
                List.of("T | 0 | second task"),
                new TaskList().getTasks().stream().map(Task::toFileString).toList());
    }

    @Test
    public void delete_idBelowRange_throwsNonExistentExceptionWithoutChangingTasks() {
        TaskList taskList = taskListWithOneTask();

        NonExistentException exception = assertThrows(
                NonExistentException.class, () -> taskList.delete(0));

        assertEquals("Task 0 does not exist.", exception.getMessage());
        assertEquals(1, taskList.getTasks().size());
    }

    @Test
    public void delete_idAboveRange_throwsNonExistentExceptionWithoutChangingTasks() {
        TaskList taskList = taskListWithOneTask();

        NonExistentException exception = assertThrows(
                NonExistentException.class, () -> taskList.delete(2));

        assertEquals("Task 2 does not exist.", exception.getMessage());
        assertEquals(1, taskList.getTasks().size());
    }

    @Test
    public void markAndUnmarkTaskDone_validOneBasedId_updatesAndPersistsTask()
            throws NonExistentException {
        TaskList taskList = new TaskList();
        Task firstTask = taskList.addTask(new ToDo("first task"));
        Task secondTask = taskList.addTask(new ToDo("second task"));

        assertSame(secondTask, taskList.markTaskDone(2));
        assertEquals("T | 1 | second task", secondTask.toFileString());
        assertEquals("T | 0 | first task", firstTask.toFileString());
        assertEquals(
                "T | 1 | second task",
                new TaskList().getTasks().get(1).toFileString());

        assertSame(secondTask, taskList.unmarkTaskDone(2));
        assertEquals("T | 0 | second task", secondTask.toFileString());
        assertEquals(
                "T | 0 | second task",
                new TaskList().getTasks().get(1).toFileString());
    }

    @Test
    public void markAndUnmarkTaskDone_invalidIds_throwNonExistentException() {
        TaskList taskList = taskListWithOneTask();

        assertThrows(NonExistentException.class, () -> taskList.markTaskDone(0));
        assertThrows(NonExistentException.class, () -> taskList.markTaskDone(2));
        assertThrows(NonExistentException.class, () -> taskList.unmarkTaskDone(0));
        assertThrows(NonExistentException.class, () -> taskList.unmarkTaskDone(2));
    }

    @Test
    public void getTasks_returnedList_cannotModifyTaskList() {
        TaskList taskList = taskListWithOneTask();
        List<Task> tasks = taskList.getTasks();

        assertThrows(UnsupportedOperationException.class, () -> tasks.add(new ToDo("new task")));
        assertEquals(1, taskList.getTasks().size());
    }

    @Test
    public void findTasks_keywordInSomeTaskNames_returnsMatchesInListOrder() {
        TaskList taskList = new TaskList();
        Task firstMatch = taskList.addTask(new ToDo("read a book"));
        taskList.addTask(new ToDo("buy groceries"));
        Task secondMatch = taskList.addTask(new ToDo("reread lecture notes"));

        List<Task> matches = taskList.findTasks("read");

        assertEquals(List.of(firstMatch, secondMatch), matches);
    }

    @Test
    public void findTasks_keywordNotInAnyTaskName_returnsEmptyList() {
        TaskList taskList = taskListWithOneTask();

        assertEquals(List.of(), taskList.findTasks("missing"));
    }

    @Test
    public void findTasks_keywordWithDifferentCase_returnsOnlyExactCaseMatches() {
        TaskList taskList = new TaskList();
        Task uppercaseMatch = taskList.addTask(new ToDo("Read a book"));
        taskList.addTask(new ToDo("read lecture notes"));

        assertEquals(List.of(uppercaseMatch), taskList.findTasks("Read"));
    }

    @Test
    public void findTasks_emptyKeyword_returnsAllTasks() {
        TaskList taskList = new TaskList();
        Task firstTask = taskList.addTask(new ToDo("first task"));
        Task secondTask = taskList.addTask(new ToDo("second task"));

        assertEquals(List.of(firstTask, secondTask), taskList.findTasks(""));
    }

    @Test
    public void findTasks_returnedList_cannotModifyTaskList() {
        TaskList taskList = taskListWithOneTask();
        List<Task> matches = taskList.findTasks("task");

        assertThrows(UnsupportedOperationException.class, () -> matches.add(new ToDo("new task")));
        assertEquals(1, taskList.getTasks().size());
    }

    @Test
    public void findTasks_nullKeyword_throwsNullPointerException() {
        TaskList taskList = taskListWithOneTask();

        assertThrows(NullPointerException.class, () -> taskList.findTasks(null));
    }

    private TaskList taskListWithOneTask() {
        TaskList taskList = new TaskList();
        taskList.addTask(new ToDo("only task"));
        return taskList;
    }
}
