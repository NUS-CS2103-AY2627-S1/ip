package goat.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.time.LocalDateTime;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import goat.GoatException;
import goat.storage.Storage;
import goat.task.Deadline;
import goat.task.SortOrder;
import goat.task.TaskList;
import goat.task.Todo;
import goat.ui.Ui;

/**
 * Tests what sorting does when it runs: the list is reordered, the new order
 * reaches the disk, and the user is shown the result with its new numbers.
 */
public class SortCommandTest {

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
        ui = new Ui();
    }

    @AfterEach
    public void tearDown() {
        System.setOut(originalOut);
    }

    private String output() {
        System.out.flush();
        return captured.toString();
    }

    @Test
    public void execute_byName_reordersAndRenumbers() throws GoatException {
        tasks.add(new Todo("zebra"));
        tasks.add(new Todo("apple"));

        new SortCommand(SortOrder.NAME).execute(tasks, ui, storage);

        assertEquals("apple", tasks.get(1).getDescription());
        assertTrue(output().contains("Sorted by name."), output());
        assertTrue(output().contains("1.[T][ ] apple"), output());
        assertTrue(output().contains("2.[T][ ] zebra"), output());
    }

    @Test
    public void execute_byDate_undatedLast() throws GoatException {
        tasks.add(new Todo("no date"));
        tasks.add(new Deadline("due soon", LocalDateTime.of(2019, 8, 6, 14, 0)));

        new SortCommand(SortOrder.DATE).execute(tasks, ui, storage);

        assertEquals("due soon", tasks.get(1).getDescription());
        assertTrue(output().contains("Sorted by date."), output());
    }

    @Test
    public void execute_always_savesTheNewOrder() throws GoatException {
        tasks.add(new Todo("zebra"));
        tasks.add(new Todo("apple"));

        new SortCommand(SortOrder.NAME).execute(tasks, ui, storage);

        // The order has to survive a restart, or sorting is only a way of
        // looking at the list rather than a change to it.
        assertEquals("apple", storage.load().get(0).getDescription());
    }

    @Test
    public void execute_emptyList_saysSoAndSavesNothing() throws GoatException {
        new SortCommand(SortOrder.NAME).execute(tasks, ui, storage);

        assertTrue(output().contains("Nothing to sort."), output());
        assertFalse(output().contains("Sorted by"), output());
        // No save file is written, because nothing changed.
        assertTrue(storage.load().isEmpty());
    }

    @Test
    public void execute_singleTask_stillConfirms() throws GoatException {
        tasks.add(new Todo("only one"));

        new SortCommand(SortOrder.NAME).execute(tasks, ui, storage);

        assertTrue(output().contains("Sorted by name."), output());
        assertTrue(output().contains("1.[T][ ] only one"), output());
    }

    @Test
    public void execute_doesNotAddOrRemoveTasks() throws GoatException {
        tasks.add(new Todo("b"));
        tasks.add(new Todo("a"));

        new SortCommand(SortOrder.NAME).execute(tasks, ui, storage);

        assertEquals(2, tasks.size());
    }

    @Test
    public void isExit_sortCommand_false() {
        assertFalse(new SortCommand(SortOrder.NAME).isExit());
    }
}
