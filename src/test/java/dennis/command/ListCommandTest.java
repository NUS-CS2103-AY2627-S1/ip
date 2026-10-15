package dennis.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import dennis.DennisException;
import dennis.gui.DialogUi;
import dennis.storage.Storage;
import dennis.task.TaskList;
import dennis.task.Todo;

/**
 * Tests for {@link ListCommand#execute}: the header shown with an empty
 * list, and that a non-empty list is shown numbered from 1 in list order.
 */
public class ListCommandTest {

    @TempDir
    private Path tempDir;

    private Storage storage() {
        return new Storage(tempDir.resolve("dennis.txt"));
    }

    @Test
    public void execute_emptyList_showsOnlyTheHeader() {
        DialogUi ui = new DialogUi();

        new ListCommand().execute(new TaskList(), ui, storage());

        assertEquals("Here's your list. Try to keep it that way.", ui.drain());
    }

    @Test
    public void execute_nonEmptyList_showsEachTaskNumberedFromOne() throws DennisException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("first"));
        tasks.add(new Todo("second"));
        DialogUi ui = new DialogUi();

        new ListCommand().execute(tasks, ui, storage());

        String[] lines = ui.drain().split(System.lineSeparator(), -1);
        assertEquals(3, lines.length);
        assertEquals("Here's your list. Try to keep it that way.", lines[0]);
        assertEquals("1.[T][ ] first", lines[1]);
        assertEquals("2.[T][ ] second", lines[2]);
    }

    @Test
    public void isExit_returnsFalse() {
        assertFalse(new ListCommand().isExit());
    }
}
