package charliek;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import charliek.storage.Storage;

/**
 * Tests the command-line application loop using isolated input and storage.
 */
class CharlieKTest {
    @TempDir
    Path tempDirectory;

    private InputStream originalInput;
    private PrintStream originalOutput;
    private ByteArrayOutputStream capturedOutput;

    /**
     * Redirects process-wide console streams before constructing CharlieK.
     */
    @BeforeEach
    void setUpConsole() {
        originalInput = System.in;
        originalOutput = System.out;
        capturedOutput = new ByteArrayOutputStream();
        System.setOut(new PrintStream(capturedOutput));
    }

    /**
     * Restores process-wide console streams after the application session.
     */
    @AfterEach
    void restoreConsole() {
        System.setIn(originalInput);
        System.setOut(originalOutput);
    }

    /**
     * Verifies a complete session initializes storage, executes commands, and exits cleanly.
     */
    @Test
    void run_validSession_persistsMutationAndGoodbye() throws Exception {
        Path taskFile = tempDirectory.resolve("tasks.csv");
        setInput("todo read book\nlist time\nmark 8\nbye\n");

        new CharlieK(taskFile.toString()).run();

        assertEquals(8, new Storage(taskFile).load().size());
        assertTrue(capturedOutput.toString().contains("I've added this task"));
        assertTrue(capturedOutput.toString().contains("marked this task as done"));
        assertTrue(capturedOutput.toString().contains("Bye. Keep carrying the flame!"));
    }

    /**
     * Verifies command and storage errors are rendered without terminating the session.
     */
    @Test
    void run_invalidCommands_continuesToFollowingCommand() {
        Path taskFile = tempDirectory.resolve("tasks.csv");
        setInput("list unsupported\nbye\n");

        new CharlieK(taskFile.toString()).run();

        String output = capturedOutput.toString();
        assertTrue(output.contains("That command format is not winning"));
        assertTrue(output.contains("Bye. Keep carrying the flame!"));
    }

    /**
     * Verifies a task-file path failure is shown during startup while the session remains usable.
     */
    @Test
    void run_directoryTaskPath_showsLoadingErrorAndAcceptsExit() throws Exception {
        Path directoryPath = tempDirectory.resolve("task-directory");
        Files.createDirectory(directoryPath);
        setInput("bye\n");

        new CharlieK(directoryPath.toString()).run();

        String output = capturedOutput.toString();
        assertTrue(output.contains("task file path is not a regular file"));
        assertTrue(output.contains("Bye. Keep carrying the flame!"));
    }

    /**
     * Replaces standard input with an exact session transcript.
     */
    private void setInput(String input) {
        System.setIn(new ByteArrayInputStream(input.getBytes()));
    }
}
