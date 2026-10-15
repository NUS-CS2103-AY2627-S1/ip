package dennis.command;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import dennis.DennisException;
import dennis.gui.DialogUi;
import dennis.storage.Storage;
import dennis.task.Deadline;
import dennis.task.TaskList;
import dennis.task.Todo;

/**
 * Tests for {@link OnCommand#execute}: that matches are shown at their
 * original list position, that a plain {@code Todo} never matches any date,
 * and the fallback message shown when nothing falls on the given date.
 */
public class OnCommandTest {

    @TempDir
    private Path tempDir;

    private Storage storage() {
        return new Storage(tempDir.resolve("dennis.txt"));
    }

    @Test
    public void execute_matchingTask_shownAtItsOriginalPosition() throws DennisException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Deadline("return book", "2019-12-01"));
        DialogUi ui = new DialogUi();

        new OnCommand(LocalDate.of(2019, 12, 1)).execute(tasks, ui, storage());

        String shown = ui.drain();
        assertTrue(shown.contains("2.[D][ ] return book (by: Dec 01 2019)"));
        assertFalse(shown.contains("1.[T]"));
    }

    @Test
    public void execute_plainTodo_neverMatchesAnyDate() throws DennisException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        DialogUi ui = new DialogUi();

        new OnCommand(LocalDate.of(2019, 12, 1)).execute(tasks, ui, storage());

        assertTrue(ui.drain().contains("You have no deadlines or events on that date."));
    }

    @Test
    public void execute_noMatches_showsFallbackMessage() {
        DialogUi ui = new DialogUi();

        new OnCommand(LocalDate.of(2019, 12, 1)).execute(new TaskList(), ui, storage());

        assertTrue(ui.drain().contains("You have no deadlines or events on that date."));
    }

    @Test
    public void execute_headerNamesTheDate() {
        DialogUi ui = new DialogUi();

        new OnCommand(LocalDate.of(2019, 12, 1)).execute(new TaskList(), ui, storage());

        assertTrue(ui.drain().contains("Here's what's happening on Dec 01 2019, if you must know."));
    }

    @Test
    public void isExit_returnsFalse() {
        assertFalse(new OnCommand(LocalDate.of(2019, 12, 1)).isExit());
    }
}
