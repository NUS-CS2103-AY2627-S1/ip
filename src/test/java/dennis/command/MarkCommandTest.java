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
 * Tests for {@link MarkCommand#execute}: that a valid task number is marked
 * done and persisted, and that an out-of-range number is rejected (on top of
 * {@link TaskCommandTest}'s unit coverage of {@code checkInRange} itself)
 * without touching the list or the save file.
 */
public class MarkCommandTest {

    @TempDir
    private Path tempDir;

    private Path file() {
        return tempDir.resolve("dennis.txt");
    }

    @Test
    public void execute_validNumber_marksTaskDoneAndConfirms() throws DennisException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        DialogUi ui = new DialogUi();

        new MarkCommand(1).execute(tasks, ui, new Storage(file()));

        assertEquals("[T][X] read book", tasks.get(0).toString());
        assertTrue(ui.drain().contains("Paw-sitively marked as done:"));
    }

    @Test
    public void execute_validNumber_persistsTheDoneFlag() throws DennisException, IOException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        Path file = file();

        new MarkCommand(1).execute(tasks, new DialogUi(), new Storage(file));

        assertEquals(List.of("T | 1 | read book"),
                Files.readAllLines(file, StandardCharsets.UTF_8));
    }

    @Test
    public void execute_outOfRangeNumber_throwsAndLeavesUnchanged() throws DennisException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        Path file = file();

        DennisException e = assertThrows(DennisException.class, () ->
                new MarkCommand(2).execute(tasks, new DialogUi(), new Storage(file)));

        assertEquals("Index is out of range!!", e.getMessage());
        assertEquals("[T][ ] read book", tasks.get(0).toString());
        assertFalse(Files.exists(file));
    }

    @Test
    public void isExit_returnsFalse() {
        assertFalse(new MarkCommand(1).isExit());
    }
}
