package cheecken;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.parallel.ResourceLock;

/**
 * Checks the console loop with controlled streams and isolated persistence.
 */
@ResourceLock("SYSTEM_STREAMS")
class CheeckenConsoleTest {
    @TempDir
    private Path directory;

    /**
     * Verifies run with end of input: exits without inventing farewell.
     */
    @Test
    void run_endOfInput_exitsWithoutInventingFarewell() {
        String output = run("", new Cheecken(directory.resolve("tasks.txt").toString()));
        assertTrue(output.contains("What can I do for you?"));
        assertFalse(output.contains("Bye."));
    }

    /**
     * Verifies run with error then valid command: recovers and stops at bye.
     * @throws Exception if test setup, execution, or file access fails
     */
    @Test
    void run_errorThenValidCommand_recoversAndStopsAtBye() throws Exception {
        Path file = directory.resolve("tasks.txt");
        String output = run("wat\ntodo keep\nbye\ntodo ignored\n", new Cheecken(file.toString()));
        assertTrue(output.contains("What do you want?"));
        assertTrue(output.contains("[T][ ] keep"));
        assertTrue(output.contains("Bye. Hope to see you again soon!"));
        assertFalse(output.contains("ignored"));
        assertEquals("T | 0 | keep\n", Files.readString(file));
    }

    /**
     * Verifies run with existing tasks: loads before first command.
     * @throws Exception if test setup, execution, or file access fails
     */
    @Test
    void run_existingTasks_loadsBeforeFirstCommand() throws Exception {
        Path file = directory.resolve("tasks.txt");
        Files.writeString(file, "T | 1 | saved\n");
        String output = run("list\n", new Cheecken(file.toString()));
        assertTrue(output.contains("1.[T][X] saved"));
        assertEquals(1, output.split("1\\.\\[T]\\[X] saved", -1).length - 1);
    }

    /**
     * Verifies default ui_prints to console.
     */
    @Test
    void defaultUi_printsToConsole() {
        PrintStream original = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (PrintStream captured = new PrintStream(output, true, StandardCharsets.UTF_8)) {
            System.setOut(captured);
            new Ui().showBye();
        } finally {
            System.setOut(original);
        }
        assertTrue(output.toString(StandardCharsets.UTF_8).contains("Bye. Hope to see you again soon!"));
    }

    /**
     * Runs one console session and always restores the process-wide streams.
     * @param input input supplied by the test case
     * @param chatbot chatbot session used by the window
     * @return captured console output
     */
    private String run(String input, Cheecken chatbot) {
        InputStream originalInput = System.in;
        PrintStream originalOutput = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (PrintStream captured = new PrintStream(output, true, StandardCharsets.UTF_8)) {
            System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
            System.setOut(captured);
            chatbot.run();
        } finally {
            System.setIn(originalInput);
            System.setOut(originalOutput);
        }
        return output.toString(StandardCharsets.UTF_8);
    }
}
