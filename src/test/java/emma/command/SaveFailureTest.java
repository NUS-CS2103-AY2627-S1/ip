package emma.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import emma.Deadline;
import emma.EmmaException;
import emma.Storage;
import emma.Task;
import emma.TaskList;
import emma.Todo;

/**
 * Checks that a command which cannot save leaves the task list exactly as it was, so a
 * change is either both made and saved or neither.
 *
 * <p>The save is made to fail without relying on file permissions: the save file is put
 * inside a folder that is really a file, so creating that folder always fails.
 */
public class SaveFailureTest {

    @TempDir
    Path tempDir;

    private Storage blockedStorage;

    @BeforeEach
    public void createBlockedStorage() throws IOException {
        Path blocker = tempDir.resolve("data");
        Files.writeString(blocker, "a file where the folder should be");
        blockedStorage = new Storage(blocker.resolve("emma.json").toString());
    }

    private TaskList twoTasks() {
        List<Task> initial = List.of(
                new Todo("read book"),
                new Deadline("return book", LocalDate.of(2019, 10, 15)));
        return new TaskList(initial);
    }

    @Test
    public void execute_addCannotSave_theTaskIsNotKept() {
        TaskList tasks = twoTasks();
        String before = tasks.format();

        assertThrows(EmmaException.class, () ->
                new AddTodoCommand("third").execute(tasks, blockedStorage));

        assertEquals(before, tasks.format());
        assertEquals(2, tasks.size());
    }

    @Test
    public void execute_addToAnEmptyListCannotSave_theListStaysEmpty() {
        TaskList tasks = new TaskList(List.of());

        assertThrows(EmmaException.class, () ->
                new AddTodoCommand("read book").execute(tasks, blockedStorage));

        assertTrue(tasks.isEmpty());
    }

    @Test
    public void execute_deleteCannotSave_theTaskIsPutBackWhereItWas() {
        TaskList tasks = twoTasks();
        String before = tasks.format();

        assertThrows(EmmaException.class, () ->
                new DeleteCommand(1).execute(tasks, blockedStorage));

        assertEquals(before, tasks.format());
        assertEquals(2, tasks.size());
    }

    @Test
    public void execute_markCannotSave_theTaskStaysNotDone() {
        TaskList tasks = twoTasks();

        assertThrows(EmmaException.class, () ->
                new MarkCommand(1, true).execute(tasks, blockedStorage));

        assertFalse(tasks.get(1).isDone());
    }

    @Test
    public void execute_unmarkCannotSave_theTaskStaysDone() throws EmmaException {
        TaskList tasks = twoTasks();
        tasks.applyMark(1, true);

        assertThrows(EmmaException.class, () ->
                new MarkCommand(1, false).execute(tasks, blockedStorage));

        assertTrue(tasks.get(1).isDone());
    }

    @Test
    public void execute_severalFailedCommands_leaveTheListUntouched() {
        TaskList tasks = twoTasks();
        String before = tasks.format();

        assertThrows(EmmaException.class, () ->
                new AddTodoCommand("third").execute(tasks, blockedStorage));
        assertThrows(EmmaException.class, () ->
                new DeleteCommand(2).execute(tasks, blockedStorage));
        assertThrows(EmmaException.class, () ->
                new MarkCommand(2, true).execute(tasks, blockedStorage));

        assertEquals(before, tasks.format());
    }
}
