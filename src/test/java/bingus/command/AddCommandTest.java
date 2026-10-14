package bingus.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
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
 * Tests the state changes performed by {@link AddCommand}.
 */
class AddCommandTest {
    /** Directory created by JUnit for temporary test data. */
    @TempDir
    Path temporaryDirectory;

    /**
     * Verifies that adding a task updates both the in-memory list and saved task data.
     */
    @Test
    void execute_addsTaskAndPersistsIt() {
        Storage storage = new Storage(temporaryDirectory.resolve("data/tasks.txt").toString());
        TaskList tasks = new TaskList();
        Todo todo = new Todo("read JUnit guide");

        new AddCommand(todo).execute(tasks, new Ui(), storage);

        assertEquals(1, tasks.size());
        assertEquals(todo, tasks.get(0));
        List<Task> savedTasks = storage.loadTasks();
        Todo savedTodo = assertInstanceOf(Todo.class, savedTasks.get(0));
        assertEquals("read JUnit guide", savedTodo.getDescription());
    }

    /**
     * Verifies that an added task is removed from memory when saving the updated list fails.
     */
    @Test
    void execute_saveFails_removesNewTaskFromTaskList() {
        Storage failingStorage = new Storage(temporaryDirectory.resolve("data/tasks.txt").toString()) {
            @Override
            public void saveTasks(List<Task> tasks) throws BingusException {
                throw new BingusException("Test save failure.");
            }
        };
        Todo originalTask = new Todo("read book");
        Todo newTask = new Todo("write tests");
        TaskList tasks = new TaskList(List.of(originalTask));

        assertThrows(BingusException.class, () -> new AddCommand(newTask).execute(tasks, new Ui(), failingStorage));

        assertEquals(List.of(originalTask), tasks.getTasksSnapshot());
    }
}
