package yuno.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import yuno.exception.FileStorageException;
import yuno.exception.InvalidTaskNumberException;
import yuno.storage.Storage;
import yuno.task.Task;
import yuno.task.TaskList;
import yuno.task.Todo;

class TransactionalCommandTest extends CommandTestSupport {
    @Test
    void execute_addCommandsWhenSaveFails_leaveTaskListUnchanged() throws FileStorageException {
        TaskList originalTasks = createTaskListWithTodo(false);

        assertSaveFailureDoesNotChangeTasks(new TodoCommand("new todo"), originalTasks);
        assertSaveFailureDoesNotChangeTasks(
                new DeadlineCommand("new deadline /by 2026-09-15"), originalTasks);
        assertSaveFailureDoesNotChangeTasks(
                new EventCommand("new event /from 2026-09-15 1000 /to 2026-09-15 1100"),
                originalTasks);
    }

    @Test
    void execute_managementCommandsWhenSaveFails_leaveTaskListUnchanged()
            throws FileStorageException {
        assertSaveFailureDoesNotChangeTasks(
                new MarkCommand("1"), createTaskListWithTodo(false));
        assertSaveFailureDoesNotChangeTasks(
                new UnmarkCommand("1"), createTaskListWithTodo(true));
        assertSaveFailureDoesNotChangeTasks(
                new DeleteCommand("1"), createTaskListWithTodo(false));
        assertSaveFailureDoesNotChangeTasks(
                new ClearCommand(""), createTaskListWithTodo(false));

        TaskList unsortedTasks = new TaskList();
        unsortedTasks.addTask("undated task");
        unsortedTasks.addTask("deadline", LocalDateTime.of(2026, 9, 15, 12, 0));
        assertSaveFailureDoesNotChangeTasks(new SortCommand(""), unsortedTasks);
    }

    private TaskList createTaskListWithTodo(boolean isDone) {
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo("existing task", isDone));
        return tasks;
    }

    private void assertSaveFailureDoesNotChangeTasks(Command command, TaskList tasks)
            throws FileStorageException {
        List<String> originalTaskData = getTaskData(tasks);
        List<Task> originalTasks = getTasks(tasks);
        Storage failingStorage = new Storage(tempDir.resolve("failing-tasks.txt")) {
            @Override
            public void save(TaskList taskList) throws FileStorageException {
                throw new FileStorageException("Simulated save failure");
            }
        };

        FileStorageException exception = assertThrows(
                FileStorageException.class, () -> command.execute(tasks, ui, failingStorage));

        assertEquals("Simulated save failure", exception.getMessage());
        assertEquals(originalTaskData, getTaskData(tasks));
        for (int i = 0; i < originalTasks.size(); i++) {
            assertSame(originalTasks.get(i), getTasks(tasks).get(i));
        }
        assertNull(ui.getAddedTask());
        assertNull(ui.getMarkedTask());
        assertNull(ui.getUnmarkedTask());
        assertNull(ui.getDeletedTask());
        assertNull(ui.getDisplayedTaskList());
        assertFalse(ui.areTasksClearedPrinted());
    }

    private List<Task> getTasks(TaskList tasks) {
        List<Task> taskObjects = new ArrayList<>();
        for (int taskNumber = 1; taskNumber <= tasks.getCount(); taskNumber++) {
            try {
                taskObjects.add(tasks.getTask(taskNumber));
            } catch (InvalidTaskNumberException exception) {
                throw new AssertionError("Valid task number should retrieve a task", exception);
            }
        }
        return taskObjects;
    }

    private List<String> getTaskData(TaskList tasks) {
        List<String> taskData = new ArrayList<>();
        for (int taskNumber = 1; taskNumber <= tasks.getCount(); taskNumber++) {
            try {
                taskData.add(tasks.getTask(taskNumber).toStorageString());
            } catch (InvalidTaskNumberException exception) {
                throw new AssertionError("Valid task number should retrieve a task", exception);
            }
        }
        return taskData;
    }
}
