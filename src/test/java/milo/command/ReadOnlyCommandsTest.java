package milo.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import milo.exception.MiloException;
import milo.storage.Storage;
import milo.task.TaskList;
import milo.task.Todo;
import milo.ui.Ui;

/**
 * The commands that only read the list: list, find and bye. They share a
 * test because each is small, and because what matters about all three is
 * the same thing, namely that they show the right lines and leave the list
 * and the save file alone.
 */
class ReadOnlyCommandsTest {
    @TempDir
    Path tempDir;

    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream capturedOut;

    @BeforeEach
    void redirectStdOut() {
        capturedOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(capturedOut));
    }

    @AfterEach
    void restoreStdOut() {
        System.setOut(originalOut);
    }

    private Storage newStorage() {
        return new Storage(tempDir.resolve("milo.txt").toString());
    }

    @Test
    void list_emptyList_saysSoRatherThanShowingAnEmptyBlock() throws MiloException {
        new ListCommand().execute(new TaskList(), new Ui(), newStorage());
        assertTrue(capturedOut.toString().contains("Your list is empty"));
    }

    @Test
    void list_withTasks_numbersThemFromOne() throws MiloException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("a"));
        tasks.add(new Todo("b"));
        new ListCommand().execute(tasks, new Ui(), newStorage());

        String printed = capturedOut.toString();
        assertTrue(printed.contains("1.[T][ ] a"));
        assertTrue(printed.contains("2.[T][ ] b"));
    }

    @Test
    void list_doesNotChangeTheList() throws MiloException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("a"));
        new ListCommand().execute(tasks, new Ui(), newStorage());
        assertEquals(1, tasks.size());
    }

    @Test
    void find_matchingKeyword_showsOnlyTheMatchesRenumbered() throws MiloException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Todo("write essay"));
        tasks.add(new Todo("return book"));
        new FindCommand("book").execute(tasks, new Ui(), newStorage());

        String printed = capturedOut.toString();
        assertTrue(printed.contains("1.[T][ ] read book"));
        assertTrue(printed.contains("2.[T][ ] return book"));
        assertTrue(!printed.contains("write essay"));
    }

    @Test
    void find_noMatches_saysSo() throws MiloException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        new FindCommand("essay").execute(tasks, new Ui(), newStorage());
        assertTrue(capturedOut.toString().contains("found nothing matching"));
    }

    @Test
    void find_isCaseSensitive() throws MiloException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("Read book"));
        new FindCommand("read").execute(tasks, new Ui(), newStorage());
        assertTrue(capturedOut.toString().contains("found nothing matching"));
    }

    @Test
    void exit_endsTheSession() {
        assertTrue(new ExitCommand().isExit());
    }

    @Test
    void exit_printsNothingItself() throws MiloException {
        // Milo.run() prints the goodbye once after its loop; printing it
        // here as well would show it twice.
        new ExitCommand().execute(new TaskList(), new Ui(), newStorage());
        assertEquals("", capturedOut.toString());
    }

    @Test
    void readOnlyCommands_areNotUndoable() {
        assertFalse(new ListCommand().isUndoable());
        assertFalse(new FindCommand("book").isUndoable());
        assertFalse(new ExitCommand().isUndoable());
    }
}
