package aravii;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Exercises the command lifecycle shared by the GUI and text interface.
 */
class ChatSessionTest {
    @TempDir
    Path directory;

    @ParameterizedTest
    @ValueSource(strings = {"deadline /by ", "event /from /to "})
    void respond_reviewedMalformedCommands_preservesSavedTasksAndRecovers(String input) throws IOException {
        Path file = directory.resolve("aravii.txt");
        ChatSession session = new ChatSession(new Storage(file));
        session.respond("todo keep me");
        String saved = Files.readString(file);
        String before = session.respond("list");

        assertTrue(session.respond(input).startsWith("Error:"));
        assertEquals(before, session.respond("list"));
        assertEquals(saved, Files.readString(file));
        assertEquals("Added: [T] [ ] next\nNow you have 2 tasks in the list.", session.respond("todo next"));
        ChatSession restarted = new ChatSession(new Storage(file));
        assertEquals("Added: [T] [ ] later\nNow you have 3 tasks in the list.", restarted.respond("todo later"));
    }

    @Test
    void respond_allCommands_preserveChangesAcrossRestart() {
        Storage storage = new Storage(directory.resolve("data/aravii.txt"));
        ChatSession session = new ChatSession(storage);
        assertTrue(session.respond("help").contains("sort\nhelp\nbye"));
        assertEquals("There are no tasks.", session.respond("list"));
        assertTrue(session.respond("todo Zebra").startsWith("Added: [T]"));
        assertTrue(session.respond("deadline Alpha /by 2026-09-20").contains("Sep 20 2026"));
        assertTrue(session.respond("event Beta /from 2026-09-21 14:00 /to 2026-09-21 15:00")
                .startsWith("Added: [E]"));
        assertTrue(session.respond("mark 1").contains("[X]"));
        assertTrue(session.respond("unmark 1").contains("[ ]"));
        assertTrue(session.respond("find ALPHA").startsWith("2. [D]"));
        assertEquals("No matching tasks found.", session.respond("find absent"));
        assertTrue(session.respond("sort").startsWith("1. [D] [ ] Alpha"));
        assertTrue(session.respond("delete 3").contains("Zebra"));
        String list = session.respond("list");
        assertEquals(list, new ChatSession(storage).respond("list"));
        assertTrue(session.respond("bye").startsWith("Bye."));
        assertTrue(session.shouldExit());
    }

    @Test
    void respond_invalidCommands_doesNotLoseExistingTasks() {
        ChatSession session = new ChatSession(new Storage(directory.resolve("aravii.txt")));
        session.respond("todo keep me");
        String before = session.respond("list");
        String[] invalidCommands = {"deadline /by 2026-09-20", "todo one\ttwo", "Hi", "mark 0",
            "event /from 2026-09-21 14:00 /to 2026-09-21 15:00", "delete 999999999999"};
        for (String command : invalidCommands) {
            assertTrue(session.respond(command).startsWith("Error:"), command);
            assertEquals(before, session.respond("list"));
        }
    }

    @Test
    void respond_corruptSave_blocksChangesAndNeverOverwrites() throws IOException {
        Path file = directory.resolve("aravii.txt");
        String original = "TODO\tfalse\tkeep me\t\nbroken\n";
        Files.writeString(file, original);
        ChatSession session = new ChatSession(new Storage(file));
        assertTrue(session.getStartupError().contains("line 2"));
        for (String command : new String[]{"list", "todo new", "sort", "delete 1", "mark 1"}) {
            assertTrue(session.respond(command).contains("original file has not been changed"));
        }
        assertTrue(session.respond("help").startsWith("Available commands:"));
        session.respond("bye");
        assertTrue(session.shouldExit());
        assertEquals(original, Files.readString(file));
    }

    @Test
    void respond_saveFailure_reportsUnsavedStateAndRetriesBeforeExit() throws IOException {
        Path file = directory.resolve("data/aravii.txt");
        ChatSession session = new ChatSession(new Storage(file));
        Path blockedParent = file.getParent();
        Files.writeString(blockedParent, "blocker");
        assertTrue(session.respond("todo remember").contains("Changes are only in memory"));
        assertTrue(session.respond("bye").startsWith("Error:"));
        assertFalse(session.shouldExit());
        assertTrue(session.respond("list").contains("remember"));
        Files.delete(blockedParent);
        assertTrue(session.respond("bye").startsWith("Bye."));
        assertTrue(session.shouldExit());
        assertTrue(new ChatSession(new Storage(file)).respond("list").contains("remember"));
    }

    @Test
    void run_textInterface_showsBannerRecoversFromErrorsAndExits() {
        String commands = "deadline /by 2026-09-20\ntodo works\nlist\nbye\ntodo must not run\n";
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Ui ui = new Ui(new ByteArrayInputStream(commands.getBytes(StandardCharsets.UTF_8)), new PrintStream(output));
        ui.run(new ChatSession(new Storage(directory.resolve("aravii.txt"))));
        String transcript = output.toString(StandardCharsets.UTF_8);
        assertTrue(transcript.contains("( II )"));
        assertTrue(transcript.contains("Hello! I'm Arav (II)."));
        assertTrue(transcript.contains("Error:"));
        assertTrue(transcript.contains("1. [T] [ ] works"));
        assertTrue(transcript.contains("Bye. Hope to see you again soon!"));
        assertFalse(transcript.contains("must not run"));
    }

    @Test
    void run_endOfInput_showsGoodbye() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        new Ui(new ByteArrayInputStream(new byte[0]), new PrintStream(output))
                .run(new ChatSession(new Storage(directory.resolve("aravii.txt"))));
        assertTrue(output.toString(StandardCharsets.UTF_8).contains("Bye. Hope to see you again soon!"));
    }
}
