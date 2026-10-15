package elsa.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import elsa.ElsaException;
import elsa.storage.Storage;
import elsa.task.TaskList;
import elsa.task.Todo;
import elsa.ui.Ui;

/** Tests {@link ExitCommand}, the one command that ends the session. */
public class ExitCommandTest {

    @Test
    public void execute_always_saysGoodbye(@TempDir Path folder) throws ElsaException {
        Storage storage = new Storage(folder.resolve("tasks.txt").toString());

        assertEquals("The cold never bother me anyways!",
                new ExitCommand().execute(new TaskList(), new Ui(), storage));
    }

    /**
     * Leaving is the only thing that ends a session. Every other command answers
     * the opposite, which is what keeps the session going after each one.
     */
    @Test
    public void isExit_leavingAgainstAnythingElse_onlyLeavingIsTrue() {
        assertTrue(new ExitCommand().isExit());
        assertFalse(new ListCommand().isExit());
        assertFalse(new HelpCommand().isExit());
        assertFalse(new AddCommand(new Todo("read book")).isExit());
        assertFalse(new DeleteCommand(1).isExit());
    }

    /**
     * The list is written after every change, so there is nothing left to write
     * on the way out and saying goodbye must not touch the file.
     */
    @Test
    public void execute_always_leavesTheStoreAlone(@TempDir Path folder) throws ElsaException {
        Path file = folder.resolve("tasks.txt");
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        new ExitCommand().execute(tasks, new Ui(), new Storage(file.toString()));

        assertFalse(Files.exists(file), "saying goodbye wrote to the disk");
    }
}
