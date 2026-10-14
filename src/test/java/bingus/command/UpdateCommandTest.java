package bingus.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import bingus.exception.BingusException;
import bingus.storage.Storage;
import bingus.task.Task;
import bingus.task.TaskList;
import bingus.task.Todo;
import bingus.ui.Ui;

/**
 * Tests the state changes performed by {@link UpdateCommand}.
 */
class UpdateCommandTest {
    /** Directory created by JUnit for temporary test data. */
    @TempDir
    Path temporaryDirectory;

    /**
     * Verifies that updating replaces and persists the task without changing its completion status.
     */
    @Test
    void execute_markedTask_replacesAndPersistsTaskWithMarkedStatus() {
        Storage storage = new Storage(temporaryDirectory.resolve("data/tasks.txt").toString());
        Todo originalTask = new Todo("read book");
        originalTask.mark();
        Todo updatedTask = new Todo("read two chapters");
        TaskList tasks = new TaskList(List.of(originalTask));

        String response = new UpdateCommand(1, updatedTask).execute(tasks, new Ui(), storage);

        assertSame(updatedTask, tasks.get(0));
        assertTrue(updatedTask.isDone());
        assertEquals("Got it! I updated this task:\n[T][✓] read two chapters", response);
        List<Task> savedTasks = storage.loadTasks();
        assertEquals("read two chapters", savedTasks.get(0).getDescription());
        assertTrue(savedTasks.get(0).isDone());
    }

    /**
     * Verifies that an update is undone in memory when the modified task list cannot be saved.
     */
    @Test
    void execute_saveFails_restoresOriginalTask() {
        Storage failingStorage = new Storage(temporaryDirectory.resolve("data/tasks.txt").toString()) {
            @Override
            public void saveTasks(List<Task> tasks) throws BingusException {
                throw new BingusException("Test save failure.");
            }
        };
        Todo originalTask = new Todo("read book");
        Todo updatedTask = new Todo("read two chapters");
        TaskList tasks = new TaskList(List.of(originalTask));
        UpdateCommand command = new UpdateCommand(1, updatedTask);

        assertThrows(BingusException.class, () -> command.execute(tasks, new Ui(), failingStorage));

        assertSame(originalTask, tasks.get(0));
        assertEquals(1, tasks.size());
    }

    /**
     * Verifies that a failed update restores the original task and its completed status.
     */
    @Test
    void execute_saveFailsForMarkedTask_restoresOriginalMarkedTask() {
        Storage failingStorage = new Storage(temporaryDirectory.resolve("data/tasks.txt").toString()) {
            @Override
            public void saveTasks(List<Task> tasks) throws BingusException {
                throw new BingusException("Test save failure.");
            }
        };
        Todo originalTask = new Todo("read book");
        originalTask.mark();
        Todo updatedTask = new Todo("read two chapters");
        TaskList tasks = new TaskList(List.of(originalTask));

        assertThrows(
                BingusException.class, () -> new UpdateCommand(1, updatedTask)
                        .execute(tasks, new Ui(), failingStorage));

        assertSame(originalTask, tasks.get(0));
        assertTrue(originalTask.isDone());
    }
}
