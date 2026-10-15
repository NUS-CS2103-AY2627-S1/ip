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
 * Tests for {@link AddCommand#execute}: that a task is added, persisted, and
 * confirmed, and that an exact duplicate is rejected before either happens.
 *
 * <p>{@link Storage#Storage(Path)} is pointed at a JUnit {@code @TempDir} so
 * this suite never touches the real save file. {@link DialogUi} doubles as a
 * capturing {@code Ui}: its {@code drain()} lets a test see what a command
 * displayed without printing to the console.</p>
 */
public class AddCommandTest {

    @TempDir
    private Path tempDir;

    private Path file() {
        return tempDir.resolve("dennis.txt");
    }

    @Test
    public void execute_newTask_addsItAndConfirms() throws DennisException {
        TaskList tasks = new TaskList();
        DialogUi ui = new DialogUi();

        new AddCommand(new Todo("read book")).execute(tasks, ui, new Storage(file()));

        assertEquals(1, tasks.size());
        assertEquals("[T][ ] read book", tasks.get(0).toString());
        assertTrue(ui.drain().contains("Purrfect, I've added this task:"));
    }

    @Test
    public void execute_newTask_writesToStorage() throws DennisException, IOException {
        Path file = file();

        new AddCommand(new Todo("read book"))
                .execute(new TaskList(), new DialogUi(), new Storage(file));

        assertEquals(List.of("T | 0 | read book"),
                Files.readAllLines(file, StandardCharsets.UTF_8));
    }

    @Test
    public void execute_duplicateTask_throwsAndLeavesListAndFileUnchanged()
            throws DennisException, IOException {
        TaskList tasks = new TaskList();
        DialogUi ui = new DialogUi();
        Path file = file();
        Storage storage = new Storage(file);
        new AddCommand(new Todo("read book")).execute(tasks, ui, storage);
        ui.drain();

        DennisException e = assertThrows(DennisException.class, () ->
                new AddCommand(new Todo("read book")).execute(tasks, ui, storage));

        assertEquals("You already have a task exactly like this one.", e.getMessage());
        assertEquals(1, tasks.size());
        assertEquals(List.of("T | 0 | read book"),
                Files.readAllLines(file, StandardCharsets.UTF_8));
    }

    @Test
    public void isExit_returnsFalse() throws DennisException {
        assertFalse(new AddCommand(new Todo("read book")).isExit());
    }
}
