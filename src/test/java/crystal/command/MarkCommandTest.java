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
 * Tests marking tasks, no-op handling, persistence, and index validation.
 */
public class MarkCommandTest extends CommandTestBase {
    /** Verifies marking, persistence, and user feedback for an incomplete task. */
    @Test
    public void execute_incompleteTask_marksPersistsAndDisplaysTask() throws Exception {
        Todo todo = new Todo("read book");
        TaskList tasks = new TaskList(List.of(todo));

        new MarkCommand(0).execute(tasks, createUi(), createStorage());

        assertTrue(todo.isDone());
        Path dataFile = tempDirectory.resolve("data").resolve("crystal.txt");
        assertEquals(List.of("T | 1 | read book"),
                Files.readAllLines(dataFile, StandardCharsets.UTF_8));
        assertEquals("Crystal: Yayyy!!! You finished this task:\n"
                + "         [T][X] read book\n", getOutput());
    }

    /** Verifies that marking an already completed task does not save again. */
    @Test
    public void execute_alreadyCompletedTask_displaysNoOpWithoutSaving() throws Exception {
        Todo todo = new Todo("read book");
        todo.markAsDone();
        TaskList tasks = new TaskList(List.of(todo));
        Path directoryAsFile = tempDirectory.resolve("crystal.txt");
        Files.createDirectory(directoryAsFile);

        assertDoesNotThrow(() -> new MarkCommand(0).execute(
                tasks, createUi(), new Storage(directoryAsFile)));

        assertEquals("Crystal: This task is already done, good job!\n"
                + "         [T][X] read book\n", getOutput());
    }

    /** Verifies that marking rejects an out-of-range task index. */
    @Test
    public void execute_indexOutsideTaskList_throwsCrystalException() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        CrystalException exception = assertThrows(CrystalException.class, () ->
                new MarkCommand(1).execute(tasks, createUi(), createStorage()));

        assertEquals("That task number does not exist!", exception.getMessage());
    }

    /** Verifies that a failed save restores the task's incomplete state. */
    @Test
    public void execute_storageFailure_restoresIncompleteStatus() throws Exception {
        Todo todo = new Todo("read book");
        TaskList tasks = new TaskList(List.of(todo));
        Path directoryAsFile = tempDirectory.resolve("crystal.txt");
        Files.createDirectory(directoryAsFile);

        CrystalException exception = assertThrows(CrystalException.class, () ->
                new MarkCommand(0).execute(
                        tasks, createUi(), new Storage(directoryAsFile)));

        assertEquals("I couldn't save your tasks to the hard disk.", exception.getMessage());
        assertFalse(todo.isDone());
    }
}
