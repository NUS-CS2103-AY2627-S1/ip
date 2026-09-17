package megatron.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import megatron.exception.MegatronException;
import megatron.exception.StorageException;
import megatron.exception.TaskNotFoundException;
import megatron.storage.TaskStorage;
import megatron.task.TaskList;
import megatron.task.Todo;
import megatron.ui.Ui;

/**
 * Tests task selection, persistence, and display by {@link UnmarkCommand}.
 */
class UnmarkCommandTest {
    @TempDir
    private Path tempDirectory;

    @Test
    void execute_completedTask_unmarksSelectedTaskSavesAndDisplaysIt() throws MegatronException {
        Todo firstTask = new Todo("first");
        firstTask.markAsDone();
        Todo secondTask = new Todo("second");
        secondTask.markAsDone();
        TaskList tasks = new TaskList(List.of(firstTask, secondTask));
        Path storageFile = tempDirectory.resolve("tasks.csv");
        TaskStorage storage = new TaskStorage(storageFile.toString());
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        new UnmarkCommand(2).execute(tasks, createUi(output), storage);

        TaskList savedTasks = storage.load();
        assertTrue(tasks.getTask(1).isDone());
        assertFalse(tasks.getTask(2).isDone());
        assertTrue(savedTasks.getTask(1).isDone());
        assertFalse(savedTasks.getTask(2).isDone());
        assertEquals(expectedUnmarkedOutput("[T][ ] second"), output.toString(StandardCharsets.UTF_8));
    }

    @Test
    void execute_alreadyNotDoneTask_remainsNotDoneAndDisplaysIt() throws MegatronException {
        TaskList tasks = new TaskList(List.of(new Todo("already not done")));
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        new UnmarkCommand(1).execute(tasks, createUi(output),
                new TaskStorage(tempDirectory.resolve("tasks.csv").toString()));

        assertFalse(tasks.getTask(1).isDone());
        assertEquals(expectedUnmarkedOutput("[T][ ] already not done"),
                output.toString(StandardCharsets.UTF_8));
    }

    @Test
    void execute_zeroTaskNumber_throwsWithoutSavingOrDisplaying() throws MegatronException {
        assertInvalidTaskNumber(0);
    }

    @Test
    void execute_negativeTaskNumber_throwsWithoutSavingOrDisplaying() throws MegatronException {
        assertInvalidTaskNumber(-1);
    }

    @Test
    void execute_taskNumberAboveSize_throwsWithoutSavingOrDisplaying() throws MegatronException {
        assertInvalidTaskNumber(2);
    }

    @Test
    void execute_saveFailure_restoresPreviousStatus() {
        Todo task = new Todo("task");
        task.markAsDone();
        TaskList tasks = new TaskList(List.of(task));
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        TaskStorage storage = new TaskStorage(tempDirectory.toString());

        assertThrows(StorageException.class, () -> new UnmarkCommand(1)
                .execute(tasks, createUi(output), storage));

        assertTrue(task.isDone());
        assertEquals("", output.toString(StandardCharsets.UTF_8));
    }

    @Test
    void execute_saveFailureAlreadyNotDone_keepsItNotDone() {
        Todo task = new Todo("already not done");
        TaskList tasks = new TaskList(List.of(task));
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        TaskStorage storage = new TaskStorage(tempDirectory.toString());

        assertThrows(StorageException.class, () -> new UnmarkCommand(1)
                .execute(tasks, createUi(output), storage));

        assertFalse(task.isDone());
        assertEquals("", output.toString(StandardCharsets.UTF_8));
    }

    private void assertInvalidTaskNumber(int taskNumber) throws MegatronException {
        TaskList tasks = new TaskList(List.of(new Todo("task")));
        Path storageFile = tempDirectory.resolve("tasks.csv");
        TaskStorage storage = new TaskStorage(storageFile.toString());
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        assertThrows(TaskNotFoundException.class, () -> new UnmarkCommand(taskNumber)
                .execute(tasks, createUi(output), storage));

        assertFalse(tasks.getTask(1).isDone());
        assertFalse(Files.exists(storageFile));
        assertEquals("", output.toString(StandardCharsets.UTF_8));
    }

    private static Ui createUi(ByteArrayOutputStream output) {
        return new Ui(new Scanner(""), new PrintStream(output, true, StandardCharsets.UTF_8));
    }

    private static String expectedUnmarkedOutput(String taskText) {
        return "Rebellion successful. This task escaped completion:" + System.lineSeparator()
                + "" + taskText + System.lineSeparator();
    }
}
