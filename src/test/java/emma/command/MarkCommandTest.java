package emma.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import emma.EmmaException;
import emma.Storage;
import emma.Task;
import emma.TaskList;
import emma.Todo;

/**
 * Checks that marking changes only the numbered task, in the direction asked for.
 */
public class MarkCommandTest {

    @TempDir
    Path tempDir;

    private Storage storage() {
        return new Storage(tempDir.resolve("emma.json").toString());
    }

    private TaskList twoTodos() {
        return new TaskList(List.of(new Todo("read book"), new Todo("return book")));
    }

    @Test
    public void execute_mark_marksTheTaskDone() throws EmmaException {
        TaskList tasks = twoTodos();
        String response = new MarkCommand(2, true).execute(tasks, storage());
        assertEquals("Nice! I've marked this as done:\n  [T][x] return book", response);
        assertTrue(tasks.get(2).isDone());
    }

    @Test
    public void execute_unmark_marksTheTaskNotDone() throws EmmaException {
        TaskList tasks = twoTodos();
        Storage storage = storage();
        new MarkCommand(2, true).execute(tasks, storage);

        String response = new MarkCommand(2, false).execute(tasks, storage);
        assertEquals("Okay, I've marked this as not done yet:\n  [T][ ] return book", response);
        assertFalse(tasks.get(2).isDone());
    }

    @Test
    public void execute_mark_leavesTheOtherTasksAlone() throws EmmaException {
        TaskList tasks = twoTodos();
        new MarkCommand(2, true).execute(tasks, storage());
        assertFalse(tasks.get(1).isDone());
        assertEquals("1. [T][ ] read book\n2. [T][x] return book", tasks.format());
    }

    @Test
    public void execute_markATaskAlreadyDone_leavesItDone() throws EmmaException {
        TaskList tasks = twoTodos();
        Storage storage = storage();
        new MarkCommand(1, true).execute(tasks, storage);

        String response = new MarkCommand(1, true).execute(tasks, storage);
        assertEquals("Nice! I've marked this as done:\n  [T][x] read book", response);
        assertTrue(tasks.get(1).isDone());
    }

    @Test
    public void execute_numberOutsideTheList_isRejected() {
        TaskList tasks = twoTodos();
        Storage storage = storage();
        assertEquals("You don't have a task numbered 3.",
                assertThrows(EmmaException.class, () ->
                        new MarkCommand(3, true).execute(tasks, storage)).getMessage());
        assertEquals("You don't have a task numbered 0.",
                assertThrows(EmmaException.class, () ->
                        new MarkCommand(0, true).execute(tasks, storage)).getMessage());
        assertEquals("You don't have a task numbered -1.",
                assertThrows(EmmaException.class, () ->
                        new MarkCommand(-1, false).execute(tasks, storage)).getMessage());
    }

    @Test
    public void execute_rejectedMark_changesNothing() {
        TaskList tasks = twoTodos();
        Storage storage = storage();
        String before = tasks.format();
        assertThrows(EmmaException.class, () -> new MarkCommand(9, true).execute(tasks, storage));
        assertEquals(before, tasks.format());
    }

    @Test
    public void execute_mark_writesTheChangeToStorage() throws EmmaException {
        TaskList tasks = twoTodos();
        Storage storage = storage();
        new MarkCommand(1, true).execute(tasks, storage);

        TaskList reloaded = new TaskList(storage.load());
        Task first = reloaded.get(1);
        assertTrue(first.isDone());
        assertEquals("1. [T][x] read book\n2. [T][ ] return book", reloaded.format());
    }
}
