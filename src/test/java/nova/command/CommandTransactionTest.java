package nova.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;

import nova.exception.NovaException;
import nova.storage.Storage;
import nova.task.Task;
import nova.task.TaskList;
import nova.task.Todo;
import nova.ui.Ui;

/**
 * Tests that mutating commands restore in-memory state when persistence fails.
 */
public class CommandTransactionTest {
    private static final String SAVE_ERROR = "simulated save failure";
    private final Ui ui = new Ui();
    private final Storage failingStorage = new FailingStorage();

    @Test
    public void execute_addSaveFails_removesAddedTask() {
        TaskList tasks = new TaskList(List.of(new Todo("existing task")));
        AddCommand command = new AddCommand(new Todo("new task"));

        NovaException exception = assertThrows(NovaException.class, () ->
                command.execute(tasks, ui, failingStorage));

        assertEquals(SAVE_ERROR, exception.getMessage());
        assertEquals(1, tasks.size());
        assertEquals("[T][ ] existing task", tasks.get(0).toString());
    }

    @Test
    public void execute_deleteSaveFails_restoresTaskAtOriginalPosition() {
        Todo first = new Todo("first");
        Todo second = new Todo("second");
        Todo third = new Todo("third");
        TaskList tasks = new TaskList(List.of(first, second, third));

        NovaException exception = assertThrows(NovaException.class, () ->
                new DeleteCommand(2).execute(tasks, ui, failingStorage));

        assertEquals(SAVE_ERROR, exception.getMessage());
        assertEquals(3, tasks.size());
        assertSame(first, tasks.get(0));
        assertSame(second, tasks.get(1));
        assertSame(third, tasks.get(2));
    }

    @Test
    public void execute_markSaveFails_restoresIncompleteStatus() {
        Todo task = new Todo("read book");
        TaskList tasks = new TaskList(List.of(task));

        NovaException exception = assertThrows(NovaException.class, () ->
                new MarkCommand(1).execute(tasks, ui, failingStorage));

        assertEquals(SAVE_ERROR, exception.getMessage());
        assertFalse(task.isDone());
    }

    @Test
    public void execute_unmarkSaveFails_restoresCompletedStatus() {
        Todo task = new Todo("read book");
        task.markAsDone();
        TaskList tasks = new TaskList(List.of(task));

        NovaException exception = assertThrows(NovaException.class, () ->
                new UnmarkCommand(1).execute(tasks, ui, failingStorage));

        assertEquals(SAVE_ERROR, exception.getMessage());
        assertTrue(task.isDone());
    }

    @Test
    public void execute_editSaveFails_restoresOriginalTask() {
        Todo task = new Todo("read book");
        task.markAsDone();
        TaskList tasks = new TaskList(List.of(task));

        NovaException exception = assertThrows(NovaException.class, () ->
                new EditCommand(1, EditField.DESCRIPTION, "read novel")
                        .execute(tasks, ui, failingStorage));

        assertEquals(SAVE_ERROR, exception.getMessage());
        assertSame(task, tasks.get(0));
        assertTrue(tasks.get(0).isDone());
    }

    @Test
    public void execute_taskNumberOutsideList_throwsRangeExceptionWithoutMutation() {
        Todo task = new Todo("read book");
        TaskList tasks = new TaskList(List.of(task));

        NovaException exception = assertThrows(NovaException.class, () ->
                new DeleteCommand(2).execute(tasks, ui, failingStorage));

        assertEquals("Task 2 does not exist. Choose a number from 1 to 1.", exception.getMessage());
        assertEquals(1, tasks.size());
        assertSame(task, tasks.get(0));
    }

    /**
     * Storage test double that makes every save fail before touching the file system.
     */
    private static class FailingStorage extends Storage {
        FailingStorage() {
            super(Path.of("unused"));
        }

        @Override
        public void save(List<Task> tasks) throws NovaException {
            throw new NovaException(SAVE_ERROR);
        }
    }
}
