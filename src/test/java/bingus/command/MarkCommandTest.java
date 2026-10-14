package bingus.command;

import static org.junit.jupiter.api.Assertions.assertFalse;
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
 * Tests the state changes performed by {@link MarkCommand}.
 */
class MarkCommandTest {
    /** Directory created by JUnit for temporary test data. */
    @TempDir
    Path temporaryDirectory;

    /**
     * Creates storage that always fails when saving tasks.
     *
     * @return Storage that fails when saving tasks.
     */
    private Storage createFailingStorage() {
        return new Storage(temporaryDirectory.resolve("data/tasks.txt").toString()) {
            @Override
            public void saveTasks(List<Task> tasks) throws BingusException {
                throw new BingusException("Test save failure.");
            }
        };
    }

    /**
     * Verifies that marking and then unmarking a task changes and persists its completion status.
     */
    @Test
    void execute_markThenUnmark_updatesAndPersistsTaskStatus() {
        Storage storage = new Storage(temporaryDirectory.resolve("data/tasks.txt").toString());
        TaskList tasks = new TaskList();
        Todo todo = new Todo("read JUnit guide");
        tasks.add(todo);

        new MarkCommand(1, true).execute(tasks, new Ui(), storage);

        assertTrue(todo.isDone());
        assertTrue(storage.loadTasks().get(0).isDone());

        new MarkCommand(1, false).execute(tasks, new Ui(), storage);

        assertFalse(todo.isDone());
        assertFalse(storage.loadTasks().get(0).isDone());
    }

    /**
     * Verifies that a task remains incomplete when marking it cannot be saved.
     */
    @Test
    void execute_markSaveFails_restoresIncompleteStatus() {
        Storage failingStorage = createFailingStorage();
        Todo todo = new Todo("read JUnit guide");
        TaskList tasks = new TaskList(List.of(todo));

        assertThrows(BingusException.class, () -> new MarkCommand(1, true).execute(tasks, new Ui(), failingStorage));

        assertFalse(todo.isDone());
    }

    /**
     * Verifies that a task remains complete when unmarking it cannot be saved.
     */
    @Test
    void execute_unmarkSaveFails_restoresCompleteStatus() {
        Storage failingStorage = createFailingStorage();
        Todo todo = new Todo("read JUnit guide");
        todo.mark();
        TaskList tasks = new TaskList(List.of(todo));

        assertThrows(BingusException.class, () -> new MarkCommand(1, false).execute(tasks, new Ui(), failingStorage));

        assertTrue(todo.isDone());
    }

    /**
     * Verifies that a completed task remains complete when marking it again cannot be saved.
     */
    @Test
    void execute_redundantMarkSaveFails_restoresCompleteStatus() {
        Storage failingStorage = createFailingStorage();
        Todo todo = new Todo("read JUnit guide");
        todo.mark();
        TaskList tasks = new TaskList(List.of(todo));
        MarkCommand command = new MarkCommand(1, true);

        assertThrows(BingusException.class, () -> command.execute(tasks, new Ui(), failingStorage));

        assertTrue(todo.isDone());
    }

    /**
     * Verifies that an incomplete task remains incomplete when unmarking it again cannot be saved.
     */
    @Test
    void execute_redundantUnmarkSaveFails_restoresIncompleteStatus() {
        Storage failingStorage = createFailingStorage();
        Todo todo = new Todo("read JUnit guide");
        TaskList tasks = new TaskList(List.of(todo));
        MarkCommand command = new MarkCommand(1, false);

        assertThrows(BingusException.class, () -> command.execute(tasks, new Ui(), failingStorage));

        assertFalse(todo.isDone());
    }
}
