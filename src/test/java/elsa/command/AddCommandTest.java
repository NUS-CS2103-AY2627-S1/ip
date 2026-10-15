package elsa.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import elsa.ElsaException;
import elsa.storage.Storage;
import elsa.task.TaskList;
import elsa.task.Todo;
import elsa.ui.Ui;

/**
 * Tests {@link AddCommand}, which puts one task into the list.
 *
 * <p>Each test builds the command its own list, user interface and store, rather
 * than reaching it through the chatbot. That is what lets the store be checked:
 * a confirmation saying a task was added proves only that the sentence was
 * written, and the task the user cares about is the one still there tomorrow.
 */
public class AddCommandTest {

    /** Returns a store writing into the folder JUnit provided for this test. */
    private static Storage storeIn(Path folder) {
        return new Storage(folder.resolve("tasks.txt").toString());
    }

    @Test
    public void execute_newTask_addsItAndConfirms(@TempDir Path folder) throws ElsaException {
        TaskList tasks = new TaskList();

        String reply = new AddCommand(new Todo("read book")).execute(tasks, new Ui(), storeIn(folder));

        assertEquals(1, tasks.size());
        assertEquals("Frozen in place. I've added this task:\n"
                + "  [T][ ] read book\n"
                + "Now you have 1 task in the list.", reply);
    }

    /**
     * The confirmation is worded after the store has been written, so a task the
     * chatbot says it kept is a task that reached the disk.
     */
    @Test
    public void execute_newTask_writesItToTheStore(@TempDir Path folder) throws ElsaException {
        TaskList tasks = new TaskList();
        Storage storage = storeIn(folder);

        new AddCommand(new Todo("read book")).execute(tasks, new Ui(), storage);

        TaskList reloaded = storage.load().tasks();
        assertEquals(1, reloaded.size(), "the task never reached the disk");
        assertEquals("[T][ ] read book", reloaded.get(0).toString());
    }

    /**
     * The list is the only place a repeat can be noticed: the parser reads one
     * line at a time and never sees what is already there. The refusal has to
     * leave the list exactly as it was, not half changed.
     */
    @Test
    public void execute_taskAlreadyInTheList_refusesAndLeavesTheListAlone(@TempDir Path folder)
            throws ElsaException {
        TaskList tasks = new TaskList();
        Storage storage = storeIn(folder);
        new AddCommand(new Todo("read book")).execute(tasks, new Ui(), storage);

        AddCommand again = new AddCommand(new Todo("read book"));
        Ui ui = new Ui();
        ElsaException thrown = assertThrows(ElsaException.class, () -> again.execute(tasks, ui, storage));

        assertTrue(thrown.getMessage().contains("already have that one"), thrown.getMessage());
        assertEquals(1, tasks.size(), "the repeat was stored after all");
    }
}
