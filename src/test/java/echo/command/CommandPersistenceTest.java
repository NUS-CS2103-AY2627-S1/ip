package echo.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import echo.exception.StorageException;
import echo.storage.Storage;
import echo.task.Task;
import echo.task.TaskList;
import echo.task.Todo;
import echo.ui.Ui;

/**
 * Tests that command mutations are rolled back or deferred when saving fails.
 */
public class CommandPersistenceTest {
    private static class FailingStorage extends Storage {
        FailingStorage() {
            super(Path.of("unused"));
        }

        @Override
        public void save(List<Task> tasks) throws StorageException {
            throw new StorageException("Simulated save failure.");
        }
    }

    @Test
    public void execute_addSaveFails_listUnchanged() {
        TaskList tasks = new TaskList();

        assertThrows(StorageException.class, () -> new AddCommand(new Todo("read book"))
                .execute(tasks, new Ui(), new FailingStorage()));
        assertEquals(0, tasks.size());
    }

    @Test
    public void execute_deleteSaveFails_listUnchanged() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        assertThrows(StorageException.class, () -> new DeleteCommand(0)
                .execute(tasks, new Ui(), new FailingStorage()));
        assertEquals(1, tasks.size());
    }

    @Test
    public void execute_markSaveFails_completionTimeRestored() {
        TaskList tasks = new TaskList();
        Todo task = new Todo("read book");
        tasks.add(task);

        assertThrows(StorageException.class, () -> new MarkCommand(0, true)
                .execute(tasks, new Ui(), new FailingStorage()));
        assertFalse(task.isDone());
    }

    @Test
    public void execute_unmarkSaveFails_completionTimeRestored() {
        TaskList tasks = new TaskList();
        Todo task = new Todo("read book");
        LocalDateTime completionTime = LocalDateTime.of(2025, 1, 13, 12, 45);
        task.markDone(completionTime);
        tasks.add(task);

        assertThrows(StorageException.class, () -> new MarkCommand(0, false)
                .execute(tasks, new Ui(), new FailingStorage()));
        assertTrue(task.isDone());
        assertEquals(completionTime, task.getCompletedAt());
    }
}
