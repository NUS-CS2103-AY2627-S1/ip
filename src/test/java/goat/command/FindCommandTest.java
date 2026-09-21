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
import goat.task.TaskList;
import goat.task.Todo;
import goat.ui.Ui;

/**
 * Tests the search.
 *
 * The cases that matter are the ones a user would hit by accident: different
 * capitalization from the task, a keyword in the middle of a word, and a
 * search that finds nothing at all.
 */
public class FindCommandTest {

    private TaskList tasks;
    private Storage storage;
    private Ui ui;
    private ByteArrayOutputStream captured;
    private PrintStream originalOut;

    @BeforeEach
    public void setUp(@TempDir Path folder) {
        tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Deadline("return book", LocalDateTime.of(2019, 12, 2, 18, 0)));
        tasks.add(new Todo("join sports club"));
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
    public void execute_keywordInTwoTasks_showsBothNumberedFromOne() throws GoatException {
        new FindCommand("book").execute(tasks, ui, storage);

        String shown = output();
        assertTrue(shown.contains("Found these:"), shown);
        assertTrue(shown.contains("1.[T][ ] read book"), shown);
        // "return book" is third in the full list but second among the matches.
        assertTrue(shown.contains("2.[D][ ] return book (by: Dec 02 2019, 6:00pm)"), shown);
        assertFalse(shown.contains("join sports club"), shown);
    }

    @Test
    public void execute_differentCapitalization_stillMatches() throws GoatException {
        new FindCommand("BOOK").execute(tasks, ui, storage);
        assertTrue(output().contains("1.[T][ ] read book"), output());
    }

    @Test
    public void execute_keywordInsideAWord_matches() throws GoatException {
        // "port" sits inside "sports"; matching a substring is deliberate.
        new FindCommand("port").execute(tasks, ui, storage);
        assertTrue(output().contains("join sports club"), output());
    }

    @Test
    public void execute_noMatch_saysSoRatherThanShowingAnEmptyList() throws GoatException {
        new FindCommand("pineapple").execute(tasks, ui, storage);

        String shown = output();
        assertTrue(shown.contains("Nothing matches that."), shown);
        assertFalse(shown.contains("Found these:"), shown);
    }

    @Test
    public void execute_onEmptyList_saysNoMatches() throws GoatException {
        new FindCommand("book").execute(new TaskList(), ui, storage);
        assertTrue(output().contains("Nothing matches that."), output());
    }

    @Test
    public void execute_always_leavesTheListAlone() throws GoatException {
        new FindCommand("book").execute(tasks, ui, storage);
        assertEquals(3, tasks.size());
    }

    @Test
    public void execute_always_writesNothingToDisk() throws GoatException {
        new FindCommand("book").execute(tasks, ui, storage);
        // The save file is never created, because nothing changed.
        assertTrue(storage.load().isEmpty());
    }

    @Test
    public void isExit_findCommand_false() {
        assertFalse(new FindCommand("book").isExit());
    }
}
