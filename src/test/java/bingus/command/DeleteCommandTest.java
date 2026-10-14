package bingus.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
 * Tests the state changes performed by {@link DeleteCommand}.
 */
class DeleteCommandTest {
    /** Directory created by JUnit for temporary test data. */
    @TempDir
    Path temporaryDirectory;

    /**
     * Verifies that deleting a one-based task number keeps the remaining tasks in order and saves them.
     */
    @Test
    void execute_deletesSelectedTaskAndPersistsRemainingTaskOrder() {
        Storage storage = new Storage(temporaryDirectory.resolve("data/tasks.txt").toString());
        TaskList tasks = new TaskList();
        tasks.add(new Todo("first"));
        tasks.add(new Todo("second"));
        tasks.add(new Todo("third"));

        new DeleteCommand(2).execute(tasks, new Ui(), storage);

        assertEquals(2, tasks.size());
        assertEquals("first", tasks.get(0).getDescription());
        assertEquals("third", tasks.get(1).getDescription());
        List<Task> savedTasks = storage.loadTasks();
        assertEquals(2, savedTasks.size());
        assertEquals("first", savedTasks.get(0).getDescription());
        assertEquals("third", savedTasks.get(1).getDescription());
    }

    /**
     * Verifies that a deleted task is restored at its original position when saving fails.
     */
    @Test
    void execute_saveFails_restoresDeletedTaskAtOriginalIndex() {
        Storage failingStorage = new Storage(temporaryDirectory.resolve("data/tasks.txt").toString()) {
            @Override
            public void saveTasks(List<Task> tasks) throws BingusException {
                throw new BingusException("Test save failure.");
            }
        };
        Todo firstTask = new Todo("first");
        Todo deletedTask = new Todo("second");
        Todo lastTask = new Todo("third");
        TaskList tasks = new TaskList(List.of(firstTask, deletedTask, lastTask));

        assertThrows(BingusException.class, () -> new DeleteCommand(2).execute(tasks, new Ui(), failingStorage));

        assertEquals(List.of(firstTask, deletedTask, lastTask), tasks.getTasksSnapshot());
    }
}
