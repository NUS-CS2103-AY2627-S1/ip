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
 * Tests task selection, persistence, and display by {@link MarkCommand}.
 */
class MarkCommandTest {
    @TempDir
    private Path tempDirectory;

    @Test
    void execute_validTask_marksSelectedTaskSavesAndDisplaysIt() throws MegatronException {
        TaskList tasks = new TaskList(List.of(new Todo("first"), new Todo("second")));
        Path storageFile = tempDirectory.resolve("tasks.csv");
        TaskStorage storage = new TaskStorage(storageFile.toString());
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        new MarkCommand(2).execute(tasks, createUi(output), storage);

        TaskList savedTasks = storage.load();
        assertFalse(tasks.getTask(1).isDone());
        assertTrue(tasks.getTask(2).isDone());
        assertFalse(savedTasks.getTask(1).isDone());
        assertTrue(savedTasks.getTask(2).isDone());
        assertEquals(expectedMarkedOutput("[T][X] second"), output.toString(StandardCharsets.UTF_8));
    }

    @Test
    void execute_alreadyDoneTask_remainsDoneAndDisplaysIt() throws MegatronException {
        Todo task = new Todo("already done");
        task.markAsDone();
        TaskList tasks = new TaskList(List.of(task));
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        new MarkCommand(1).execute(tasks, createUi(output),
                new TaskStorage(tempDirectory.resolve("tasks.csv").toString()));

        assertTrue(task.isDone());
        assertEquals(expectedMarkedOutput("[T][X] already done"), output.toString(StandardCharsets.UTF_8));
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
        TaskList tasks = new TaskList(List.of(task));
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        TaskStorage storage = new TaskStorage(tempDirectory.toString());

        assertThrows(StorageException.class, () -> new MarkCommand(1)
                .execute(tasks, createUi(output), storage));

        assertFalse(task.isDone());
        assertEquals("", output.toString(StandardCharsets.UTF_8));
    }

    @Test
    void execute_saveFailureAlreadyDone_keepsItDone() {
        Todo task = new Todo("already done");
        task.markAsDone();
        TaskList tasks = new TaskList(List.of(task));
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        TaskStorage storage = new TaskStorage(tempDirectory.toString());

        assertThrows(StorageException.class, () -> new MarkCommand(1)
                .execute(tasks, createUi(output), storage));

        assertTrue(task.isDone());
        assertEquals("", output.toString(StandardCharsets.UTF_8));
    }

    private void assertInvalidTaskNumber(int taskNumber) throws MegatronException {
        TaskList tasks = new TaskList(List.of(new Todo("task")));
        Path storageFile = tempDirectory.resolve("tasks.csv");
        TaskStorage storage = new TaskStorage(storageFile.toString());
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        assertThrows(TaskNotFoundException.class, () -> new MarkCommand(taskNumber)
                .execute(tasks, createUi(output), storage));

        assertFalse(tasks.getTask(1).isDone());
        assertFalse(Files.exists(storageFile));
        assertEquals("", output.toString(StandardCharsets.UTF_8));
    }

    private static Ui createUi(ByteArrayOutputStream output) {
        return new Ui(new Scanner(""), new PrintStream(output, true, StandardCharsets.UTF_8));
    }

    private static String expectedMarkedOutput(String taskText) {
        return "Victory! This task has fallen before my mighty intellect:"
                + System.lineSeparator()
                + "" + taskText + System.lineSeparator();
    }
}
