package emma.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import emma.Deadline;
import emma.EmmaException;
import emma.Storage;
import emma.Task;
import emma.TaskList;
import emma.Todo;

/**
 * Checks that deleting removes the numbered task, reports it, and renumbers the rest.
 */
public class DeleteCommandTest {

    @TempDir
    Path tempDir;

    private Storage storage() {
        return new Storage(tempDir.resolve("emma.json").toString());
    }

    private TaskList threeTasks() {
        List<Task> initial = List.of(
                new Todo("read book"),
                new Deadline("return book", LocalDate.of(2019, 10, 15)),
                new Todo("join club"));
        return new TaskList(initial);
    }

    @Test
    public void execute_delete_reportsTheTaskItRemoved() throws EmmaException {
        TaskList tasks = threeTasks();
        String response = new DeleteCommand(2).execute(tasks, storage());
        assertEquals("Okay, I've removed this:\n  [D][ ] return book (by: Oct 15 2019)", response);
    }

    @Test
    public void execute_delete_renumbersTheTasksAfterIt() throws EmmaException {
        TaskList tasks = threeTasks();
        new DeleteCommand(2).execute(tasks, storage());
        assertEquals(2, tasks.size());
        assertEquals("1. [T][ ] read book\n2. [T][ ] join club", tasks.format());
    }

    @Test
    public void execute_deleteTheLastRemainingTask_emptiesTheList() throws EmmaException {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));
        new DeleteCommand(1).execute(tasks, storage());
        assertTrue(tasks.isEmpty());
    }

    @Test
    public void execute_numberOutsideTheList_isRejected() {
        TaskList tasks = threeTasks();
        Storage storage = storage();
        assertEquals("You don't have a task numbered 4.",
                assertThrows(EmmaException.class, () ->
                        new DeleteCommand(4).execute(tasks, storage)).getMessage());
        assertEquals("You don't have a task numbered 0.",
                assertThrows(EmmaException.class, () ->
                        new DeleteCommand(0).execute(tasks, storage)).getMessage());
    }

    @Test
    public void execute_deleteFromAnEmptyList_isRejected() {
        TaskList tasks = new TaskList(List.of());
        Storage storage = storage();
        assertEquals("You don't have a task numbered 1.",
                assertThrows(EmmaException.class, () ->
                        new DeleteCommand(1).execute(tasks, storage)).getMessage());
    }

    @Test
    public void execute_rejectedDelete_changesNothing() {
        TaskList tasks = threeTasks();
        Storage storage = storage();
        String before = tasks.format();
        assertThrows(EmmaException.class, () -> new DeleteCommand(9).execute(tasks, storage));
        assertEquals(before, tasks.format());
        assertEquals(3, tasks.size());
    }

    @Test
    public void execute_delete_writesTheChangeToStorage() throws EmmaException {
        TaskList tasks = threeTasks();
        Storage storage = storage();
        new DeleteCommand(1).execute(tasks, storage);

        TaskList reloaded = new TaskList(storage.load());
        assertEquals("1. [D][ ] return book (by: Oct 15 2019)\n2. [T][ ] join club",
                reloaded.format());
    }
}
