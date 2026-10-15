package noms.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import noms.exception.InvalidSnoozeException;
import noms.exception.NomsException;
import noms.exception.StorageException;
import noms.storage.Storage;
import noms.task.Deadline;
import noms.task.Event;
import noms.task.TaskList;
import noms.task.ToDo;
import noms.ui.Ui;

/** Tests snoozing deadlines and events, including persistence and errors. */
public class SnoozeCommandTest {

    @TempDir
    private Path tempDir;

    private final PrintStream realOut = System.out;
    private Ui ui;
    private Storage storage;

    @BeforeEach
    public void setUp() {
        System.setOut(new PrintStream(OutputStream.nullOutputStream()));
        ui = new Ui();
        storage = new Storage(tempDir.toString(), "noms.txt");
    }

    @AfterEach
    public void tearDown() {
        System.setOut(realOut);
    }

    @Test
    public void execute_deadline_replacesDateAndPreservesCompletion() throws NomsException {
        Deadline deadline = new Deadline("submit report", LocalDate.of(2026, 9, 15));
        deadline.markAsDone();
        TaskList tasks = new TaskList(deadline);

        new SnoozeCommand("snooze 1 /by 2026-09-20").execute(tasks, ui, storage);

        assertEquals("[D][X] submit report (by: Sep 20 2026)", tasks.get(0).toString());
    }

    @Test
    public void execute_eventFromOnly_preservesDuration() throws NomsException {
        Event event = new Event("conference",
                LocalDate.of(2026, 9, 10), LocalDate.of(2026, 9, 12));
        TaskList tasks = new TaskList(event);

        new SnoozeCommand("snooze 1 /from 2026-09-20").execute(tasks, ui, storage);

        assertEquals("[E][ ] conference (from: Sep 20 2026 to: Sep 22 2026)",
                tasks.get(0).toString());
    }

    @Test
    public void execute_eventFromAndTo_replacesBothDates() throws NomsException {
        Event event = new Event("conference",
                LocalDate.of(2026, 9, 10), LocalDate.of(2026, 9, 12));
        TaskList tasks = new TaskList(event);

        new SnoozeCommand("snooze 1 /from 2026-09-20 /to 2026-10-01")
                .execute(tasks, ui, storage);

        assertEquals("[E][ ] conference (from: Sep 20 2026 to: Oct 01 2026)",
                tasks.get(0).toString());
    }

    @Test
    public void execute_validCommand_persistsUpdatedTask() throws NomsException, IOException {
        TaskList tasks = new TaskList(
                new Deadline("submit report", LocalDate.of(2026, 9, 15)));

        new SnoozeCommand("snooze 1 /by 2026-09-20").execute(tasks, ui, storage);

        assertEquals("[D][ ] submit report (by: Sep 20 2026)",
                storage.load().get(0).toString());
    }

    @Test
    public void execute_validCommand_showsUpdatedTask() throws NomsException {
        TaskList tasks = new TaskList(
                new Deadline("submit report", LocalDate.of(2026, 9, 15)));
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));

        new SnoozeCommand("snooze 1 /by 2026-09-20").execute(tasks, ui, storage);

        // Checked as two separate lines, not one string joined by "\n": println
        // emits the platform line separator (e.g. "\r\n" on Windows), so a
        // literal "\n" between them would not match there.
        assertTrue(output.toString().contains("Nom nom! Noms has snoozed this task:"));
        assertTrue(output.toString().contains("   [D][ ] submit report (by: Sep 20 2026)"));
    }

    @Test
    public void execute_todo_throwsInvalidSnoozeException() {
        TaskList tasks = new TaskList(new ToDo("read book"));

        assertThrows(InvalidSnoozeException.class,
                () -> new SnoozeCommand("snooze 1 /by 2026-09-20")
                        .execute(tasks, ui, storage));
    }

    @Test
    public void execute_invalidDeadlineSyntax_doesNotChangeTask() {
        Deadline deadline = new Deadline("submit report", LocalDate.of(2026, 9, 15));
        TaskList tasks = new TaskList(deadline);

        assertThrows(InvalidSnoozeException.class,
                () -> new SnoozeCommand("snooze 1 /from 2026-09-20")
                        .execute(tasks, ui, storage));
        assertEquals("[D][ ] submit report (by: Sep 15 2026)", deadline.toString());
    }

    @Test
    public void execute_emptyList_throwsNomsException() {
        TaskList tasks = new TaskList();

        assertThrows(NomsException.class,
                () -> new SnoozeCommand("snooze 1 /by 2026-09-20")
                        .execute(tasks, ui, storage));
    }

    @Test
    public void execute_outOfRangeNumber_throwsNomsException() {
        TaskList tasks = new TaskList(
                new Deadline("submit report", LocalDate.of(2026, 9, 15)));

        assertThrows(NomsException.class,
                () -> new SnoozeCommand("snooze 2 /by 2026-09-20")
                        .execute(tasks, ui, storage));
    }

    @Test
    public void execute_invalidEventSyntax_doesNotChangeTask() {
        Event event = new Event("conference",
                LocalDate.of(2026, 9, 10), LocalDate.of(2026, 9, 12));
        TaskList tasks = new TaskList(event);

        assertThrows(InvalidSnoozeException.class,
                () -> new SnoozeCommand("snooze 1 /to 2026-09-20")
                        .execute(tasks, ui, storage));
        assertEquals("[E][ ] conference (from: Sep 10 2026 to: Sep 12 2026)",
                event.toString());
    }

    @Test
    public void execute_invalidDate_doesNotChangeTask() {
        Deadline deadline = new Deadline("submit report", LocalDate.of(2026, 9, 15));
        TaskList tasks = new TaskList(deadline);

        assertThrows(NomsException.class,
                () -> new SnoozeCommand("snooze 1 /by tomorrow")
                        .execute(tasks, ui, storage));
        assertEquals("[D][ ] submit report (by: Sep 15 2026)", deadline.toString());
    }

    @Test
    public void execute_saveFails_throwsAndRestoresEventDates() throws IOException {
        Path blockedDirectory = tempDir.resolve("blocked-directory");
        Files.writeString(blockedDirectory, "not a directory");
        Storage blockedStorage = new Storage(blockedDirectory.toString(), "noms.txt");
        Event event = new Event("conference",
                LocalDate.of(2026, 9, 10), LocalDate.of(2026, 9, 12));
        TaskList tasks = new TaskList(event);

        assertThrows(StorageException.class,
                () -> new SnoozeCommand("snooze 1 /from 2026-10-01 /to 2026-10-05")
                        .execute(tasks, ui, blockedStorage));
        assertEquals("[E][ ] conference (from: Sep 10 2026 to: Sep 12 2026)",
                event.toString());
    }
}
