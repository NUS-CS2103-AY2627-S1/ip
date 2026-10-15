package tangent;

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
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import tangent.exception.ErrorMessages;
import tangent.ui.Ui;

public class TangentTest {
    @TempDir
    Path tempDir;

    @Test
    public void executeCommand_lazilyLoadsAndExecutesCommands() throws Exception {
        Path dataFile = tempDir.resolve("tangent.txt");
        Tangent tangent = new Tangent(dataFile.toString());
        List<String> messages = new ArrayList<>();
        Ui ui = new Ui(messages::add);

        tangent.executeCommand("todo read book", ui);
        tangent.executeCommand("mark 1", ui);
        tangent.executeCommand("find book", ui);
        tangent.executeCommand("list", ui);
        tangent.executeCommand("bye", ui);

        assertTrue(tangent.isExitRequested());
        assertEquals(List.of("T | 1 | read book"), Files.readAllLines(dataFile));
        assertTrue(messages.contains("i've marked 1 task as done!"));
        assertTrue(messages.contains("bye o/ hope to see you again soon"));
    }

    @Test
    public void executeCommand_blankInput_reportsErrorWithoutExit() {
        Tangent tangent = new Tangent(tempDir.resolve("tangent.txt").toString());
        List<String> errors = new ArrayList<>();

        tangent.executeCommand("  \t", new Ui(_ -> { }, errors::add));

        assertFalse(tangent.isExitRequested());
        assertEquals(List.of(ErrorMessages.EMPTY_INPUT_MESSAGE), errors);
    }

    @Test
    public void executeCommand_invalidCommand_reportsParserError() {
        Tangent tangent = new Tangent(tempDir.resolve("tangent.txt").toString());
        List<String> errors = new ArrayList<>();

        tangent.executeCommand("unknown command", new Ui(_ -> { }, errors::add));

        assertEquals(List.of(ErrorMessages.INVALID_COMMAND_MESSAGE), errors);
        assertFalse(tangent.isExitRequested());
    }

    @Test
    public void executeCommand_storageLoadFailure_reportsStorageError() throws Exception {
        Path directory = tempDir.resolve("directory");
        Files.createDirectory(directory);
        Tangent tangent = new Tangent(directory.toString());
        List<String> errors = new ArrayList<>();

        tangent.executeCommand("list", new Ui(_ -> { }, errors::add));

        assertEquals(1, errors.size());
        assertTrue(errors.get(0).startsWith("data path is a directory:"));
        assertFalse(tangent.isExitRequested());
    }

    @Test
    public void executeCommand_invalidStoredDate_reportsDataFileRecord() throws Exception {
        Path dataFile = tempDir.resolve("tangent.txt");
        Files.writeString(dataFile, "D | 0 | return book | 31/2/2019 1800");
        Tangent tangent = new Tangent(dataFile.toString());
        List<String> errors = new ArrayList<>();

        tangent.executeCommand("list", new Ui(_ -> { }, errors::add));

        assertEquals(List.of(String.format(ErrorMessages.INVALID_STORED_DATE_MESSAGE,
                "D | 0 | return book | 31/2/2019 1800")), errors);
        assertFalse(tangent.isExitRequested());
    }

    @Test
    public void run_consoleLoopProcessesCommandsUntilBye() throws Exception {
        Path dataFile = tempDir.resolve("tangent.txt");
        InputStream originalInput = System.in;
        PrintStream originalOutput = System.out;
        ByteArrayOutputStream capturedOutput = new ByteArrayOutputStream();
        try {
            System.setIn(new ByteArrayInputStream("todo read book\nbye\n".getBytes(StandardCharsets.UTF_8)));
            System.setOut(new PrintStream(capturedOutput, true, StandardCharsets.UTF_8));

            new Tangent(dataFile.toString()).run();
        } finally {
            System.setIn(originalInput);
            System.setOut(originalOutput);
        }

        String output = capturedOutput.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("got it! you have a new task:"));
        assertTrue(output.contains("bye o/ hope to see you again soon"));
    }

    @Test
    public void run_loadFailure_reportsErrorAndStops() throws Exception {
        Path directory = tempDir.resolve("directory");
        Files.createDirectory(directory);
        InputStream originalInput = System.in;
        PrintStream originalOutput = System.out;
        ByteArrayOutputStream capturedOutput = new ByteArrayOutputStream();
        try {
            System.setIn(new ByteArrayInputStream("bye\n".getBytes(StandardCharsets.UTF_8)));
            System.setOut(new PrintStream(capturedOutput, true, StandardCharsets.UTF_8));

            new Tangent(directory.toString()).run();
        } finally {
            System.setIn(originalInput);
            System.setOut(originalOutput);
        }

        assertTrue(capturedOutput.toString(StandardCharsets.UTF_8)
                .contains("data path is a directory:"));
    }

    @Test
    public void run_blankAndInvalidCommands_reportsErrorsAndContinues() throws Exception {
        Path dataFile = tempDir.resolve("tangent.txt");
        InputStream originalInput = System.in;
        PrintStream originalOutput = System.out;
        ByteArrayOutputStream capturedOutput = new ByteArrayOutputStream();
        try {
            System.setIn(new ByteArrayInputStream("\nunknown\nbye\n".getBytes(StandardCharsets.UTF_8)));
            System.setOut(new PrintStream(capturedOutput, true, StandardCharsets.UTF_8));

            new Tangent(dataFile.toString()).run();
        } finally {
            System.setIn(originalInput);
            System.setOut(originalOutput);
        }

        String output = capturedOutput.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains(ErrorMessages.EMPTY_INPUT_MESSAGE));
        assertTrue(output.contains(ErrorMessages.INVALID_COMMAND_MESSAGE));
        assertTrue(output.contains("bye o/ hope to see you again soon"));
    }
}
