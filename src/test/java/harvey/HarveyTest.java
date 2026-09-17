package harvey;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
 * Tests {@link Harvey}, the class both interfaces talk to.
 * <p>
 * The other test classes check each part on its own. These check that the parts are
 * joined up correctly: that a startup problem becomes a warning, that every reply records
 * which command produced it, that tasks survive a restart, and that the text interface's
 * loop stops at the right moment.
 * <p>
 * The text interface reads {@code System.in} and writes {@code System.out}. The tests swap
 * both for in-memory streams, so a whole conversation can be fed in and its output read
 * back, and put the real streams back afterwards so later tests are unaffected.
 */
public class HarveyTest {
    /** The name of the save file each test uses, inside its temporary folder. */
    private static final String FILE_NAME = "harvey.txt";

    /** An empty folder created fresh for each test and removed afterwards. */
    @TempDir
    private Path tempDir;

    /** The real standard input, restored after each test. */
    private InputStream originalIn;

    /** The real standard output, restored after each test. */
    private PrintStream originalOut;

    /** Collects everything printed while a test runs. */
    private ByteArrayOutputStream printed;

    /** Swaps standard output for a buffer the test can read. */
    @BeforeEach
    public void captureOutput() {
        originalIn = System.in;
        originalOut = System.out;
        printed = new ByteArrayOutputStream();
        System.setOut(new PrintStream(printed, true, StandardCharsets.UTF_8));
    }

    /** Puts the real standard input and output back. */
    @AfterEach
    public void restoreStreams() {
        System.setIn(originalIn);
        System.setOut(originalOut);
    }

    /** Returns a Harvey saving into this test's temporary folder. */
    private Harvey harvey() {
        return new Harvey(tempDir.toString(), FILE_NAME);
    }

    /**
     * Makes the given lines the input a Harvey built afterwards will read.
     * Harvey's Ui opens standard input when it is constructed, so this must come first.
     */
    private void feedInput(String... lines) {
        String input = String.join(System.lineSeparator(), lines) + System.lineSeparator();
        System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
    }

    /** Writes the given lines straight to the save file, as a hand edit would. */
    private void writeSaveFile(String... lines) throws IOException {
        Files.write(tempDir.resolve(FILE_NAME), List.of(lines));
    }

    /** Returns everything printed so far. */
    private String output() {
        return printed.toString(StandardCharsets.UTF_8);
    }

    @Test
    public void constructor_noSaveFile_noStartupWarning() {
        // The first run on any computer takes this path, so it must stay silent.
        assertNull(harvey().getStartupWarning());
    }

    @Test
    public void constructor_damagedLine_warningCountsLineAndNamesBackup() throws IOException {
        writeSaveFile("T | 0 | keep me", "nonsense");
        String warning = harvey().getStartupWarning();

        assertTrue(warning.startsWith("Objection! "), warning);
        assertTrue(warning.contains("1 line(s)"), warning);
        assertTrue(warning.contains(".bak"), warning);
    }

    @Test
    public void constructor_saveFileIsAFolder_warningSaysStartingEmpty() throws IOException {
        Files.createDirectory(tempDir.resolve(FILE_NAME));
        Harvey harvey = harvey();

        // No data was at risk, so there is no backup sentence and no trailing space.
        String warning = harvey.getStartupWarning();
        assertTrue(warning.endsWith("Starting with an empty list."), warning);
        assertTrue(harvey.getResponse("list").contains("empty"));
    }

    @Test
    public void getGreeting_always_introducesHarvey() {
        assertTrue(harvey().getGreeting().contains("Harvey"));
    }

    @Test
    public void getResponse_validCommand_returnsReplyAndRecordsCommand() {
        Harvey harvey = harvey();
        String reply = harvey.getResponse("todo read book");

        assertTrue(reply.contains("[T][ ] read book"), reply);
        assertEquals("AddCommand", harvey.getCommandType());
        assertFalse(harvey.isExit());
    }

    @Test
    public void getResponse_invalidCommand_returnsObjectionAndRecordsError() {
        Harvey harvey = harvey();
        String reply = harvey.getResponse("blah");

        // Nothing is thrown: the error comes back as the reply, for the window to show.
        assertTrue(reply.startsWith("Objection! "), reply);
        assertEquals("ErrorCommand", harvey.getCommandType());
        assertFalse(harvey.isExit());
    }

    @Test
    public void getResponse_errorAfterBye_isExitResetToFalse() {
        Harvey harvey = harvey();
        harvey.getResponse("bye");
        assertTrue(harvey.isExit());

        harvey.getResponse("blah");
        assertFalse(harvey.isExit());
    }

    @Test
    public void getResponse_beforeAnyInput_commandTypeIsNull() {
        assertNull(harvey().getCommandType());
    }

    @Test
    public void getResponse_tasksAddedThenRestarted_tasksReloaded() {
        harvey().getResponse("deadline essay /by 2026-10-01");

        // A second Harvey on the same folder stands in for closing and reopening the app.
        String reply = harvey().getResponse("list");
        assertTrue(reply.contains("1.[D][ ] essay (by: Oct 1 2026)"), reply);
    }

    @Test
    public void run_inputWithBye_answersEachLineAndStopsAtBye() {
        feedInput("todo read book", "bye", "list");
        harvey().run();

        String output = output();
        assertTrue(output.contains("Harvey Specter"), output);
        assertTrue(output.contains("Consider it filed:"), output);
        assertTrue(output.contains("We're done here."), output);
        // The line after bye must never be read.
        assertFalse(output.contains("Here's your docket:"), output);
    }

    @Test
    public void run_inputEndsWithoutBye_stillSaysFarewell() {
        // Stands in for the user pressing Ctrl-D.
        feedInput("todo read book");
        harvey().run();

        assertTrue(output().contains("We're done here."), output());
    }

    @Test
    public void run_damagedSaveFile_warningPrintedAfterGreeting() throws IOException {
        writeSaveFile("nonsense");
        feedInput("bye");
        harvey().run();

        String output = output();
        int greeting = output.indexOf("Harvey Specter");
        int warning = output.indexOf("I could not understand");
        assertTrue(greeting >= 0 && warning > greeting, output);
    }
}
