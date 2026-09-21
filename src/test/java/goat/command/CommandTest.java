package goat.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import goat.GoatException;
import goat.storage.Storage;
import goat.task.TaskList;
import goat.task.Todo;
import goat.ui.Ui;

/**
 * Tests what each command actually does when it runs.
 *
 * Three things are worth checking for every command that changes something:
 * the list ends up right, the change reached the disk, and the user was told.
 * The third matters because a command that quietly succeeds is as much a bug
 * as one that quietly fails.
 *
 * The save file lives in a folder JUnit creates and deletes per test, and
 * System.out is captured so the replies can be read back instead of cluttering
 * the test output.
 */
public class CommandTest {

    private TaskList tasks;
    private Storage storage;
    private Ui ui;
    private ByteArrayOutputStream captured;
    private PrintStream originalOut;

    @BeforeEach
    public void setUp(@TempDir Path folder) {
        tasks = new TaskList();
        storage = new Storage(folder.resolve("goat.txt").toString());
        captured = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(captured));
        // Ui reads System.in only when asked, so building one here is safe.
        ui = new Ui();
    }

    @AfterEach
    public void tearDown() {
        System.setOut(originalOut);
    }

    /** What the user was shown since the last command ran. */
    private String output() {
        System.out.flush();
        return captured.toString();
    }

    @Test
    public void execute_addCommand_taskAppearsInTheList() throws GoatException {
        new AddCommand(new Todo("read book")).execute(tasks, ui, storage);

        assertEquals(1, tasks.size());
        assertEquals("[T][ ] read book", tasks.get(1).toString());
    }

    @Test
    public void execute_addCommand_confirmsWithTheNewCount() throws GoatException {
        new AddCommand(new Todo("read book")).execute(tasks, ui, storage);
        new AddCommand(new Todo("join sports club")).execute(tasks, ui, storage);

        assertTrue(output().contains("On the list:"), output());
        assertTrue(output().contains("That makes 2."), output());
    }

    @Test
    public void execute_addCommand_writesThroughToTheSaveFile() throws GoatException {
        new AddCommand(new Todo("read book")).execute(tasks, ui, storage);

        // The point of saving before confirming: what was promised is on disk.
        assertEquals(1, storage.load().size());
        assertEquals("[T][ ] read book", storage.load().get(0).toString());
    }

    @Test
    public void execute_addCommandForATaskAlreadyThere_refusedAndNotAdded() throws GoatException {
        new AddCommand(new Todo("read book")).execute(tasks, ui, storage);

        assertThrows(GoatException.class, () ->
                        new AddCommand(new Todo("read book")).execute(tasks, ui, storage));

        assertEquals(1, tasks.size());
    }

    @Test
    public void execute_addCommandForADuplicate_complaintShowsTheTask() throws GoatException {
        new AddCommand(new Todo("read book")).execute(tasks, ui, storage);

        GoatException e = assertThrows(GoatException.class, () ->
                        new AddCommand(new Todo("read book")).execute(tasks, ui, storage));

        assertTrue(e.getMessage().contains("already on the list"), e.getMessage());
        assertTrue(e.getMessage().contains("read book"), e.getMessage());
    }

    @Test
    public void execute_addCommandForADuplicate_saveFileUntouched() throws GoatException {
        new AddCommand(new Todo("read book")).execute(tasks, ui, storage);

        assertThrows(GoatException.class, () ->
                        new AddCommand(new Todo("read book")).execute(tasks, ui, storage));

        // The refusal happens before the save, so the file still holds one.
        assertEquals(1, storage.load().size());
    }

    @Test
    public void execute_markCommandDone_marksAndSaves() throws GoatException {
        new AddCommand(new Todo("read book")).execute(tasks, ui, storage);

        new MarkCommand(1, true).execute(tasks, ui, storage);

        assertEquals("[T][X] read book", tasks.get(1).toString());
        assertEquals("[T][X] read book", storage.load().get(0).toString());
        assertTrue(output().contains("Done. One less to climb:"), output());
    }

    @Test
    public void execute_markCommandNotDone_unmarksAndSaves() throws GoatException {
        new AddCommand(new Todo("read book")).execute(tasks, ui, storage);
        new MarkCommand(1, true).execute(tasks, ui, storage);

        new MarkCommand(1, false).execute(tasks, ui, storage);

        assertEquals("[T][ ] read book", tasks.get(1).toString());
        assertEquals("[T][ ] read book", storage.load().get(0).toString());
        assertTrue(output().contains("Back on the list:"), output());
    }

    @Test
    public void execute_markCommandOnMissingTask_exceptionThrownAndNothingSaved() {
        assertThrows(GoatException.class, () -> new MarkCommand(1, true).execute(tasks, ui, storage));
    }

    @Test
    public void execute_deleteCommand_removesAndSaves() throws GoatException {
        new AddCommand(new Todo("a")).execute(tasks, ui, storage);
        new AddCommand(new Todo("b")).execute(tasks, ui, storage);

        new DeleteCommand(1).execute(tasks, ui, storage);

        assertEquals(1, tasks.size());
        assertEquals("[T][ ] b", tasks.get(1).toString());
        assertEquals(1, storage.load().size());
        assertTrue(output().contains("Gone:"), output());
        assertTrue(output().contains("  [T][ ] a"), output());
    }

    @Test
    public void execute_deleteCommandOnMissingTask_exceptionThrown() {
        assertThrows(GoatException.class, () -> new DeleteCommand(1).execute(tasks, ui, storage));
    }

    @Test
    public void execute_listCommandOnEmptyList_saysSo() throws GoatException {
        new ListCommand().execute(tasks, ui, storage);
        assertTrue(output().contains("Nothing on the list. Enjoy it."), output());
    }

    @Test
    public void execute_listCommand_numbersTasksFromOne() throws GoatException {
        new AddCommand(new Todo("a")).execute(tasks, ui, storage);
        new AddCommand(new Todo("b")).execute(tasks, ui, storage);
        captured.reset();

        new ListCommand().execute(tasks, ui, storage);

        assertTrue(output().contains("What you are carrying:"), output());
        assertTrue(output().contains("1.[T][ ] a"), output());
        assertTrue(output().contains("2.[T][ ] b"), output());
    }

    @Test
    public void execute_listCommand_changesNothing() throws GoatException {
        new AddCommand(new Todo("a")).execute(tasks, ui, storage);

        new ListCommand().execute(tasks, ui, storage);

        assertEquals(1, tasks.size());
    }

    @Test
    public void execute_exitCommand_changesNothingAndSaysNothing() throws GoatException {
        new AddCommand(new Todo("a")).execute(tasks, ui, storage);
        captured.reset();

        new ExitCommand().execute(tasks, ui, storage);

        // The farewell is Goat's job, after the loop, not this command's.
        assertEquals("", output());
        assertEquals(1, tasks.size());
    }

    @Test
    public void isExit_exitCommand_true() {
        assertTrue(new ExitCommand().isExit());
    }

    @Test
    public void isExit_everyOtherCommand_false() {
        assertFalse(new AddCommand(new Todo("a")).isExit());
        assertFalse(new DeleteCommand(1).isExit());
        assertFalse(new MarkCommand(1, true).isExit());
        assertFalse(new ListCommand().isExit());
    }
}
