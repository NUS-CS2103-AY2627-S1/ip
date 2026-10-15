package noms.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import noms.storage.Storage;
import noms.task.TaskList;
import noms.task.ToDo;
import noms.ui.Ui;

/** Tests task searching through {@link FindCommand}. */
public class FindCommandTest {
    @TempDir
    private Path tempDir;

    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream output;
    private Storage storage;
    private Ui ui;

    @BeforeEach
    public void setUp() {
        output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
        storage = new Storage(tempDir.toString(), "noms.txt");
        ui = new Ui();
    }

    @AfterEach
    public void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    public void execute_matchingTasks_printsMatchesInListOrderWithoutModification() {
        TaskList tasks = new TaskList(
                new ToDo("read book"), new ToDo("buy milk"), new ToDo("book flight"));

        new FindCommand("book").execute(tasks, ui, storage);

        String printed = output.toString(StandardCharsets.UTF_8);
        assertTrue(printed.indexOf("read book") < printed.indexOf("book flight"));
        assertEquals(3, tasks.size());
    }

    @Test
    public void execute_noMatches_printsNotFoundMessage() {
        TaskList tasks = new TaskList(new ToDo("read book"));

        new FindCommand("holiday").execute(tasks, ui, storage);

        assertTrue(output.toString(StandardCharsets.UTF_8).contains("no tasks matching \"holiday\""));
    }
}
