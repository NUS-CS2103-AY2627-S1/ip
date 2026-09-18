package myriad;

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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests for {@link Myriad#getResponse(String)}, the single seam a GUI talks
 * through. It has to answer a line exactly as the console loop would — same
 * wording, same error handling — while also doing the two things the loop
 * does around it: greeting first, and saying goodbye at the end. The console
 * loop itself, {@link Myriad#run()}, is tested too, by swapping standard
 * input and output for in-memory streams.
 * <p>
 * Every test points the session at a JUnit-managed temporary data file, so
 * the real one at {@code data/myriad.txt} is never touched. No JavaFX class
 * is used here: a headless test JVM has no toolkit to load one into.
 */
public class MyriadTest {

    /** What a recorded reply puts between its lines, on every platform. */
    private static final String NEWLINE = "\n";

    /** JUnit creates this per test and deletes it afterwards. */
    @TempDir
    private Path tempDir;

    /** Path to a data file inside the temp directory, not yet created. */
    private Path dataFile() {
        return tempDir.resolve("myriad.txt");
    }

    /** A session on the temp data file, with console echoing off as a GUI has it. */
    private Myriad sessionAtTempFile() {
        return new Myriad(dataFile().toString(), false);
    }

    /** Writes the given lines to the data file, newline-separated. */
    private void writeDataFile(String... lines) throws IOException {
        Files.writeString(dataFile(), String.join("\n", lines) + "\n");
    }

    /**
     * Runs a console session on the temp data file, feeding {@code input} to
     * it as if typed, and returns everything it printed with line endings
     * normalised. Standard input and output are restored afterwards, even if
     * the session throws, so later tests are unaffected.
     */
    private String runConsoleSession(String input) {
        InputStream originalIn = System.in;
        PrintStream originalOut = System.out;
        ByteArrayOutputStream printed = new ByteArrayOutputStream();
        try {
            System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
            System.setOut(new PrintStream(printed, true, StandardCharsets.UTF_8));
            new Myriad(dataFile().toString()).run();
        } finally {
            System.setIn(originalIn);
            System.setOut(originalOut);
        }
        return printed.toString(StandardCharsets.UTF_8).replace("\r\n", "\n");
    }

    // ---------------------------------------------------------------
    // getGreeting: what run() shows before its loop
    // ---------------------------------------------------------------

    @Test
    public void getGreeting_noSavedData_greetingOnly() {
        assertEquals("Hello! I'm Myriad, your task painter." + NEWLINE + "What shall we paint on today's canvas?",
                sessionAtTempFile().getGreeting());
    }

    @Test
    public void getGreeting_savedDataWithBadLine_warningFollowsGreeting() throws IOException {
        // A load problem is reported after the greeting, never before it.
        writeDataFile("T | 0 | read book", "nonsense");
        String greeting = sessionAtTempFile().getGreeting();
        assertTrue(greeting.startsWith("Hello! I'm Myriad, your task painter."));
        assertTrue(greeting.contains("could not be loaded and were skipped"));
        assertTrue(greeting.contains("line 2"));
    }

    @Test
    public void getGreeting_unreadableDataFile_errorFollowsGreeting() throws IOException {
        // A directory where the data file should be cannot be read as a file
        // on any OS, so the whole load fails rather than skipping lines.
        Files.createDirectory(dataFile());
        String greeting = sessionAtTempFile().getGreeting();
        assertTrue(greeting.startsWith("Hello! I'm Myriad, your task painter."));
        assertTrue(greeting.contains("couldn't read your saved tasks"));
        assertFalse(greeting.contains("were skipped"));
    }

    @Test
    public void getResponse_unreadableDataFile_sessionStartsEmpty() throws IOException {
        Files.createDirectory(dataFile());
        assertEquals("Here's your canvas so far:", sessionAtTempFile().getResponse("list"));
    }

    @Test
    public void getResponse_savedDataWithBadLine_validTasksStillLoaded() throws IOException {
        writeDataFile("T | 0 | read book", "nonsense", "T | 1 | walk dog");
        assertEquals("Here's your canvas so far:" + NEWLINE
                + "1.[T][ ] read book" + NEWLINE
                + "2.[T][X] walk dog",
                sessionAtTempFile().getResponse("list"));
    }

    @Test
    public void hasLoadProblem_noSavedData_false() {
        // A missing file is a first run, not a problem.
        assertFalse(sessionAtTempFile().hasLoadProblem());
    }

    @Test
    public void hasLoadProblem_validSavedData_false() throws IOException {
        writeDataFile("T | 0 | read book");
        assertFalse(sessionAtTempFile().hasLoadProblem());
    }

    @Test
    public void hasLoadProblem_savedDataWithBadLine_true() throws IOException {
        writeDataFile("T | 0 | read book", "nonsense");
        assertTrue(sessionAtTempFile().hasLoadProblem());
    }

    @Test
    public void hasLoadProblem_unreadableDataFile_true() throws IOException {
        Files.createDirectory(dataFile());
        assertTrue(sessionAtTempFile().hasLoadProblem());
    }

    @Test
    public void getGreeting_calledTwice_notRepeatedWithinOneReply() {
        // Each call is its own reply, so the second must not carry the first.
        Myriad myriad = sessionAtTempFile();
        assertEquals(myriad.getGreeting(), myriad.getGreeting());
    }

    // ---------------------------------------------------------------
    // getResponse: one line in, one reply out
    // ---------------------------------------------------------------

    @Test
    public void getResponse_todoCommand_additionConfirmed() {
        assertEquals("A fresh stroke on the canvas:" + NEWLINE
                + "[T][ ] read book" + NEWLINE
                + "Your canvas now holds 1 task.",
                sessionAtTempFile().getResponse("todo read book"));
    }

    @Test
    public void getResponse_unrecognisedCommand_errorReturnedNotThrown() {
        // A GUI has nowhere to catch a MyriadException, so getResponse must
        // turn it into text the same way the console loop does.
        String response = sessionAtTempFile().getResponse("blah");
        assertEquals("Smudge! I don't recognize that command. Try: todo, deadline, event, list, "
                + "mark, unmark, delete, show, find, stats, or bye.", response);
    }

    @Test
    public void getResponse_deleteFromEmptyList_errorReturned() {
        // Failures raised while executing, not parsing, take the same path.
        assertTrue(sessionAtTempFile().getResponse("delete 1").startsWith("Smudge! "));
    }

    @Test
    public void getResponse_surroundingWhitespace_commandStillRecognised() {
        // The console strips input as it reads it; a text field does not, so
        // getResponse strips on the GUI's behalf.
        assertTrue(sessionAtTempFile().getResponse("   list   ")
                .startsWith("Here's your canvas so far:"));
    }

    @Test
    public void getResponse_secondCommand_firstReplyNotRepeated() {
        Myriad myriad = sessionAtTempFile();
        myriad.getResponse("todo read book");
        String second = myriad.getResponse("list");
        assertFalse(second.contains("A fresh stroke"));
        assertEquals("Here's your canvas so far:" + NEWLINE + "1.[T][ ] read book", second);
    }

    @Test
    public void getResponse_blankInput_errorReturned() {
        // The GUI filters blank lines before calling, but the seam itself
        // still has to answer rather than throw.
        assertTrue(sessionAtTempFile().getResponse("   ").startsWith("Smudge! "));
    }

    @Test
    public void getResponse_addThenList_taskVisibleInSameSession() {
        Myriad myriad = sessionAtTempFile();
        myriad.getResponse("todo read book");
        myriad.getResponse("mark 1");
        assertEquals("Here's your canvas so far:" + NEWLINE + "1.[T][X] read book",
                myriad.getResponse("list"));
    }

    @Test
    public void getResponse_addTask_writtenToDataFile() throws IOException {
        // A GUI session must save exactly as a console one does, since both
        // front ends share the one data file.
        sessionAtTempFile().getResponse("todo read book");
        assertEquals("T | 0 | read book\n", Files.readString(dataFile()));
    }

    // ---------------------------------------------------------------
    // Ending the session
    // ---------------------------------------------------------------

    @Test
    public void isExitRequested_beforeAnyCommand_false() {
        assertFalse(sessionAtTempFile().isExitRequested());
    }

    @Test
    public void getResponse_byeCommand_farewellReturned() {
        // run() prints the farewell after its loop because ExitCommand does
        // nothing; with no loop, getResponse has to produce it.
        assertEquals("Putting the brushes away. Come paint again soon!", sessionAtTempFile().getResponse("bye"));
    }

    @Test
    public void isExitRequested_afterByeCommand_true() {
        Myriad myriad = sessionAtTempFile();
        myriad.getResponse("bye");
        assertTrue(myriad.isExitRequested());
    }

    @Test
    public void isExitRequested_afterOrdinaryCommand_false() {
        Myriad myriad = sessionAtTempFile();
        myriad.getResponse("todo read book");
        assertFalse(myriad.isExitRequested());
    }

    // ---------------------------------------------------------------
    // Telling errors apart from ordinary replies
    // ---------------------------------------------------------------

    @Test
    public void isLastResponseError_beforeAnyCommand_false() {
        assertFalse(sessionAtTempFile().isLastResponseError());
    }

    @Test
    public void isLastResponseError_validCommand_false() {
        Myriad myriad = sessionAtTempFile();
        myriad.getResponse("todo read book");
        assertFalse(myriad.isLastResponseError());
    }

    @Test
    public void isLastResponseError_unrecognisedCommand_true() {
        Myriad myriad = sessionAtTempFile();
        myriad.getResponse("blah");
        assertTrue(myriad.isLastResponseError());
    }

    @Test
    public void isLastResponseError_executionFailure_true() {
        // An error raised while executing, not parsing, counts too.
        Myriad myriad = sessionAtTempFile();
        myriad.getResponse("delete 1");
        assertTrue(myriad.isLastResponseError());
    }

    @Test
    public void isLastResponseError_errorThenValidCommand_false() {
        // The flag describes only the latest reply, so it must not stick.
        Myriad myriad = sessionAtTempFile();
        myriad.getResponse("blah");
        myriad.getResponse("list");
        assertFalse(myriad.isLastResponseError());
    }

    // ---------------------------------------------------------------
    // run: the console session
    // ---------------------------------------------------------------

    @Test
    public void run_commandsThenBye_eachAnsweredThenFarewell() {
        String printed = runConsoleSession("todo read book\nblah\nbye\n");

        int greeting = printed.indexOf("Hello! I'm Myriad, your task painter.");
        int added = printed.indexOf("A fresh stroke on the canvas:");
        int error = printed.indexOf("Smudge! I don't recognize that command.");
        int farewell = printed.indexOf("Putting the brushes away. Come paint again soon!");
        assertTrue(greeting >= 0 && greeting < added && added < error && error < farewell, printed);
    }

    @Test
    public void run_linesAfterBye_ignored() throws IOException {
        // The loop stops at the exit command, so the todo after it never runs.
        runConsoleSession("todo read book\nbye\ntodo never added\n");
        assertEquals("T | 0 | read book\n", Files.readString(dataFile()));
    }

    @Test
    public void run_inputEndsWithoutBye_farewellStillShown() {
        // Piped input can simply run out; that ends the session like "bye" does.
        String printed = runConsoleSession("list\n");
        assertTrue(printed.endsWith("Putting the brushes away. Come paint again soon!\n"
                + "____________________________________________________________\n"), printed);
    }

    @Test
    public void run_byeWithSurroundingWhitespace_sessionEnds() throws IOException {
        // The console strips each line as it reads it, as getResponse does for the GUI.
        runConsoleSession("   bye   \ntodo never added\n");
        assertFalse(Files.exists(dataFile()));
    }

    @Test
    public void run_badSavedLine_warningPrintedAfterGreeting() throws IOException {
        writeDataFile("T | 0 | read book", "nonsense");
        String printed = runConsoleSession("bye\n");

        int greeting = printed.indexOf("Hello! I'm Myriad, your task painter.");
        int warning = printed.indexOf("could not be loaded and were skipped");
        assertTrue(greeting >= 0 && greeting < warning, printed);
    }
}
