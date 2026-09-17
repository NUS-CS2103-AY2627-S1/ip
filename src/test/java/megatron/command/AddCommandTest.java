package megatron.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import megatron.exception.MegatronException;
import megatron.exception.StorageException;
import megatron.exception.TaskListFullException;
import megatron.exception.UnknownCommandException;
import megatron.storage.TaskStorage;
import megatron.task.Task;
import megatron.task.TaskList;
import megatron.task.Todo;
import megatron.ui.Ui;

/**
 * Tests task validation, addition, persistence, and display by {@link AddCommand}.
 */
class AddCommandTest {
    private static final int MAX_TASKS = 100;

    @TempDir
    private Path tempDirectory;

    @Test
    void execute_validTodo_addsSavesAndDisplaysTask() throws MegatronException {
        TaskList tasks = new TaskList();
        Path storageFile = tempDirectory.resolve("tasks.csv");
        TaskStorage storage = new TaskStorage(storageFile.toString());
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Ui ui = createUi(output);

        new AddCommand("todo buy milk", new Parser()).execute(tasks, ui, storage);

        Todo addedTask = assertInstanceOf(Todo.class, tasks.getTask(1));
        TaskList savedTasks = storage.load();
        assertEquals(1, tasks.size());
        assertEquals("buy milk", addedTask.getDescription());
        assertEquals(1, savedTasks.size());
        assertEquals("buy milk", savedTasks.getTask(1).getDescription());
        assertEquals(expectedAddedOutput("[T][ ] buy milk", 1), output.toString(StandardCharsets.UTF_8));
    }

    @Test
    void execute_listWith99Tasks_adds100thTask() throws MegatronException {
        TaskList tasks = createTaskList(99);
        Path storageFile = tempDirectory.resolve("tasks.csv");
        TaskStorage storage = new TaskStorage(storageFile.toString());
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        new AddCommand("todo final task", new Parser()).execute(tasks, createUi(output), storage);

        assertEquals(MAX_TASKS, tasks.size());
        assertEquals("final task", tasks.getTask(MAX_TASKS).getDescription());
        assertEquals(MAX_TASKS, storage.load().size());
        assertTrue(output.toString(StandardCharsets.UTF_8).contains("My army now contains 100 tasks."));
    }

    @Test
    void execute_fullList_throwsWithoutChangingState() {
        TaskList tasks = createTaskList(MAX_TASKS);
        Path storageFile = tempDirectory.resolve("tasks.csv");
        TaskStorage storage = new TaskStorage(storageFile.toString());
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        assertThrows(TaskListFullException.class, () -> new AddCommand("todo extra task", new Parser())
                        .execute(tasks, createUi(output), storage));

        assertEquals(MAX_TASKS, tasks.size());
        assertFalse(Files.exists(storageFile));
        assertEquals("", output.toString(StandardCharsets.UTF_8));
    }

    @Test
    void execute_unknownTaskType_throwsWithoutChangingState() {
        TaskList tasks = new TaskList();
        Path storageFile = tempDirectory.resolve("tasks.csv");
        TaskStorage storage = new TaskStorage(storageFile.toString());
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        assertThrows(UnknownCommandException.class, () -> new AddCommand("reminder buy milk", new Parser())
                        .execute(tasks, createUi(output), storage));

        assertEquals(0, tasks.size());
        assertFalse(Files.exists(storageFile));
        assertEquals("", output.toString(StandardCharsets.UTF_8));
    }

    @Test
    void execute_saveFailure_rollsBackAddedTask() {
        TaskList tasks = new TaskList();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        TaskStorage storage = new TaskStorage(tempDirectory.toString());

        assertThrows(StorageException.class, () -> new AddCommand("todo unsaved task", new Parser())
                .execute(tasks, createUi(output), storage));

        assertEquals(0, tasks.size());
        assertEquals("", output.toString(StandardCharsets.UTF_8));
    }

    @Test
    void constructor_nullInput_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new AddCommand(null, new Parser()));
    }

    @Test
    void constructor_nullParser_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new AddCommand("todo buy milk", null));
    }

    private static TaskList createTaskList(int size) {
        List<Task> tasks = new ArrayList<>();
        for (int taskNumber = 1; taskNumber <= size; taskNumber++) {
            tasks.add(new Todo("task " + taskNumber));
        }
        return new TaskList(tasks);
    }

    private static Ui createUi(ByteArrayOutputStream output) {
        return new Ui(new Scanner(""), new PrintStream(output, true, StandardCharsets.UTF_8));
    }

    private static String expectedAddedOutput(String taskText, int taskCount) {
        return "Command accepted! This task has joined my army:" + System.lineSeparator()
                + "" + taskText + System.lineSeparator()
                + "My army now contains " + taskCount + " tasks." + System.lineSeparator();
    }
}
