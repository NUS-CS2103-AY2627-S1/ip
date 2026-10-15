package cbt;

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

import storage.Storage;
import ui.Ui;

/** Tests the application's command loop with isolated input, output, and storage. */
public class CbtTest {
    @TempDir
    Path temporaryDirectory;

    /** Verifies that the command loop adds, reports, and saves a valid todo before exiting. */
    @Test
    public void run_addTodoThenExit_responsePrintedAndTaskSaved() throws Exception {
        InputStream originalIn = System.in;
        PrintStream originalOut = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Path dataFile = temporaryDirectory.resolve("tasks.txt");

        try {
            System.setIn(new ByteArrayInputStream(
                    "todo read book\nbye\n".getBytes(StandardCharsets.UTF_8)));
            System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
            Cbt application = new Cbt(new Ui(), new Storage(dataFile.toString()));

            application.run();
        } finally {
            System.setIn(originalIn);
            System.setOut(originalOut);
        }

        String consoleOutput = output.toString(StandardCharsets.UTF_8);
        assertTrue(consoleOutput.contains("Task locked into orbit"));
        assertTrue(consoleOutput.contains("Mission paused. Safe travels!"));
        assertTrue(Files.readString(dataFile).contains("TODO | 0 | read book"));
    }

    /** Verifies that an invalid command is reported without creating a data file. */
    @Test
    public void run_invalidCommand_errorPrintedAndNoDataFileCreated() {
        InputStream originalIn = System.in;
        PrintStream originalOut = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Path dataFile = temporaryDirectory.resolve("tasks.txt");

        try {
            System.setIn(new ByteArrayInputStream("dance\nbye\n".getBytes(StandardCharsets.UTF_8)));
            System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
            Cbt application = new Cbt(new Ui(), new Storage(dataFile.toString()));

            application.run();
        } finally {
            System.setIn(originalIn);
            System.setOut(originalOut);
        }

        assertTrue(output.toString(StandardCharsets.UTF_8).contains("That command is off course"));
        assertFalse(Files.exists(dataFile));
    }

    /** Verifies that GUI-style requests return responses and persist task changes. */
    @Test
    public void getResponse_addTodoThenList_responsesReturnedAndTaskSaved() throws Exception {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Cbt application = new Cbt(new Ui(), new Storage(dataFile.toString()));

        String addResponse = application.getResponse("todo read book");
        String listResponse = application.getResponse("list");

        assertTrue(addResponse.contains("Task locked into orbit"));
        assertTrue(listResponse.contains("1.[T][ ] read book"));
        assertEquals("TODO | 0 | read book" + System.lineSeparator(), Files.readString(dataFile));
    }

    /** Verifies that GUI-style requests return parser errors without changing storage. */
    @Test
    public void getResponse_invalidCommand_errorReturnedAndNoDataFileCreated() {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Cbt application = new Cbt(new Ui(), new Storage(dataFile.toString()));

        String response = application.getResponse("dance");

        assertTrue(response.contains("That command is off course"));
        assertFalse(Files.exists(dataFile));
    }

    /** Verifies that GUI responses distinguish valid commands from user-correctable errors. */
    @Test
    public void getResponseWithStatus_validAndInvalidCommands_correctErrorStatusReturned() {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Cbt application = new Cbt(new Ui(), new Storage(dataFile.toString()));

        CbtResponse validResponse = application.getResponseWithStatus("list");
        CbtResponse errorResponse = application.getResponseWithStatus("dance");

        assertFalse(validResponse.isError());
        assertTrue(errorResponse.isError());
        assertTrue(errorResponse.message().contains("That command is off course"));
    }

    /** Verifies that GUI responses report persistence failures instead of claiming success. */
    @Test
    public void getResponseWithStatus_unwritableDestination_errorStatusReturned() {
        Cbt application = new Cbt(new Ui(), new Storage(temporaryDirectory.toString()));

        CbtResponse response = application.getResponseWithStatus("todo read book");

        assertTrue(response.isError());
        assertTrue(response.message().contains("could not save your changes"));
    }

    /** Verifies startup storage warnings are exposed and printed by the console loop. */
    @Test
    public void run_malformedStoredTask_startupWarningExposedAndPrinted() throws Exception {
        InputStream originalIn = System.in;
        PrintStream originalOut = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(dataFile, "broken stored task" + System.lineSeparator());

        try {
            System.setIn(new ByteArrayInputStream("bye\n".getBytes(StandardCharsets.UTF_8)));
            System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
            Cbt application = new Cbt(new Ui(), new Storage(dataFile.toString()));

            assertTrue(application.getStartupWarning().orElseThrow().contains("skipped 1"));
            application.run();
        } finally {
            System.setIn(originalIn);
            System.setOut(originalOut);
        }

        assertTrue(output.toString(StandardCharsets.UTF_8).contains("malformed or duplicate saved task"));
    }

    /** Verifies a clean startup exposes no storage warning. */
    @Test
    public void getStartupWarning_missingDataFile_emptyValueReturned() {
        Cbt application = new Cbt(new Ui(),
                new Storage(temporaryDirectory.resolve("missing.txt").toString()));

        assertTrue(application.getStartupWarning().isEmpty());
    }

    /** Verifies an exit response does not create or modify persistent task data. */
    @Test
    public void getResponse_exitCommand_farewellReturnedWithoutSave() {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Cbt application = new Cbt(new Ui(), new Storage(dataFile.toString()));

        CbtResponse response = application.getResponseWithStatus("bye");

        assertEquals("Mission paused. Safe travels!", response.message());
        assertFalse(response.isError());
        assertFalse(Files.exists(dataFile));
    }
}
