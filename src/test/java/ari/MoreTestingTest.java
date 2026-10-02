package ari;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import ari.exception.EmptyArgumentException;
import ari.exception.TaskNotFoundException;
import ari.storage.Storage;
import ari.task.DeadlineTask;
import ari.task.EventTask;
import ari.task.Task;
import ari.task.TaskList;
import ari.task.TodoTask;

/** Broader persistence, representation and environment-boundary checks for A-MoreTesting. */
public class MoreTestingTest {
    @TempDir
    private Path directory;

    @Test
    public void processCommand_everyMutation_persistsBeforeExit() {
        String path = this.directory.resolve("ari.txt").toString();
        Ari ari = new Ari(path);
        String[] commands = {"todo zebra", "deadline Apple /by 2026-09-18",
            "event review /from morning /to evening", "mark 2", "unmark 2", "sort", "delete 1"};
        for (String command : commands) {
            assertFalse(ari.processCommand(command).isError(), command);
            Ari reloaded = new Ari(path);
            reloaded.start();
            assertEquals(ari.getResponse("list"), reloaded.getResponse("list"), command);
        }
    }

    @Test
    public void copy_mixedRepresentations_preservesFieldsWithoutSharingMutableTasks() throws TaskNotFoundException {
        TaskList original = new TaskList();
        original.addTask(new TodoTask("todo"));
        original.addTask(new DeadlineTask("date", LocalDate.of(2026, 9, 18)));
        original.addTask(new DeadlineTask("text", "whenever"));
        original.addTask(new EventTask("event", "morning", "evening"));
        original.markTask(2);
        TaskList copy = original.copy();
        for (int i = 0; i < original.getLength(); i++) {
            Task before = original.getTask(i);
            Task after = copy.getTask(i);
            assertNotSame(before, after);
            assertEquals(before.getClass(), after.getClass());
            assertEquals(before.toDataString(), after.toDataString());
        }
        copy.unmarkTask(2);
        copy.markTask(1);
        assertTrue(original.getTask(1).toString().contains("[X]"));
        assertTrue(original.getTask(0).toString().contains("[ ]"));
    }

    @Test
    public void parsing_turkishLocale_keepsCommandsAndDateDisplayStable() throws EmptyArgumentException {
        Locale previous = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));
            assertEquals(CommandType.FIND, Parser.parseCommandType("find book"));
            assertEquals(CommandType.LIST, Parser.parseCommandType("list"));
            assertEquals("[D][ ] review (by: Sep 18 2026)",
                    Parser.parseTask("deadline review /by 2026-09-18", CommandType.DEADLINE).toString());
        } finally {
            Locale.setDefault(previous);
        }
    }

    @Test
    public void parseStoredTask_invalidFields_rejectsMalformedRecords() {
        for (String line : new String[] {"T | 0 |", "T | 0 | ", "D | 0 | task |",
            "E | 1 | event | start |", "X | 0 | task", "T | yes | task", "T | 0 | a | b"}) {
            assertThrows(IllegalArgumentException.class, () -> Parser.parseStoredTask(line), line);
        }
    }

    @Test
    public void parseTask_calendarBoundaries_acceptsLeapDayRejectsImpossibleDates() throws EmptyArgumentException {
        assertEquals("D | 0 | leap | 2024-02-29",
                Parser.parseTask("deadline leap /by 2024-02-29", CommandType.DEADLINE).toDataString());
        for (String date : new String[] {"2025-02-29", "2026-04-31", "2026-13-01", "2026-01-00"}) {
            assertThrows(IllegalArgumentException.class, () ->
                    Parser.parseTask("deadline bad /by " + date, CommandType.DEADLINE));
        }
        assertEquals("D | 0 | legacy | 2025-02-29",
                Parser.parseStoredTask("D | 0 | legacy | 2025-02-29").toDataString());
    }

    @Test
    public void processCommand_unicodeDescription_roundTripsThroughUtf8() {
        String path = this.directory.resolve("ari.txt").toString();
        Ari ari = new Ari(path);
        assertFalse(ari.processCommand("todo 阅读 café ☕").isError());
        Ari reloaded = new Ari(path);
        assertEquals("Here are the tasks on your list:\n1. [T][ ] 阅读 café ☕", reloaded.getResponse("list"));
    }

    @Test
    public void start_ioFailure_protectsSessionAndNeverSaves() {
        Storage unavailable = new Storage(this.directory.resolve("ari.txt").toString()) {
            @Override
            public void start() throws IOException {
                throw new IOException("simulated initialization failure");
            }

            @Override
            public void saveFrom(TaskList tasks) {
                throw new AssertionError("Protected session must never save");
            }
        };
        Ari ari = new Ari(unavailable);
        assertTrue(ari.start().contains("protected"));
        assertTrue(ari.processCommand("sort").isError());
        assertTrue(ari.processCommand("bye").shouldExit());
    }

    @Test
    public void loadInto_invalidLaterRecord_doesNotPartiallyLoad() throws IOException {
        Path file = this.directory.resolve("ari.txt");
        Files.writeString(file, "T | 0 | valid\nT | broken | invalid\n");
        TaskList tasks = new TaskList();
        tasks.addTask(new TodoTask("existing"));
        assertThrows(IllegalArgumentException.class, () -> new Storage(file.toString()).loadInto(tasks));
        assertEquals("1. [T][ ] existing\n", tasks.toString());
    }
}
