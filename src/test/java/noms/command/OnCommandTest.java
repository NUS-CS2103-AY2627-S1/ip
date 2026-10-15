package noms.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.LocalDate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import noms.storage.Storage;
import noms.task.Deadline;
import noms.task.Event;
import noms.task.TaskList;
import noms.ui.Ui;

/** Tests date-based task listing through {@link OnCommand}. */
public class OnCommandTest {
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
    public void execute_matchingDate_printsMatchesInListOrderWithoutModification() {
        LocalDate date = LocalDate.of(2026, 9, 20);
        TaskList tasks = new TaskList(
                new Deadline("submit report", date),
                new Event("conference", date.minusDays(1), date.plusDays(1)));

        new OnCommand(date).execute(tasks, ui, storage);

        String printed = output.toString(StandardCharsets.UTF_8);
        assertTrue(printed.indexOf("submit report") < printed.indexOf("conference"));
        assertEquals(2, tasks.size());
    }

    @Test
    public void execute_noMatches_printsEmptyDayMessage() {
        LocalDate date = LocalDate.of(2026, 9, 20);
        TaskList tasks = new TaskList(new Deadline("submit report", date.plusDays(1)));

        new OnCommand(date).execute(tasks, ui, storage);

        assertTrue(output.toString(StandardCharsets.UTF_8).contains("nothing on the menu that day"));
    }
}
