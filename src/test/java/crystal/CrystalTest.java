package crystal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests Crystal's startup, command loop, persistence, recovery, and exit orchestration.
 */
public class CrystalTest {
    @TempDir
    private Path tempDirectory;

    private InputStream originalInput;
    private PrintStream originalOutput;
    private ByteArrayOutputStream capturedOutput;

    /** Redirects standard output before Crystal constructs its console UI. */
    @BeforeEach
    public void redirectStandardOutput() {
        originalInput = System.in;
        originalOutput = System.out;
        capturedOutput = new ByteArrayOutputStream();
        System.setOut(new PrintStream(capturedOutput, true, StandardCharsets.UTF_8));
    }

    /** Restores standard input and output after each application-loop test. */
    @AfterEach
    public void restoreStandardStreams() {
        System.setIn(originalInput);
        System.setOut(originalOutput);
    }

    /** Verifies startup loading, mutation persistence, feedback, and normal exit. */
    @Test
    public void run_existingData_loadsMutatesPersistsAndExits() throws Exception {
        Path dataFile = tempDirectory.resolve("data").resolve("crystal.txt");
        Files.createDirectories(dataFile.getParent());
        Files.write(dataFile, List.of("T | 0 | read book"), StandardCharsets.UTF_8);
        setInput("list\nmark 1\nbye\n");

        new Crystal(dataFile).run();

        assertEquals(List.of("T | 1 | read book"),
                Files.readAllLines(dataFile, StandardCharsets.UTF_8));
        String output = getOutput();
        assertTrue(output.contains("Crystal: Ta-da!!! Here are all your tasks:\n"
                + "         1.[T][ ] read book\n"));
        assertTrue(output.contains("Crystal: Yayyy!!! You finished this task:\n"
                + "         [T][X] read book\n"));
        assertTrue(output.contains("Crystal: Byeee!!! You did amazing today. See you soon!"));
    }

    /** Verifies recovery from corrupted data and replacement after a new mutation. */
    @Test
    public void run_corruptedData_reportsErrorUsesEmptyListAndReplacesFileOnMutation()
            throws Exception {
        Path dataFile = tempDirectory.resolve("crystal.txt");
        Files.writeString(dataFile, "corrupted data", StandardCharsets.UTF_8);
        setInput("todo recovered task\nbye\n");

        new Crystal(dataFile).run();

        assertEquals(List.of("T | 0 | recovered task"),
                Files.readAllLines(dataFile, StandardCharsets.UTF_8));
        String output = getOutput();
        assertTrue(output.contains("Crystal: Oopsies!!! Your saved task data is invalid."));
        assertTrue(output.contains("Crystal: Okkk!!! Here's your new task!!\n"
                + "         [T][ ] recovered task\n"));
    }

    /** Verifies that command errors are reported without ending the command loop. */
    @Test
    public void run_invalidCommand_reportsErrorAndContinuesToExit() {
        Path dataFile = tempDirectory.resolve("crystal.txt");
        setInput("unknown command\nbye\n");

        new Crystal(dataFile).run();

        String output = getOutput();
        int errorIndex = output.indexOf("Crystal: Oopsies!!! I don't know what that means :-(");
        int goodbyeIndex = output.indexOf(
                "Crystal: Byeee!!! You did amazing today. See you soon!");
        assertTrue(errorIndex >= 0);
        assertTrue(goodbyeIndex > errorIndex);
    }

    /** Verifies that independent GUI-style requests share task state and persistence. */
    @Test
    public void getResponse_successiveCommands_returnsResponsesAndPreservesState()
            throws Exception {
        Path dataFile = tempDirectory.resolve("crystal.txt");
        Crystal crystal = new Crystal(dataFile);

        String addResponse = crystal.getResponse("todo read book");
        String listResponse = crystal.getResponse("list");

        assertEquals("Crystal: Okkk!!! Here's your new task!!\n"
                + "         [T][ ] read book\n"
                + "         Now you have 1 task in the list.", addResponse);
        assertEquals("Crystal: Ta-da!!! Here are all your tasks:\n"
                + "         1.[T][ ] read book", listResponse);
        assertEquals(List.of("T | 0 | read book"),
                Files.readAllLines(dataFile, StandardCharsets.UTF_8));
    }

    /** Verifies that GUI-style requests return parser errors instead of throwing them. */
    @Test
    public void getResponse_invalidCommand_returnsUserFacingError() {
        Crystal crystal = new Crystal(tempDirectory.resolve("crystal.txt"));

        String response = crystal.getResponse("unknown command");

        assertEquals("Crystal: Oopsies!!! I don't know what that means :-(", response);
    }

    /** Verifies that empty GUI-style requests return the dedicated empty-input reply. */
    @Test
    public void getResponse_emptyCommand_returnsUserFacingError() {
        Crystal crystal = new Crystal(tempDirectory.resolve("crystal.txt"));

        String response = crystal.getResponse("");

        assertEquals("Crystal: Oopsies!!! I cant reply to nothing...", response);
    }

    /** Verifies that a rejected add command cannot change session or persisted state. */
    @Test
    public void getResponse_rejectedAddCommand_preservesExistingTaskState() throws Exception {
        Path dataFile = tempDirectory.resolve("crystal.txt");
        Crystal crystal = new Crystal(dataFile);
        crystal.getResponse("todo read book");

        String errorResponse = crystal.getResponse(
                "deadline submit report /by Friday /by Saturday");
        String listResponse = crystal.getResponse("list");

        assertEquals("Crystal: Oopsies!!! A deadline must have a description "
                + "and a /by time!", errorResponse);
        assertEquals("Crystal: Ta-da!!! Here are all your tasks:\n"
                + "         1.[T][ ] read book", listResponse);
        assertEquals(List.of("T | 0 | read book"),
                Files.readAllLines(dataFile, StandardCharsets.UTF_8));
    }

    /** Verifies that the first GUI-style request reports and recovers from corrupt data. */
    @Test
    public void getResponse_corruptedData_reportsErrorAndUsesEmptyTaskList()
            throws Exception {
        Path dataFile = tempDirectory.resolve("crystal.txt");
        Files.writeString(dataFile, "corrupted data", StandardCharsets.UTF_8);
        Crystal crystal = new Crystal(dataFile);

        String response = crystal.getResponse("list");

        assertEquals("Crystal: Oopsies!!! Your saved task data is invalid.\n"
                + "Crystal: Your task list is all clear, well done!", response);
    }

    /**
     * Supplies complete console commands before Crystal constructs its scanner.
     *
     * @param input newline-separated commands.
     */
    private static void setInput(String input) {
        System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
    }

    /**
     * Returns console output with platform line endings normalized.
     *
     * @return captured console output.
     */
    private String getOutput() {
        return capturedOutput.toString(StandardCharsets.UTF_8).replace("\r\n", "\n");
    }
}
