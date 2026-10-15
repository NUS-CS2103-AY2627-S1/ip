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
 * Tests for {@link DeleteCommand#execute}: that a valid task number is
 * removed and the remaining list persisted, and that an out-of-range number
 * is rejected without touching the list or the save file.
 */
public class DeleteCommandTest {

    @TempDir
    private Path tempDir;

    private Path file() {
        return tempDir.resolve("dennis.txt");
    }

    private static TaskList twoTasks() throws DennisException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("first"));
        tasks.add(new Todo("second"));
        return tasks;
    }

    @Test
    public void execute_validNumber_removesTaskAndConfirms() throws DennisException {
        TaskList tasks = twoTasks();
        DialogUi ui = new DialogUi();

        new DeleteCommand(1).execute(tasks, ui, new Storage(file()));

        assertEquals(1, tasks.size());
        assertEquals("[T][ ] second", tasks.get(0).toString());
        String shown = ui.drain();
        assertTrue(shown.contains("Scratched this task off the list:"));
        assertTrue(shown.contains("Meow there are 1 tasks left!"));
    }

    @Test
    public void execute_validNumber_persistsTheRemainingTasks()
            throws DennisException, IOException {
        TaskList tasks = twoTasks();
        Path file = file();

        new DeleteCommand(1).execute(tasks, new DialogUi(), new Storage(file));

        assertEquals(List.of("T | 0 | second"),
                Files.readAllLines(file, StandardCharsets.UTF_8));
    }

    @Test
    public void execute_outOfRangeNumber_throwsAndLeavesUnchanged() throws DennisException {
        TaskList tasks = twoTasks();
        Path file = file();

        DennisException e = assertThrows(DennisException.class, () ->
                new DeleteCommand(3).execute(tasks, new DialogUi(), new Storage(file)));

        assertEquals("Index is out of range!!", e.getMessage());
        assertEquals(2, tasks.size());
        assertFalse(Files.exists(file));
    }

    @Test
    public void isExit_returnsFalse() {
        assertFalse(new DeleteCommand(1).isExit());
    }
}
