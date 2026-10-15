package jassabot;

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
import org.junit.jupiter.api.parallel.ResourceLock;

/**
 * Tests console sessions with in-memory streams, restoring global streams after every run.
 */
@ResourceLock("java.lang.System.in")
@ResourceLock("java.lang.System.out")
public class ConsoleTest {
    private static final String DIVIDER = "____________________________________________________________\n";
    private static final String WELCOME = DIVIDER
            + "   _                         ____        _\n"
            + "  | | __ _ ___ ___  __ _    | __ )  ___ | |_\n"
            + "  | |/ _` / __/ __|/ _` |   |  _ \\ / _ \\| __|\n"
            + "  | | (_| \\__ \\__ \\ (_| |   | |_) | (_) | |_\n"
            + " _|_|\\__,_|___/___/\\__,_|   |____/ \\___/ \\__|\n"
            + "Hello, I'm JassaBot.\nLet's make room for a little progress today.\n"
            + "Type help to see available commands.\n" + DIVIDER;

    @TempDir
    private Path directory;

    @Test
    public void run_immediateEndOfInput_printsWelcomeAndInputClosed() {
        JassaBot bot = new JassaBot(dataFile());
        assertEquals(WELCOME + DIVIDER + "Input closed. Take care, and keep growing.\n" + DIVIDER,
                runSession(bot, ""));
        assertFalse(bot.isExitRequested());
        assertFalse(Files.exists(dataFile()));
    }

    @Test
    public void run_bye_stopsWithoutReadingLaterCommands() {
        JassaBot bot = new JassaBot(dataFile());
        assertEquals(WELCOME + DIVIDER + "Bye for now. Take your time, and keep growing.\n" + DIVIDER,
                runSession(bot, "  bye  \ntodo ignored\n"));
        assertTrue(bot.isExitRequested());
        assertFalse(Files.exists(dataFile()));
    }

    @Test
    public void run_blankInputThenTaskAndEndOfInput_recoversAndPersists() {
        JassaBot bot = new JassaBot(dataFile());
        String expected = WELCOME
                + DIVIDER + "Please enter a command.\n" + DIVIDER
                + DIVIDER + "Planted a new task:\n  [T][ ] read book\nYour garden now holds 1 task.\n" + DIVIDER
                + DIVIDER + "Here's what's growing in your task list:\n1.[T][ ] read book\n" + DIVIDER
                + DIVIDER + "Input closed. Take care, and keep growing.\n" + DIVIDER;
        assertEquals(expected, runSession(bot, "\n  todo read book  \nlist\n"));
        assertEquals("Here's what's growing in your task list:\n1.[T][ ] read book",
                new JassaBot(dataFile()).getResponse("list"));
    }

    @Test
    public void run_loadingWarnings_printsWarningsBeforeAcceptingCommands() throws IOException {
        Files.writeString(dataFile(), "X | 0 | invalid\nT | 1 | kept\n");
        JassaBot bot = new JassaBot(dataFile());
        String expected = WELCOME + "WARNING: Skipped data line 1: unknown task type 'X'.\n" + DIVIDER
                + DIVIDER + "Here's what's growing in your task list:\n1.[T][X] kept\n" + DIVIDER
                + DIVIDER + "Bye for now. Take your time, and keep growing.\n" + DIVIDER;
        assertEquals(expected, runSession(bot, "list\nbye\n"));
    }

    /**
     * Converts JUnit's isolated directory to the relative path required by Storage.
     */
    private Path dataFile() {
        return Path.of("").toAbsolutePath().relativize(directory.resolve("tasks.txt"));
    }

    /**
     * Runs the real console loop and captures its exact output without retaining global stream changes.
     */
    private String runSession(JassaBot bot, String input) {
        InputStream originalInput = System.in;
        PrintStream originalOutput = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (PrintStream capture = new PrintStream(output, true, StandardCharsets.UTF_8)) {
            System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
            System.setOut(capture);
            bot.run();
        } finally {
            System.setIn(originalInput);
            System.setOut(originalOutput);
        }
        return output.toString(StandardCharsets.UTF_8).replace(System.lineSeparator(), "\n");
    }
}
