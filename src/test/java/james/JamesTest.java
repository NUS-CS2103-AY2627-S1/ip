package james;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.parallel.ResourceLock;

/**
 * Verifies console orchestration with isolated streams and storage.
 */
@ResourceLock("standardStreams")
public class JamesTest {
    private static final String DIVIDER = "____________________________________________________________";
    private static final String GREETING = "James the ぱんどろぼう\nShh! I'm James, your bread thief.\n" +
            "I'll guard your tasks. The bread? No promises!\n";

    private final InputStream originalInput = System.in;
    private final PrintStream originalOutput = System.out;
    @TempDir
    private Path directory;

    @AfterEach
    public void restoreStreams() {
        System.setIn(originalInput);
        System.setOut(originalOutput);
    }

    @Test
    public void run_immediateEof_printsOnlyGreeting() {
        assertEquals(encase(GREETING) + System.lineSeparator(), runConsole(""));
    }

    @Test
    public void run_randomStickerThenEof_printsFallbackAndContinues() {
        assertEquals(encase(GREETING) + System.lineSeparator() +
                encase("Hehe! A little treat from my secret stash!\n") +
                encase("Let's peek in the basket. Your tasks:\n"), runConsole("random_sticker\nlist"));
        assertFalse(Files.exists(directory.resolve("tasks.txt")));
    }

    @Test
    public void run_invalidThenValidCommand_recoversAndProcessesRemainingInput() {
        assertEquals(encase(GREETING) + System.lineSeparator() +
                encase("Gomen! A little flour in the gears.\n" +
                        "I don't know that recipe! Try list or todo <description>.\n") +
                encase("Let's peek in the basket. Your tasks:\n"), runConsole("unknown\nlist\n"));
    }

    @Test
    public void run_byeBeforeMutation_ignoresCommandsAfterExit() {
        assertEquals(encase(GREETING) + System.lineSeparator() + encase("Mata ne! Rest up. I smell fresh bread!\n"),
                runConsole("bye\ntodo ignored\n"));
        assertFalse(Files.exists(directory.resolve("tasks.txt")));
    }

    /**
     * Runs the real console loop using UTF-8 input and captures its exact output.
     */
    private String runConsole(String input) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
        try (PrintStream capture = new PrintStream(output, true, StandardCharsets.UTF_8)) {
            System.setOut(capture);
            new James(directory.resolve("tasks.txt").toString()).run();
        } finally {
            System.setOut(originalOutput);
        }
        return output.toString(StandardCharsets.UTF_8);
    }

    /**
     * Formats an expected console message without calling production formatting code.
     */
    private String encase(String message) {
        return DIVIDER + "\n" + message + DIVIDER + System.lineSeparator();
    }
}
