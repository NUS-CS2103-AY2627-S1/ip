package yqr.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import yqr.exception.YqrException;
import yqr.storage.Storage;
import yqr.task.TaskList;
import yqr.task.Todo;
import yqr.ui.Ui;

/**
 * Tests that task commands roll back in-memory changes when persistence fails.
 */
class CommandStorageFailureTest {
    private final Storage failingStorage = new FailingStorage();

    @Test
    void execute_addWhenSavingFails_taskRemovedAndSuccessNotShown() {
        TaskList tasks = new TaskList();
        List<String> output = new ArrayList<>();

        assertThrows(YqrException.class, () -> {
            new AddCommand(new Todo("task")).execute(tasks, new Ui(output::add), failingStorage);
        });

        assertEquals(0, tasks.getTaskCount());
        assertIterableEquals(List.of(), output);
    }

    @Test
    void execute_deleteWhenSavingFails_taskRestoredAndSuccessNotShown() {
        Todo task = new Todo("task");
        TaskList tasks = new TaskList(List.of(task));
        List<String> output = new ArrayList<>();

        assertThrows(YqrException.class, () -> {
            new DeleteCommand(1).execute(tasks, new Ui(output::add), failingStorage);
        });

        assertIterableEquals(List.of(task), tasks.getTasks());
        assertIterableEquals(List.of(), output);
    }

    @Test
    void execute_markWhenSavingFails_statusRestoredAndSuccessNotShown() {
        Todo task = new Todo("task");
        TaskList tasks = new TaskList(List.of(task));
        List<String> output = new ArrayList<>();

        assertThrows(YqrException.class, () -> {
            new MarkCommand(1).execute(tasks, new Ui(output::add), failingStorage);
        });

        assertFalse(task.isDone());
        assertIterableEquals(List.of(), output);
    }

    @Test
    void execute_unmarkWhenSavingFails_statusRestoredAndSuccessNotShown() {
        Todo task = new Todo("task");
        task.markAsDone();
        TaskList tasks = new TaskList(List.of(task));
        List<String> output = new ArrayList<>();

        assertThrows(YqrException.class, () -> {
            new UnmarkCommand(1).execute(tasks, new Ui(output::add), failingStorage);
        });

        assertTrue(task.isDone());
        assertIterableEquals(List.of(), output);
    }

    @Test
    void execute_undoWhenSavingFails_changeAndUndoAvailabilityRestored() throws YqrException {
        Todo task = new Todo("task");
        TaskList tasks = new TaskList();
        tasks.addTask(task);
        tasks.confirmLastChange();
        List<String> output = new ArrayList<>();

        assertThrows(YqrException.class, () -> {
            new UndoCommand().execute(tasks, new Ui(output::add), failingStorage);
        });

        assertIterableEquals(List.of(task), tasks.getTasks());
        assertIterableEquals(List.of(), output);

        tasks.undo();
        assertEquals(0, tasks.getTaskCount());
    }

    @Test
    void execute_failedChange_previousUndoRemainsAvailable() throws YqrException {
        Todo originalTask = new Todo("original");
        TaskList tasks = new TaskList();
        tasks.addTask(originalTask);
        tasks.confirmLastChange();

        assertThrows(YqrException.class, () -> {
            new AddCommand(new Todo("failed")).execute(tasks, new Ui(), failingStorage);
        });

        tasks.undo();
        assertEquals(0, tasks.getTaskCount());
    }

    /** Storage test double that always fails to save. */
    private static class FailingStorage extends Storage {
        FailingStorage() {
            super(Path.of("unused.txt"));
        }

        @Override
        public void saveTasks(TaskList taskList) throws YqrException {
            throw new YqrException("Unable to save tasks: simulated failure");
        }
    }
}
