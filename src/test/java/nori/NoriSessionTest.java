package nori;

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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import nori.testutil.IsolatedSessionTest;
import nori.ui.GuiUi;

/**
 * Tests a whole console session, from the greeting to the goodbye.
 *
 * These drive the loop that reads commands rather than the single-command entry
 * point the graphical interface uses, so they cover how a session starts, how it
 * ends, and what it says about the task file it loaded.
 */
public class NoriSessionTest extends IsolatedSessionTest {
    private final ByteArrayOutputStream capturedOutput = new ByteArrayOutputStream();
    private PrintStream previousOut;
    private InputStream previousIn;

    @BeforeEach
    public void captureConsole() {
        previousOut = System.out;
        previousIn = System.in;
        System.setOut(new PrintStream(capturedOutput, true, StandardCharsets.UTF_8));
    }

    @AfterEach
    public void restoreConsole() {
        System.setOut(previousOut);
        System.setIn(previousIn);
    }

    @Test
    public void run_commandsThenBye_greetsRunsThemAndSaysGoodbye() {
        String output = runSession("todo read book", "list", "bye");

        assertTrue(output.contains("Noot noot! I'm Nori, your tiny task penguin."));
        assertTrue(output.contains("Noot noot! Task tucked safely under my wing:"));
        assertTrue(output.contains("1.[T][ ] read book"));
        assertTrue(output.contains("Noot noot! Time to waddle off. Stay frosty!"));
    }

    @Test
    public void run_inputEndingWithoutBye_stillSaysGoodbye() {
        String output = runSession("todo read book");

        assertTrue(output.contains("Noot noot! Time to waddle off. Stay frosty!"));
    }

    @Test
    public void run_commandsAfterBye_areNotRun() {
        String output = runSession("bye", "todo never added");

        assertFalse(output.contains("never added"));
    }

    @Test
    public void run_goodbye_isSaidExactlyOnce() {
        String output = runSession("todo read book", "bye");

        assertEquals(1, countOccurrences(output, "Time to waddle off"));
    }

    @Test
    public void run_blankLine_answersWithANudgeAndKeepsGoing() {
        String output = runSession("", "todo read book", "bye");

        assertTrue(output.contains("Noot? You didn't say anything."));
        assertTrue(output.contains("[T][ ] read book"));
    }

    @Test
    public void run_readableTaskFile_saysNothingAboutLoading() {
        runSession("todo read book", "bye");
        capturedOutput.reset();

        String output = runSession("bye");

        assertFalse(output.contains("corrupted"));
        assertFalse(output.contains("restored the backup"));
    }

    @Test
    public void run_damagedTaskFileWithABackup_reportsTheRecoveryAtStartup() throws IOException {
        runSession("todo keep this task", "bye");
        Files.writeString(storageDirectory.resolve("nori.txt"), "this is not a task",
                StandardCharsets.UTF_8);
        capturedOutput.reset();

        String output = runSession("list", "bye");

        assertTrue(output.contains("I've restored the backup"));
        assertTrue(output.contains("1.[T][ ] keep this task"));
    }

    @Test
    public void run_damagedTaskFileWithNoBackup_reportsTheFailureAndStartsEmpty()
            throws IOException {
        Files.writeString(storageDirectory.resolve("nori.txt"), "this is not a task",
                StandardCharsets.UTF_8);

        String output = runSession("list", "bye");

        assertTrue(output.contains("Your saved tasks in data/nori.txt are corrupted"));
        assertTrue(output.contains("Repair that file or move it out of the data folder"));
        assertTrue(output.contains("The iceberg is empty."));
    }

    @Test
    public void getLoadingMessage_readableTaskFile_isAbsent() {
        assertNull(new Nori(new GuiUi()).getLoadingMessage());
    }

    @Test
    public void main_inputEndingImmediately_runsASessionAndReturns() {
        System.setIn(new ByteArrayInputStream(new byte[0]));

        Nori.main(new String[0]);

        assertTrue(captured().contains("Noot noot! I'm Nori, your tiny task penguin."));
    }

    /**
     * Runs one console session on the supplied lines and returns everything it printed.
     *
     * @param inputs the lines standard input should supply, in order.
     * @return the session's console output.
     */
    private String runSession(String... inputs) {
        StringBuilder input = new StringBuilder();
        for (String line : inputs) {
            input.append(line).append('\n');
        }
        System.setIn(new ByteArrayInputStream(input.toString().getBytes(StandardCharsets.UTF_8)));

        new Nori().run();
        return captured();
    }

    /**
     * Returns everything written to standard output so far.
     *
     * @return the captured output.
     */
    private String captured() {
        return capturedOutput.toString(StandardCharsets.UTF_8).replace("\r\n", "\n");
    }

    /**
     * Counts how many times a phrase appears in some text.
     *
     * @param text the text to search.
     * @param phrase the phrase to count.
     * @return the number of occurrences.
     */
    private static int countOccurrences(String text, String phrase) {
        int count = 0;
        for (int index = text.indexOf(phrase); index != -1; index = text.indexOf(phrase, index + 1)) {
            count++;
        }
        return count;
    }
}
