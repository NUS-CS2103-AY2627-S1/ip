package sage;

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
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import sage.ui.Ui;

class SageTest {
    @TempDir
    Path tempDir;

    @Test
    void getResponse_invalidDates_preservesSavedDataAndRecoversAfterCorrection() throws Exception {
        Path dataFile = tempDir.resolve("tasks.txt");
        Sage sage = new Sage(dataFile.toString());
        sage.getResponse("todo Keep this task");
        String originalData = Files.readString(dataFile);

        for (String input : List.of("deadline report /by 12345", "deadline report /by 0000-01-01",
                "event meeting /from Monday /to 25pm")) {
            assertTrue(sage.getResponse(input).contains("That date or time is invalid."), input);
            assertTrue(sage.hasError(), input);
            assertEquals(originalData, Files.readString(dataFile), input);
        }

        sage.getResponse("deadline report /by 2028-02-29");
        assertFalse(sage.hasError());
        Sage restarted = new Sage(dataFile.toString());
        assertEquals("", restarted.getStartupMessage());
        assertEquals(String.join(System.lineSeparator(), "Here's your list:",
                "1. [T][ ] Keep this task", "2. [D][ ] report (by: Feb 29 2028)"), restarted.getResponse("list"));
    }

    @Test
    void getResponse_invalidSavedDate_protectsFileFromReplacement() throws Exception {
        Path dataFile = tempDir.resolve("tasks.txt");
        String originalData = "T | 0 | Keep this task\nD | 0 | report | 12345\n";
        Files.writeString(dataFile, originalData);
        Sage sage = new Sage(dataFile.toString());

        assertTrue(sage.getStartupMessage().contains("I couldn't read the saved data."));
        sage.getResponse("todo replacement");
        assertTrue(sage.hasError());
        assertEquals(originalData, Files.readString(dataFile));
    }

    @Test
    void getResponse_multipleCommandsPreserveStateAndHideConsoleSeparators() {
        Sage sage = new Sage(tempDir.resolve("tasks.txt").toString());

        String addResponse = sage.getResponse("todo read book");
        String markResponse = sage.getResponse("mark 1");
        String listResponse = sage.getResponse("list");

        assertEquals(String.join(System.lineSeparator(),
                "Noted. One less thing to remember:",
                "  [T][ ] read book",
                "1 item in your list."), addResponse);
        assertEquals(String.join(System.lineSeparator(),
                "A little progress. Marked as done:",
                "  [T][X] read book"), markResponse);
        assertEquals(String.join(System.lineSeparator(),
                "Here's your list:",
                "1. [T][X] read book"), listResponse);
        assertFalse(addResponse.contains(Ui.LINE));
        assertFalse(markResponse.contains(Ui.LINE));
        assertFalse(listResponse.contains(Ui.LINE));
    }

    @Test
    void getResponse_invalidCommandReturnsErrorWithoutChangingState() {
        Sage sage = new Sage(tempDir.resolve("tasks.txt").toString());
        sage.getResponse("todo read book");

        String errorResponse = sage.getResponse("mark 99");

        assertEquals("Let's try that again. The task number is invalid. Use a number from the current list.",
                errorResponse);
        assertFalse(errorResponse.contains(Ui.LINE));
        assertFalse(sage.isExit());
        assertEquals(String.join(System.lineSeparator(),
                "Here's your list:",
                "1. [T][ ] read book"), sage.getResponse("list"));
    }

    @Test
    void getResponse_mutatingCommandsPersistAcrossInstances() {
        Path dataFile = tempDir.resolve("tasks.txt");
        Sage firstSession = new Sage(dataFile.toString());
        firstSession.getResponse("todo submit report");
        firstSession.getResponse("mark 1");

        Sage secondSession = new Sage(dataFile.toString());

        assertEquals(String.join(System.lineSeparator(),
                "Here's your list:",
                "1. [T][X] submit report"), secondSession.getResponse("list"));
    }

    @Test
    void isExit_byeThenAnotherCommand_tracksMostRecentSuccessfulCommand() {
        Sage sage = new Sage(tempDir.resolve("tasks.txt").toString());
        assertFalse(sage.isExit());

        String byeResponse = sage.getResponse("bye");

        assertEquals("Take care. One step at a time.", byeResponse);
        assertTrue(sage.isExit());

        sage.getResponse("list");

        assertFalse(sage.isExit());
    }

    @Test
    void isExit_invalidCommandAfterBye_resetsExitState() {
        Sage sage = new Sage(tempDir.resolve("tasks.txt").toString());
        sage.getResponse("bye");
        assertTrue(sage.isExit());

        String response = sage.getResponse("not a command");

        assertTrue(response.startsWith("Let's try that again. "));
        assertFalse(sage.isExit());
    }

    @Test
    void hasError_invalidThenValidCommand_resetsErrorState() {
        Sage sage = new Sage(tempDir.resolve("tasks.txt").toString());
        assertFalse(sage.hasError());
        assertEquals("", sage.getStartupMessage());

        sage.getResponse("mark 1");
        assertTrue(sage.hasError());

        sage.getResponse("list");
        assertFalse(sage.hasError());
    }

    @Test
    void getResponse_find_retainsTaskNumbersForSubsequentCommands() {
        Sage sage = new Sage(tempDir.resolve("tasks.txt").toString());
        sage.getResponse("todo Read chapter");
        sage.getResponse("todo Write report");

        assertTrue(sage.getResponse("find report").contains("2. [T][ ] Write report"));
        assertTrue(sage.getResponse("mark 2").contains("[T][X] Write report"));
        assertTrue(sage.getResponse("list").contains("1. [T][ ] Read chapter"));
    }

    @Test
    void getResponse_corruptFile_preservesOriginalAndExplainsRecovery() throws Exception {
        Path dataFile = tempDir.resolve("tasks.txt");
        String originalData = "T|0|keep this task\nnot a valid record\n";
        Files.writeString(dataFile, originalData);

        Sage sage = new Sage(dataFile.toString());

        assertFalse(sage.getStartupMessage().isBlank());
        sage.getResponse("todo replacement");
        assertTrue(sage.hasError());
        assertEquals(originalData, Files.readString(dataFile));
        assertFalse(sage.getResponse("list").contains("replacement"));
    }

    @Test
    void getResponse_failedSave_doesNotReportSuccessOrKeepUnsavedTask() throws Exception {
        Path dataFolder = tempDir.resolve("data");
        Files.createDirectory(dataFolder);
        Sage sage = new Sage(dataFolder.resolve("tasks.txt").toString());
        Files.delete(dataFolder);
        Files.writeString(dataFolder, "a file now occupies the data directory");

        sage.getResponse("todo cannot save");

        assertTrue(sage.hasError());
        assertFalse(sage.getResponse("list").contains("cannot save"));
        assertEquals("a file now occupies the data directory", Files.readString(dataFolder));
    }

    @Test
    void run_endOfInput_exitsCleanlyWithoutInventingACommand() {
        String output = runConsole(tempDir.resolve("tasks.txt"), "");

        assertTrue(output.contains("Sage"));
        assertFalse(output.contains("Let's try that again."));
    }

    @Test
    void run_commandsAndErrors_continuesUntilBye() {
        String output = runConsole(tempDir.resolve("tasks.txt"),
                "todo Read chapter\nmark 99\nlist extra\nfind chapter\nlist\nbye\ntodo Must not execute\n");

        assertTrue(output.contains("1. [T][ ] Read chapter"));
        assertTrue(output.contains("The task number is invalid."));
        assertTrue(output.contains("The list command takes no extra arguments."));
        assertTrue(output.contains("Here's what I found:"));
        assertTrue(output.contains("Take care. One step at a time."));
        assertFalse(output.contains("Must not execute"));
    }

    @Test
    void run_corruptData_reportsStartupFailureAndKeepsOriginal() throws Exception {
        Path file = tempDir.resolve("tasks.txt");
        Files.writeString(file, "invalid data");

        String output = runConsole(file, "todo Do not replace the file\nbye\n");

        assertTrue(output.contains("I couldn't read the saved data."));
        assertTrue(output.contains("Changes are disabled"));
        assertEquals("invalid data", Files.readString(file));
    }

    /**
     * Runs a complete console session while restoring the process streams afterwards.
     *
     * @param dataFile The disposable data file for this test.
     * @param commands The simulated keyboard input.
     * @return Every line printed during the session.
     */
    private String runConsole(Path dataFile, String commands) {
        InputStream originalIn = System.in;
        PrintStream originalOut = System.out;
        ByteArrayOutputStream captured = new ByteArrayOutputStream();
        System.setIn(new ByteArrayInputStream(commands.getBytes(StandardCharsets.UTF_8)));
        System.setOut(new PrintStream(captured, true, StandardCharsets.UTF_8));
        try {
            new Sage(dataFile.toString()).run();
            return captured.toString(StandardCharsets.UTF_8);
        } finally {
            System.setIn(originalIn);
            System.setOut(originalOut);
        }
    }
}
