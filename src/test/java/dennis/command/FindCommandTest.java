package dennis.command;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import dennis.DennisException;
import dennis.gui.DialogUi;
import dennis.storage.Storage;
import dennis.task.TaskList;
import dennis.task.Todo;

/**
 * Tests for {@link FindCommand#execute}: that matches are shown at their
 * real position in the list, the same convention {@link OnCommand} uses,
 * and the message shown when nothing matches.
 */
public class FindCommandTest {

    @TempDir
    private Path tempDir;

    private Storage storage() {
        return new Storage(tempDir.resolve("dennis.txt"));
    }

    @Test
    public void execute_matchNotFirstInList_shownAtItsOriginalPosition() throws DennisException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("write essay"));
        tasks.add(new Todo("read book"));
        DialogUi ui = new DialogUi();

        new FindCommand("book").execute(tasks, ui, storage());

        String shown = ui.drain();
        assertTrue(shown.contains("2.[T][ ] read book"));
        assertFalse(shown.contains("1.[T][ ] read book"));
    }

    @Test
    public void execute_noMatches_showsFallbackMessage() throws DennisException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("write essay"));
        DialogUi ui = new DialogUi();

        new FindCommand("book").execute(tasks, ui, storage());

        assertTrue(ui.drain().contains("No matching tasks found."));
    }

    @Test
    public void execute_headerShown() {
        DialogUi ui = new DialogUi();

        new FindCommand("book").execute(new TaskList(), ui, storage());

        assertTrue(ui.drain().contains("Purr-fect, found these matching tasks:"));
    }

    @Test
    public void isExit_returnsFalse() {
        assertFalse(new FindCommand("book").isExit());
    }
}
