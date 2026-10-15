package elsa.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import elsa.ElsaException;
import elsa.storage.Storage;
import elsa.task.TaskList;
import elsa.task.Todo;
import elsa.ui.Ui;

/** Tests {@link ListCommand}, which shows the whole list. */
public class ListCommandTest {

    @Test
    public void execute_severalTasks_numbersThemFromOne(@TempDir Path folder) throws ElsaException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Todo("buy milk"));
        Storage storage = new Storage(folder.resolve("tasks.txt").toString());

        assertEquals("Here are the tasks in your list:\n1.[T][ ] read book\n2.[T][ ] buy milk",
                new ListCommand().execute(tasks, new Ui(), storage));
    }

    @Test
    public void execute_noTasks_saysSoInsteadOfShowingAnEmptyList(@TempDir Path folder)
            throws ElsaException {
        Storage storage = new Storage(folder.resolve("tasks.txt").toString());

        assertEquals("Into the Unknown.", new ListCommand().execute(new TaskList(), new Ui(), storage));
    }

    /**
     * Listing changes nothing, so it must not write the file. A command that
     * saved needlessly would rewrite a data file the user was part way through
     * editing by hand.
     */
    @Test
    public void execute_always_leavesTheStoreAlone(@TempDir Path folder) throws ElsaException {
        Path file = folder.resolve("tasks.txt");
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        new ListCommand().execute(tasks, new Ui(), new Storage(file.toString()));

        assertFalse(Files.exists(file), "listing wrote to the disk");
    }
}
