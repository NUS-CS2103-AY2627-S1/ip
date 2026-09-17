package crystal.command;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;

import crystal.exception.CrystalException;
import crystal.storage.Storage;
import crystal.task.TaskList;
import crystal.task.Todo;

/**
 * Tests unmarking tasks, no-op handling, persistence, and index validation.
 */
public class UnmarkCommandTest extends CommandTestBase {
    /** Verifies unmarking, persistence, and feedback for a completed task. */
    @Test
    public void execute_completedTask_unmarksPersistsAndDisplaysTask() throws Exception {
        Todo todo = new Todo("read book");
        todo.markAsDone();
        TaskList tasks = new TaskList(List.of(todo));

        new UnmarkCommand(0).execute(tasks, createUi(), createStorage());

        assertFalse(todo.isDone());
        Path dataFile = tempDirectory.resolve("data").resolve("crystal.txt");
        assertEquals(List.of("T | 0 | read book"),
                Files.readAllLines(dataFile, StandardCharsets.UTF_8));
        assertEquals("Crystal: Okkk!!! I've put this task back on your list:\n"
                + "         [T][ ] read book\n", getOutput());
    }

    /** Verifies that unmarking an incomplete task does not save again. */
    @Test
    public void execute_alreadyIncompleteTask_displaysNoOpWithoutSaving() throws Exception {
        Todo todo = new Todo("read book");
        TaskList tasks = new TaskList(List.of(todo));
        Path directoryAsFile = tempDirectory.resolve("crystal.txt");
        Files.createDirectory(directoryAsFile);

        assertDoesNotThrow(() -> new UnmarkCommand(0).execute(
                tasks, createUi(), new Storage(directoryAsFile)));

        assertEquals("Crystal: This task is already waiting for you!\n"
                + "         [T][ ] read book\n", getOutput());
    }

    /** Verifies that unmarking rejects a negative task index. */
    @Test
    public void execute_indexOutsideTaskList_throwsCrystalException() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        CrystalException exception = assertThrows(CrystalException.class, () ->
                new UnmarkCommand(-1).execute(tasks, createUi(), createStorage()));

        assertEquals("That task number does not exist!", exception.getMessage());
    }

    /** Verifies that a failed save restores the task's completed state. */
    @Test
    public void execute_storageFailure_restoresCompletedStatus() throws Exception {
        Todo todo = new Todo("read book");
        todo.markAsDone();
        TaskList tasks = new TaskList(List.of(todo));
        Path directoryAsFile = tempDirectory.resolve("crystal.txt");
        Files.createDirectory(directoryAsFile);

        CrystalException exception = assertThrows(CrystalException.class, () ->
                new UnmarkCommand(0).execute(
                        tasks, createUi(), new Storage(directoryAsFile)));

        assertEquals("I couldn't save your tasks to the hard disk.", exception.getMessage());
        assertTrue(todo.isDone());
    }
}
