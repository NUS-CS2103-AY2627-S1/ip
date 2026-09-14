package chione.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import chione.ChioneException;
import chione.Storage;
import chione.Ui;
import chione.task.TaskList;
import chione.task.Todo;

/**
 * Tests {@link AddCommand}, and in particular that it refuses a repeat.
 *
 * <p>The refusal lives in the command rather than in the task list, so it is
 * the command that has to be exercised: a test of {@code TaskList} alone would
 * still pass if the guard were removed.
 */
public class AddCommandTest {
    @TempDir
    private Path tempDir;

    @Test
    public void execute_newTask_addedAndSaved() throws ChioneException, IOException {
        TaskList tasks = new TaskList();
        Path saveFile = tempDir.resolve("chione.txt");

        new AddCommand(new Todo("read book")).execute(tasks, new Ui(), new Storage(saveFile.toString()));

        assertEquals(1, tasks.size());
        assertEquals("T | 0 | read book", Files.readString(saveFile).strip());
    }

    @Test
    public void execute_duplicateTodo_exceptionThrownAndNothingSaved() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        Path saveFile = tempDir.resolve("chione.txt");
        Storage storage = new Storage(saveFile.toString());

        ChioneException e = assertThrows(ChioneException.class, () ->
                new AddCommand(new Todo("Read Book")).execute(tasks, new Ui(), storage));

        assertEquals("That task is already resting in your list: [T][ ] Read Book. "
                + "I haven't added it twice; type list to see it.", e.getMessage());
        assertEquals(1, tasks.size());
        // The refusal happens before the save, so no file should have appeared.
        assertFalse(Files.exists(saveFile));
    }
}
