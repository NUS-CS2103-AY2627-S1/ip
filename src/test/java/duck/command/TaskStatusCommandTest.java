package duck.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import duck.DuckException;
import duck.storage.Storage;
import duck.task.Task;
import duck.task.TaskList;
import duck.task.Todo;
import duck.ui.Ui;

/**
 * Tests shared task-status updates and rollback behavior through mark and unmark commands.
 */
public class TaskStatusCommandTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void execute_validMarkTaskNumber_marksAndPersistsTask()
            throws DuckException, IOException {
        Task task = new Todo("task");
        TaskList tasks = new TaskList(List.of(task));
        Path dataFile = this.temporaryDirectory.resolve("duck.txt");

        new MarkCommand(1).execute(tasks, new Ui(), new Storage(dataFile.toString()));

        assertTrue(task.isDone());
        assertEquals(List.of("T | 1 | task"),
                Files.readAllLines(dataFile, StandardCharsets.UTF_8));
    }

    @Test
    public void execute_validUnmarkTaskNumber_unmarksAndPersistsTask()
            throws DuckException, IOException {
        Task task = new Todo("task");
        task.markAsDone();
        TaskList tasks = new TaskList(List.of(task));
        Path dataFile = this.temporaryDirectory.resolve("duck.txt");

        new UnmarkCommand(1).execute(tasks, new Ui(), new Storage(dataFile.toString()));

        assertFalse(task.isDone());
        assertEquals(List.of("T | 0 | task"),
                Files.readAllLines(dataFile, StandardCharsets.UTF_8));
    }

    @Test
    public void execute_taskNumberOutsideList_throwsWithoutChangingStatus() {
        Task incompleteTask = new Todo("incomplete");
        Task completedTask = new Todo("completed");
        completedTask.markAsDone();
        TaskList tasks = new TaskList(List.of(incompleteTask, completedTask));
        Storage storage = new Storage(this.temporaryDirectory.resolve("duck.txt").toString());

        DuckException markException = assertThrows(DuckException.class, () ->
                new MarkCommand(0).execute(tasks, new Ui(), storage));
        DuckException unmarkException = assertThrows(DuckException.class, () ->
                new UnmarkCommand(3).execute(tasks, new Ui(), storage));

        String expectedMessage = "That task number does not exist. "
                + "Use list to check the available task numbers.";
        assertEquals(expectedMessage, markException.getMessage());
        assertEquals(expectedMessage, unmarkException.getMessage());
        assertFalse(incompleteTask.isDone());
        assertTrue(completedTask.isDone());
    }

    @Test
    public void execute_markAlreadyCompletedTask_throwsWithoutSaving() {
        Task task = new Todo("task");
        task.markAsDone();
        TaskList tasks = new TaskList(List.of(task));

        DuckException exception = assertThrows(DuckException.class, () ->
                new MarkCommand(1).execute(tasks, new Ui(), new FailingStorage()));

        assertEquals("Task 1 is already complete.", exception.getMessage());
        assertTrue(task.isDone());
    }

    @Test
    public void execute_unmarkAlreadyIncompleteTask_throwsWithoutSaving() {
        Task task = new Todo("task");
        TaskList tasks = new TaskList(List.of(task));

        DuckException exception = assertThrows(DuckException.class, () ->
                new UnmarkCommand(1).execute(tasks, new Ui(), new FailingStorage()));

        assertEquals("Task 1 is already active.", exception.getMessage());
        assertFalse(task.isDone());
    }

    @Test
    public void execute_markSaveFails_restoresIncompleteStatus() {
        Task task = new Todo("task");
        TaskList tasks = new TaskList(List.of(task));

        DuckException exception = assertThrows(DuckException.class, () ->
                new MarkCommand(1).execute(tasks, new Ui(), new FailingStorage()));

        assertEquals("Simulated save failure.", exception.getMessage());
        assertFalse(task.isDone());
    }

    @Test
    public void execute_unmarkSaveFails_restoresCompletedStatus() {
        Task task = new Todo("task");
        task.markAsDone();
        TaskList tasks = new TaskList(List.of(task));

        DuckException exception = assertThrows(DuckException.class, () ->
                new UnmarkCommand(1).execute(tasks, new Ui(), new FailingStorage()));

        assertEquals("Simulated save failure.", exception.getMessage());
        assertTrue(task.isDone());
    }
}
