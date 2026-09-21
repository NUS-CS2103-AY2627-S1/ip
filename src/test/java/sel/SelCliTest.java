package sel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
 * Tests the command-line loop ({@code Sel.run}) by feeding it a script of
 * commands through {@code System.in} and reading back what it printed.
 *
 * <p>These are the counterpart of {@link SelTest}, which drives the same
 * commands through the entry point the GUI uses. Both interfaces share
 * their validation, so this suite concentrates on the loop itself: which
 * commands are dispatched where, and when the loop stops.
 */
public class SelCliTest {

    @TempDir
    Path tempDir;

    private Path saveFile;
    private ByteArrayOutputStream captured;
    private PrintStream originalOut;
    private InputStream originalIn;

    @BeforeEach
    public void setUp() {
        saveFile = tempDir.resolve("sel.txt");
        originalOut = System.out;
        originalIn = System.in;
        captured = new ByteArrayOutputStream();
        System.setOut(new PrintStream(captured, true, StandardCharsets.UTF_8));
    }

    @AfterEach
    public void restoreStreams() {
        System.setOut(originalOut);
        System.setIn(originalIn);
    }

    /**
     * Runs the command loop over the given commands and returns everything
     * it printed. Sel wraps {@code System.in} when it is constructed, so
     * the input has to be in place before that happens.
     *
     * @param commands the lines to type, in order.
     * @return everything printed while the loop ran.
     */
    private String runWith(String... commands) {
        String script = String.join("\n", commands) + "\n";
        System.setIn(new ByteArrayInputStream(script.getBytes(StandardCharsets.UTF_8)));

        new Sel(saveFile.toString()).run();

        return captured.toString(StandardCharsets.UTF_8);
    }

    // ---------- the loop itself ----------

    @Test
    public void run_greetsBeforeReadingAnyCommand() {
        assertTrue(runWith("bye").contains("Sup, I'm Sel."));
    }

    @Test
    public void run_bye_saysGoodbyeAndStopsReading() {
        String output = runWith("bye", "todo should never be added");

        assertTrue(output.contains("Bye see ya later alligator."));
        assertFalse(output.contains("should never be added"), output);
    }

    @Test
    public void run_endOfInputWithoutBye_stopsWithoutComplaining() {
        String output = runWith("todo read book");

        assertTrue(output.contains("read book"));
        assertFalse(output.contains("Bye see ya later alligator."), output);
    }

    @Test
    public void run_blankLines_areSkippedWithoutAnError() {
        String output = runWith("", "   ", "list", "bye");

        assertFalse(output.contains("type something first"), output);
        assertTrue(output.contains("anyway here it is"), output);
    }

    @Test
    public void run_errorDoesNotStopTheLoop() {
        String output = runWith("frobnicate", "todo read book", "bye");

        assertTrue(output.contains("Rephrase your words"), output);
        assertTrue(output.contains("[T][ ] read book"), output);
        assertTrue(output.contains("Bye see ya later alligator."), output);
    }

    // ---------- each command reaches its handler ----------

    @Test
    public void run_todo_addsAndConfirms() {
        String output = runWith("todo read book", "bye");

        assertTrue(output.contains("Why more work for you"), output);
        assertTrue(output.contains("[T][ ] read book"), output);
        assertTrue(output.contains("1 task(s)"), output);
    }

    @Test
    public void run_deadline_addsAndConfirms() {
        String output = runWith("deadline return book /by 2019-12-02 1800", "bye");

        assertTrue(output.contains("[D][ ] return book"), output);
        assertTrue(output.contains("Dec 2 2019, 6:00PM"), output);
    }

    @Test
    public void run_event_addsAndConfirms() {
        String output = runWith("event meeting /from 2019-12-02 1400 /to 2019-12-02 1600", "bye");

        assertTrue(output.contains("[E][ ] meeting"), output);
        assertTrue(output.contains("Dec 2 2019, 2:00PM"), output);
    }

    @Test
    public void run_list_printsEveryTaskNumbered() {
        String output = runWith("todo read book", "todo join sports club", "list", "bye");

        assertTrue(output.contains("1.[T][ ] read book"), output);
        assertTrue(output.contains("2.[T][ ] join sports club"), output);
    }

    @Test
    public void run_markThenUnmark_confirmsBoth() {
        String output = runWith("todo read book", "mark 1", "unmark 1", "bye");

        assertTrue(output.contains("Marked task as done"), output);
        assertTrue(output.contains("[T][X] read book"), output);
        assertTrue(output.contains("Unmarked"), output);
    }

    @Test
    public void run_delete_removesTheTaskAndReportsWhatIsLeft() {
        String output = runWith("todo read book", "todo join sports club", "delete 1", "list", "bye");

        assertTrue(output.contains("You have fewer tasks now"), output);
        assertTrue(output.contains("1 task(s)"), output);
        assertTrue(output.contains("1.[T][ ] join sports club"), output);
    }

    @Test
    public void run_find_printsOnlyTheMatches() {
        String output = runWith("todo read book", "todo wash car", "find book", "bye");

        assertTrue(output.contains("Here are the matching tasks"), output);
        assertTrue(output.contains("read book"), output);
        assertFalse(output.contains("1.[T][ ] wash car"), output);
    }

    @Test
    public void run_findWithNoMatches_saysSo() {
        String output = runWith("todo read book", "find zzz", "bye");

        assertTrue(output.contains("nothing in your list matches"), output);
    }

    // ---------- errors reach the command line too ----------

    @Test
    public void run_argumentsAfterAnArgumentlessCommand_areReported() {
        String output = runWith("list all", "bye now", "bye");

        assertTrue(output.contains("doesn't take anything after it"), output);
        // "bye now" was rejected, so the loop kept going until the real bye.
        assertTrue(output.contains("Bye see ya later alligator."), output);
    }

    @Test
    public void run_taskNumberOutOfRange_isReported() {
        String output = runWith("todo read book", "mark 5", "bye");

        assertTrue(output.contains("Pick a number from 1 to 1"), output);
    }

    @Test
    public void run_duplicateTask_isReported() {
        String output = runWith("todo read book", "todo read book", "bye");

        assertTrue(output.contains("already on your list"), output);
    }

    @Test
    public void run_impossibleDateAndBackwardsEvent_areReported() {
        String output = runWith(
            "deadline x /by 2019-02-30 1800",
            "event y /from 2019-12-02 1800 /to 2019-12-02 1600",
            "bye");

        assertTrue(output.contains("doesn't exist"), output);
        assertTrue(output.contains("ends before it starts"), output);
    }

    @Test
    public void run_untidySpacing_isAccepted() {
        String output = runWith("   todo    read    book   ", "bye");

        assertTrue(output.contains("[T][ ] read book"), output);
    }

    // ---------- the save file ----------

    @Test
    public void run_reportsUnusableSavedDataAfterTheGreeting() throws IOException {
        Files.write(saveFile, List.of(
            "T | 0 | read book",
            "X | garbage | unknown type"));

        String output = runWith("list", "bye");

        int greeting = output.indexOf("Sup, I'm Sel.");
        int warning = output.indexOf("couldn't use");
        assertTrue(warning > greeting, output);
        // The usable line still loaded.
        assertTrue(output.contains("1.[T][ ] read book"), output);
    }

    @Test
    public void run_tasksAddedInOneSession_areThereInTheNext() {
        runWith("todo read book", "bye");
        captured.reset();

        String output = runWith("list", "bye");

        assertTrue(output.contains("1.[T][ ] read book"), output);
    }

    @Test
    public void run_savedFileMatchesWhatWasAdded() throws IOException {
        runWith("todo read book", "deadline return book /by 2019-12-02 1800", "bye");

        assertEquals(
            List.of("T | 0 | read book", "D | 0 | return book | 2019-12-02T18:00"),
            Files.readAllLines(saveFile));
    }
}
