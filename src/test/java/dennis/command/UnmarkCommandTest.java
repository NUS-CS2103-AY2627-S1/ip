package dennis.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import dennis.DennisException;
import dennis.gui.DialogUi;
import dennis.storage.Storage;
import dennis.task.TaskList;
import dennis.task.Todo;

/**
 * Tests for {@link UnmarkCommand#execute}: that a valid task number is
 * marked not-done and persisted, and that an out-of-range number is
 * rejected without touching the list or the save file.
 */
public class UnmarkCommandTest {

    @TempDir
    private Path tempDir;

    private Path file() {
        return tempDir.resolve("dennis.txt");
    }

    private static Todo doneTodo(String description) throws DennisException {
        Todo todo = new Todo(description);
        todo.markAsDone();
        return todo;
    }

    @Test
    public void execute_validNumber_marksTaskNotDoneAndConfirms() throws DennisException {
        TaskList tasks = new TaskList();
        tasks.add(doneTodo("read book"));
        DialogUi ui = new DialogUi();

        new UnmarkCommand(1).execute(tasks, ui, new Storage(file()));

        assertEquals("[T][ ] read book", tasks.get(0).toString());
        assertTrue(ui.drain().contains("Un-fur-tunately, back on the list:"));
    }

    @Test
    public void execute_validNumber_persistsTheNotDoneFlag() throws DennisException, IOException {
        TaskList tasks = new TaskList();
        tasks.add(doneTodo("read book"));
        Path file = file();

        new UnmarkCommand(1).execute(tasks, new DialogUi(), new Storage(file));

        assertEquals(List.of("T | 0 | read book"),
                Files.readAllLines(file, StandardCharsets.UTF_8));
    }

    @Test
    public void execute_outOfRangeNumber_throwsAndLeavesUnchanged() throws DennisException {
        TaskList tasks = new TaskList();
        tasks.add(doneTodo("read book"));
        Path file = file();

        DennisException e = assertThrows(DennisException.class, () ->
                new UnmarkCommand(2).execute(tasks, new DialogUi(), new Storage(file)));

        assertEquals("Index is out of range!!", e.getMessage());
        assertEquals("[T][X] read book", tasks.get(0).toString());
        assertFalse(Files.exists(file));
    }

    @Test
    public void isExit_returnsFalse() {
        assertFalse(new UnmarkCommand(1).isExit());
    }
}
