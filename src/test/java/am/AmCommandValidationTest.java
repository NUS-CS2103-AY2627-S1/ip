package am;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import am.storage.Storage;
import am.task.TaskList;

/** Verifies validation through the GUI response boundary and the console loop. */
public class AmCommandValidationTest {
    @TempDir
    private Path directory;

    @Test
    void ignoresBlankResponsesBeforeLoadingAndResetsTheErrorFlag() {
        Storage inaccessible = new Storage(directory.resolve("tasks.txt").toString()) {
            @Override
            public TaskList load() throws IOException {
                throw new IOException("Simulated read failure");
            }
        };
        Am am = new Am(inaccessible);
        for (String input : new String[]{"", "  ", "\t\r\n", "\u2003"}) {
            assertEquals("", am.getResponse(input));
            assertFalse(am.isResponseError());
        }
        am.getResponse("list");
        assertTrue(am.isResponseError());
        assertEquals("", am.getResponse(" "));
        assertFalse(am.isResponseError());
    }

    @Test
    void reportsMissingTasksForEveryIndexedCommandWithoutChangingState() throws IOException {
        Path file = directory.resolve("tasks.txt");
        Am am = new Am(new Storage(file.toString()));
        for (String command : new String[]{"mark", "unmark", "delete", "edit"}) {
            String suffix = command.equals("edit") ? " /name x" : "";
            assertEquals("Task 1 does not exist. Your task list is empty. Example: todo buy milk.",
                    am.getResponse(command + " 1" + suffix));
            assertTrue(am.isResponseError());
            assertFalse(Files.exists(file));
        }
        am.getResponse("todo original");
        String before = am.getResponse("list");
        String saved = Files.readString(file);
        for (String command : new String[]{"mark", "unmark", "delete", "edit"}) {
            String suffix = command.equals("edit") ? " /name x" : "";
            for (int number : new int[]{2, Integer.MAX_VALUE}) {
                assertEquals("Task " + number + " does not exist. Choose a task number from 1 to 1."
                        + " Use list to see your tasks.", am.getResponse(command + " " + number + suffix));
                assertTrue(am.isResponseError());
                assertEquals(before, am.getResponse("list"));
                assertEquals(saved, Files.readString(file));
            }
        }
    }

    @Test
    void rejectsInvalidCommandsWithoutChangingMemoryOrDisk() throws IOException {
        Path file = directory.resolve("tasks.txt");
        Am am = new Am(new Storage(file.toString()));
        am.getResponse("todo original");
        am.getResponse("deadline report /by 2026-09-12");
        am.getResponse("event meeting /from 2026-09-12 1400 /to 2026-09-12 1600");
        String before = am.getResponse("list");
        String saved = Files.readString(file);
        String[] commands = {"todo a|b", "deadline a|b /by 2026-09-12",
            "event a|b /from 2026-09-12 /to 2026-09-12", "edit 1 /name a|b", "edit 2 /name a|b",
            "edit 3 /name a|b", "todo", "deadline /by 2026-09-12", "edit 1 /name",
            "deadline report /by 2026-09-12 /by 2026-09-13", "edit 1 /name a /name b",
            "mark 0", "unmark -1", "delete -2147483648", "edit 2147483648 /name x", "delete 1.5"};
        for (String command : commands) {
            assertFalse(am.getResponse(command).isBlank(), command);
            assertTrue(am.isResponseError(), command);
            assertEquals(before, am.getResponse("list"), command);
            assertFalse(am.isResponseError());
            assertEquals(saved, Files.readString(file), command);
        }
        assertEquals(before, new Am(new Storage(file.toString())).getResponse("list"));
    }

    @Test
    void showsSyntaxExamplesForEveryCommandWithoutChangingState() throws IOException {
        Path file = directory.resolve("tasks.txt");
        Am am = new Am(new Storage(file.toString()));
        am.getResponse("todo original");
        String saved = Files.readString(file);
        String[][] invalidCommands = {
            {"bye extra", "bye"},
            {"list extra", "list"},
            {"past extra", "past"},
            {"find", "find milk"},
            {"todo a|b", "todo buy milk"},
            {"deadline report /by 2026-09-12 /until 1800", "deadline report /by 2026-09-12 1800"},
            {"event meeting /from 2026-09-12 /to 2026-09-12 /by 1800",
                "event meeting /from 2026-09-12 1400 /to 2026-09-12 1600"},
            {"edit 1 /by 2026-09-12", "edit 1 /name buy milk"},
            {"mark 0", "mark 1"},
            {"unmark abc", "unmark 1"},
            {"delete 2147483648", "delete 1"}
        };
        for (String[] invalid : invalidCommands) {
            assertEquals("You messed up the command.\nExample: " + invalid[1],
                    am.getResponse(invalid[0]), invalid[0]);
            assertTrue(am.isResponseError(), invalid[0]);
            assertFalse(am.isExitRequested());
            assertEquals(saved, Files.readString(file));
            assertEquals("1. [T][ ] original", am.getResponse("list"));
            assertFalse(am.isResponseError());
        }
    }

    @Test
    void savesAcceptedWhitespaceAndSlashesAcrossRestart() throws IOException {
        Path file = directory.resolve("tasks.txt");
        Am am = new Am(new Storage(file.toString()));
        am.getResponse("  todo   read/write  notes  ");
        am.getResponse("  deadline   read/write  notes   /by 2026-09-12  ");
        am.getResponse("  event   read/write  notes   /from 2026-09-12 /to 2026-09-12  ");
        for (int number = 1; number <= 3; number++) {
            am.getResponse("  edit   " + number + "   /name read/write  revised notes  ");
            assertFalse(am.isResponseError());
        }
        String expected = "T | 0 | read/write  revised notes" + System.lineSeparator()
                + "D | 0 | read/write  revised notes | 2026-09-12T23:59" + System.lineSeparator()
                + "E | 0 | read/write  revised notes | 2026-09-12T00:00 | 2026-09-12T23:59"
                + System.lineSeparator();
        assertEquals(expected, Files.readString(file));
        assertEquals(am.getResponse("list"), new Am(new Storage(file.toString())).getResponse("list"));
    }

    @Test
    void ignoresBlankConsoleCommandsWithoutPrintingExtraResponses() throws IOException {
        assertEquals(runConsole("bye\n"), runConsole("\n  \t\n\u2003\nbye\n"));
    }

    /** Captures a console session while restoring the process-wide streams afterward. */
    private String runConsole(String input) throws IOException {
        InputStream originalInput = System.in;
        PrintStream originalOutput = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (PrintStream captured = new PrintStream(output, true, StandardCharsets.UTF_8)) {
            System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
            System.setOut(captured);
            new Am(new Storage(directory.resolve("console.txt").toString())).start();
        } finally {
            System.setIn(originalInput);
            System.setOut(originalOutput);
        }
        return output.toString(StandardCharsets.UTF_8);
    }
}
