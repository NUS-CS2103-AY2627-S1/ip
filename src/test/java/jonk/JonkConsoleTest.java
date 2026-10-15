package jonk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.parallel.ResourceLock;

/**
 * Tests the console loop with scripted input while restoring global streams after each session.
 */
@ResourceLock("SYSTEM_STREAMS")
@Timeout(10)
public class JonkConsoleTest {
    private static final String SEPARATOR = "\t____________________________________________________________\n";
    private static final String WELCOME = SEPARATOR
            + "\t       __  ____  _   ____ __\n"
            + "      / / / __ \\ | / / //_/\n"
            + " __  / / / / / /  |/ / ,<\n"
            + "/ /_/ / / /_/ / /|  / /| |\n"
            + "\\____/  \\____/_/ |_/_/ |_|\n"
            + "\tSystems online! I'm Jonk, your mission-control copilot.\n"
            + "What shall we put on the flight plan?\n"
            + "Type help to see the available commands.\n" + SEPARATOR;
    private static final String GOODBYE = SEPARATOR
            + "\tMission control signing off. Clear skies, explorer!\n" + SEPARATOR;

    @TempDir
    private Path tempDirectory;

    @Test
    public void run_commandsThenBye_printsExactTranscriptAndStopsReading() throws IOException {
        Path dataFile = tempDirectory.resolve("jonk.txt");

        String output = runSession(dataFile, "todo read book\nmark 1\nlist\nbye\ntodo must not run\n");

        assertEquals(WELCOME
                + SEPARATOR + "Mission logged:\n\t[T][ ] read book\nFlight plan now holds 1 mission.\n"
                + SEPARATOR
                + SEPARATOR + "Touchdown! Mission complete:\n\t[T][X] read book\n" + SEPARATOR
                + SEPARATOR + "Flight plan, coming right up:\n\t1.[T][X] read book\n" + SEPARATOR
                + GOODBYE, output);
        assertEquals("T | 1 | read book", Files.readAllLines(dataFile).getFirst());
    }

    @Test
    public void run_invalidInputThenBye_reportsErrorAndContinues() {
        String output = runSession(tempDirectory.resolve("jonk.txt"), "unknown\nbye\n");

        assertEquals(WELCOME
                + SEPARATOR + "Signal unclear. Type help to open the mission guide.\n" + SEPARATOR
                + GOODBYE, output);
    }

    @Test
    public void run_loadFailure_printsStartupErrorAndStillAcceptsCommands() throws IOException {
        Path dataFile = tempDirectory.resolve("jonk.txt");
        Files.writeString(dataFile, "X | 0 | invalid\n");

        String output = runSession(dataFile, "list\nbye\n");

        assertEquals(WELCOME + "Could not load tasks from " + dataFile
                + ": invalid data at line 1 (unknown task type 'X'). Starting with an empty task list.\n"
                + SEPARATOR + "Flight plan, coming right up:\n" + SEPARATOR + GOODBYE, output);
        assertEquals("X | 0 | invalid\n", Files.readString(dataFile));
    }

    @Test
    public void main_defaultStoragePath_runsInIsolatedWorkingDirectory() throws Exception {
        // Run the real entry point in a child process so its relative data path cannot touch user data.
        String javaExecutable = Path.of(System.getProperty("java.home"), "bin", "java").toString();
        String classPath = Path.of(Jonk.class.getProtectionDomain().getCodeSource().getLocation().toURI())
                .toString();
        Path outputFile = tempDirectory.resolve("console.txt");
        Process process = new ProcessBuilder(javaExecutable, "-ea", "-cp", classPath, "jonk.Jonk")
                .directory(tempDirectory.toFile())
                .redirectErrorStream(true)
                .redirectOutput(outputFile.toFile())
                .start();
        try {
            try (var input = process.getOutputStream()) {
                input.write("todo isolated task\nbye\n".getBytes(StandardCharsets.UTF_8));
            }
            assertTrue(process.waitFor(Duration.ofSeconds(5)), "Console entry point did not exit");
            assertEquals(0, process.exitValue());
            assertEquals(WELCOME + SEPARATOR
                    + "Mission logged:\n\t[T][ ] isolated task\nFlight plan now holds 1 mission.\n"
                    + SEPARATOR + GOODBYE, Files.readString(outputFile).replace("\r\n", "\n"));
            assertEquals("T | 0 | isolated task", Files.readAllLines(tempDirectory.resolve("data/jonk.txt"))
                    .getFirst());
        } finally {
            process.destroyForcibly();
        }
    }

    /**
     * Runs an isolated console session and restores standard streams even if a test fails.
     */
    private String runSession(Path dataFile, String commands) {
        InputStream originalInput = System.in;
        PrintStream originalOutput = System.out;
        ByteArrayOutputStream capturedOutput = new ByteArrayOutputStream();
        try (PrintStream output = new PrintStream(capturedOutput, true, StandardCharsets.UTF_8)) {
            System.setIn(new ByteArrayInputStream(commands.getBytes(StandardCharsets.UTF_8)));
            System.setOut(output);
            new Jonk(dataFile.toString()).run();
        } finally {
            System.setIn(originalInput);
            System.setOut(originalOutput);
        }
        return capturedOutput.toString(StandardCharsets.UTF_8).replace("\r\n", "\n");
    }
}
