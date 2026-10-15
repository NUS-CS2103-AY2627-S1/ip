package thomas;

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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests Thomas: getResponse with the two questions the GUI asks after it, and
 * run, the console session.
 * getResponse is the whole of the GUI's path through the chatbot: the window
 * parses nothing and runs nothing itself, it types a line in here and paints
 * whatever comes back. The three things worth pinning are that the reply is the
 * same wording the console gets, that a mistake comes back as a reply rather
 * than as an exception through the window, and that the two pieces of state
 * left behind -- the command type the dialog box colors by, and whether the
 * session is over -- say what just happened rather than what happened before it.
 * run is what the text-UI plan drives, and that plan remains where the wording
 * of each command is pinned. The cases for it here are the ones the plan cannot
 * set up: a save file that cannot be read or written, which needs a folder put
 * in its place. The shape of a session -- greeting, farewell owed once however
 * the input ends -- is pinned alongside, since the same harness is in hand.
 * Each case gets its own save file in a temporary folder, so no case sees
 * another's tasks.
 */
public class ThomasTest {

    /** The console greeting, as Ui.showWelcome prints it */
    private static final String GREETING = "    ____________________________________________________________\n"
            + "       ________                              \n"
            + "      /_  __/ /_  ____  ____ ___  ____ ______\n"
            + "       / / / __ \\/ __ \\/ __ `__ \\/ __ `/ ___/\n"
            + "      / / / / / / /_/ / / / / / / /_/ (__  ) \n"
            + "     /_/ /_/ /_/\\____/_/ /_/ /_/\\__,_/____/  \n"
            + "     Peep peep! Thomas the Tank Engine, reporting for duty!\n"
            + "     What shall we haul today?\n"
            + "    ____________________________________________________________\n";

    /** The console farewell, as Ui.showGoodbye prints it */
    private static final String FAREWELL = "    ____________________________________________________________\n"
            + "     Off to the sheds! Peep peep, see you down the line!\n"
            + "    ____________________________________________________________\n";

    /** One divider line, as printed above and below every block */
    private static final String DIVIDER = "    ____________________________________________________________\n";

    /** Folder JUnit makes fresh for each test and deletes afterwards */
    @TempDir
    private Path folder;

    /**
     * Returns a chatbot saving into this case's own folder.
     *
     * @return New chatbot, with an empty task list.
     */
    private Thomas chatbot() {
        return new Thomas(folder.resolve("tasklist.txt").toString());
    }

    /**
     * Writes a save file for this case to load, one line per argument.
     * The lines are taken as separate arguments rather than as one string with
     * newlines in it, so a case shows the file as the loader sees it -- a line
     * at a time -- and cannot say "two lines" while writing one. The same
     * helper, for the same reason, is in {@code StorageTest}.
     *
     * @param lines Save file lines, in order, without line separators.
     */
    private void writeSaveFile(String... lines) throws IOException {
        Files.write(folder.resolve("tasklist.txt"), List.of(lines));
    }

    /**
     * Runs a whole console session over this case's save file and returns what
     * it printed.
     * Standard input and output are both swapped for the duration and put back
     * afterwards whatever happens. The chatbot is built inside the swap, since
     * its Ui opens a Scanner over whatever System.in is at construction.
     *
     * @param input Everything the user types, one command per line.
     * @return The console output, exactly as printed.
     */
    private String runSession(String input) {
        InputStream originalIn = System.in;
        PrintStream originalOut = System.out;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
        System.setOut(new PrintStream(buffer, true, StandardCharsets.UTF_8));
        try {
            chatbot().run();
        } finally {
            System.setIn(originalIn);
            System.setOut(originalOut);
        }
        return buffer.toString(StandardCharsets.UTF_8);
    }

    @Test
    public void getStartupMessage_readableSaveFile_isGreetingAlone() {
        // The banner the console prints above this is deliberately not here: it
        // is ASCII art, and the window draws text in a proportional font.
        assertEquals("Peep peep! Thomas the Tank Engine, reporting for duty!\n"
                + "What shall we haul today?",
                chatbot().getStartupMessage());
    }

    @Test
    public void getStartupMessage_damagedSaveFileLine_warnsAfterGreeting() throws IOException {
        writeSaveFile("T | 0 | read book", "X | 0 | mystery");

        // The damaged line costs only itself: the greeting still arrives, the
        // warning follows it, and the readable task above it still loaded.
        Thomas thomas = chatbot();
        assertEquals("Peep peep! Thomas the Tank Engine, reporting for duty!\n"
                + "What shall we haul today?\n"
                + "Cinders and ashes! I left a saved line in the yard, I couldn't read it: "
                + "unknown task type 'X': X | 0 | mystery",
                thomas.getStartupMessage());
        assertEquals("Here is every wagon on my train:\n"
                + "1. [T][ ] read book",
                thomas.getResponse("list"));
    }

    @Test
    public void getResponse_todo_returnsSameConfirmationAsConsole() {
        Thomas thomas = chatbot();

        // The wording is Ui's, and the message carries its own lines only: the
        // dividers and indentation the console shows are added when printing.
        assertEquals("Coupled up! This wagon is on the train now:\n"
                + "   [T][ ] read book\n"
                + "That's 1 wagon(s) behind me now.",
                thomas.getResponse("todo read book"));
    }

    @Test
    public void getResponse_todo_recordsCommandType() {
        Thomas thomas = chatbot();
        thomas.getResponse("todo read book");

        // The simple name, since that is what DialogBox matches its style
        // classes against.
        assertEquals("AddCommand", thomas.getCommandType());
        assertFalse(thomas.hasExited());
    }

    @Test
    public void getResponse_unknownCommand_returnsErrorMessage() {
        Thomas thomas = chatbot();

        // A mistake is a reply like any other here. The console prints it
        // through Ui.showError instead, but the words are the same.
        assertEquals("Cinders and ashes! I don't know that signal. What does it mean?",
                thomas.getResponse("blah"));
    }

    @Test
    public void getResponse_todoTypedTwice_secondIsRefusedAndListUnchanged() {
        // A duplicate is refused on the way in, so the reply is the error and the count stays at one.
        Thomas thomas = chatbot();
        thomas.getResponse("todo read book");

        assertEquals("Bust my buffers! That wagon is already on my train, at number 1:\n   [T][ ] read book",
                thomas.getResponse("todo read book"));
        assertTrue(thomas.hasErrored());
        assertEquals("Here is every wagon on my train:\n1. [T][ ] read book", thomas.getResponse("list"));
    }

    @Test
    public void getResponse_unknownCommand_clearsCommandType() {
        Thomas thomas = chatbot();
        thomas.getResponse("todo read book");
        thomas.getResponse("blah");

        // No command ran, so the previous command's color must not be left
        // behind for the error bubble to pick up.
        assertEquals("", thomas.getCommandType());
    }

    @Test
    public void getResponse_bye_reportsSessionDone() {
        Thomas thomas = chatbot();

        assertEquals("Off to the sheds! Peep peep, see you down the line!", thomas.getResponse("bye"));
        assertTrue(thomas.hasExited());
    }

    @Test
    public void hasExited_beforeAnyCommand_isFalse() {
        assertFalse(chatbot().hasExited());
    }

    @Test
    public void hasErrored_beforeAnyCommand_isFalse() {
        // The greeting is not a rejection, so it must not get the error bubble.
        assertFalse(chatbot().hasErrored());
    }

    @Test
    public void hasErrored_unknownCommand_isTrue() {
        Thomas thomas = chatbot();
        thomas.getResponse("blah");

        assertTrue(thomas.hasErrored());
    }

    @Test
    public void hasErrored_validCommandAfterError_isCleared() {
        Thomas thomas = chatbot();
        thomas.getResponse("blah");
        thomas.getResponse("todo read book");

        // One rejection must not leave every later reply in the error bubble.
        assertFalse(thomas.hasErrored());
    }

    @Test
    public void getCommandType_beforeAnyCommand_isEmpty() {
        // Not null: DialogBox switches on this value, and a null would close
        // the window with an exception before the first bubble is drawn.
        assertEquals("", chatbot().getCommandType());
    }

    @Test
    public void getResponse_deleteMissingTask_returnsErrorAndClearsCommandType() {
        Thomas thomas = chatbot();
        thomas.getResponse("todo read book");

        // A command that parses but cannot be carried out fails the same way an
        // unparseable one does, which is what keeps the window free of error
        // handling.
        assertEquals("There's no wagon 5 on my train! I'm only pulling 1 wagon(s).",
                thomas.getResponse("delete 5"));
        assertEquals("", thomas.getCommandType());
    }

    @Test
    public void getResponse_undoBeforeAnyChange_returnsErrorMessage() {
        // A fresh chatbot has nothing to undo whatever the save file held: the
        // history is built with the session, not loaded with the tasks.
        assertEquals("I can't reverse any further! There's nothing to undo.", chatbot().getResponse("undo"));
    }

    @Test
    public void getResponse_undoAfterLoadingTasks_returnsErrorMessage() throws IOException {
        writeSaveFile("T | 0 | read book");

        // Loading is not a change the user made, so it is not one to take back.
        assertEquals("I can't reverse any further! There's nothing to undo.", chatbot().getResponse("undo"));
    }

    @Test
    public void getResponse_undoAfterTodo_quotesTheLineAndReportsTheCount() {
        Thomas thomas = chatbot();
        thomas.getResponse("todo read book");

        assertEquals("Reversing! I've backed out of 'todo read book'.\n"
                + "That's 0 wagon(s) behind me now.", thomas.getResponse("undo"));
    }

    @Test
    public void getResponse_undoAfterTodo_taskIsGoneFromTheList() {
        Thomas thomas = chatbot();
        thomas.getResponse("todo read book");

        thomas.getResponse("undo");

        assertEquals("Here is every wagon on my train:", thomas.getResponse("list"));
    }

    @Test
    public void getResponse_undo_recordsCommandType() {
        Thomas thomas = chatbot();
        thomas.getResponse("todo read book");

        thomas.getResponse("undo");

        // UndoCommand earns no colour of its own in DialogBox, which reaches its
        // default branch on this name rather than on an empty string.
        assertEquals("UndoCommand", thomas.getCommandType());
    }

    @Test
    public void getResponse_undoWithNothingToUndo_clearsCommandType() {
        Thomas thomas = chatbot();
        thomas.getResponse("todo read book");

        thomas.getResponse("undo");
        thomas.getResponse("undo");

        // The second undo failed, so no command ran and there is nothing to
        // colour by, exactly as for any other rejected command.
        assertEquals("", thomas.getCommandType());
    }

    @Test
    public void getResponse_undoTwice_walksBackTwoChanges() {
        Thomas thomas = chatbot();
        thomas.getResponse("todo read book");
        thomas.getResponse("todo buy milk");

        assertEquals("Reversing! I've backed out of 'todo buy milk'.\n"
                + "That's 1 wagon(s) behind me now.", thomas.getResponse("undo"));
        assertEquals("Reversing! I've backed out of 'todo read book'.\n"
                + "That's 0 wagon(s) behind me now.", thomas.getResponse("undo"));
    }

    @Test
    public void getResponse_undoAfterARejectedCommand_reachesTheChangeBeforeIt() {
        Thomas thomas = chatbot();
        thomas.getResponse("todo read book");
        thomas.getResponse("delete 99");

        // The rejected command changed nothing, so it recorded nothing and did
        // not become the change that the next undo takes back.
        assertEquals("Reversing! I've backed out of 'todo read book'.\n"
                + "That's 0 wagon(s) behind me now.", thomas.getResponse("undo"));
    }

    @Test
    public void getResponse_undoAfterReadOnlyCommands_reachesTheLastChange() {
        Thomas thomas = chatbot();
        thomas.getResponse("todo read book");
        thomas.getResponse("list");
        thomas.getResponse("find read");

        assertEquals("Reversing! I've backed out of 'todo read book'.\n"
                + "That's 0 wagon(s) behind me now.", thomas.getResponse("undo"));
    }

    @Test
    public void getResponse_undoOfANoOpMark_leavesTheTaskDone() {
        Thomas thomas = chatbot();
        thomas.getResponse("todo read book");
        thomas.getResponse("mark 1");

        thomas.getResponse("mark 1");
        thomas.getResponse("undo");

        // The second mark changed nothing, so undoing it must not clear the tick
        // the first one set.
        assertEquals("Here is every wagon on my train:\n1. [T][X] read book",
                thomas.getResponse("list"));
    }

    @Test
    public void getResponse_undo_writesTheRestoredListToTheSaveFile() throws IOException {
        Thomas thomas = chatbot();
        thomas.getResponse("todo read book");
        thomas.getResponse("todo buy milk");

        thomas.getResponse("undo");

        // Saved, not only put back in memory: every command that changed the list
        // wrote as it went, so a list restored in memory alone would be the old
        // one again on the next run.
        assertEquals(List.of("T | 0 | read book"),
                Files.readAllLines(folder.resolve("tasklist.txt")));
    }

    // ---- the command type each command reports ----

    @Test
    public void getResponse_eachCommand_recordsItsOwnType() {
        Thomas thomas = chatbot();
        thomas.getResponse("todo read book");
        thomas.getResponse("deadline return book /by 2019-12-02 1800");

        // One list per command kind, since DialogBox colors by these names and
        // a renamed class would silently lose its bubble color.
        thomas.getResponse("mark 1");
        assertEquals("MarkCommand", thomas.getCommandType());
        thomas.getResponse("unmark 1");
        assertEquals("UnmarkCommand", thomas.getCommandType());
        thomas.getResponse("list");
        assertEquals("ListCommand", thomas.getCommandType());
        thomas.getResponse("find book");
        assertEquals("FindCommand", thomas.getCommandType());
        thomas.getResponse("on 2019-12-02");
        assertEquals("OnCommand", thomas.getCommandType());
        thomas.getResponse("delete 2");
        assertEquals("DeleteCommand", thomas.getCommandType());
        thomas.getResponse("bye");
        assertEquals("ExitCommand", thomas.getCommandType());
    }

    @Test
    public void getResponse_commandAfterBye_hasExitedIsCleared() {
        // The window closes after bye, so this cannot happen from the GUI; it
        // pins that the flag says what the last command was, not whether a bye
        // has ever been typed.
        Thomas thomas = chatbot();
        thomas.getResponse("bye");
        thomas.getResponse("list");

        assertFalse(thomas.hasExited());
    }

    // ---- a save file that cannot be read or written ----

    @Test
    public void getStartupMessage_saveFileIsAFolder_warnsItCouldNotBeRead() throws IOException {
        Files.createDirectory(folder.resolve("tasklist.txt"));

        assertEquals("Peep peep! Thomas the Tank Engine, reporting for duty!\n"
                + "What shall we haul today?\n"
                + "Cinders and ashes! I couldn't read your saved tasks: '" + folder.resolve("tasklist.txt")
                + "' is a folder, and I need it to be a file. Move the folder out of the way.\n"
                + "Setting off with an empty train.",
                chatbot().getStartupMessage());
    }

    @Test
    public void getResponse_saveFileIsAFolder_warningPrecedesTheReply() throws IOException {
        Files.createDirectory(folder.resolve("tasklist.txt"));
        Thomas thomas = chatbot();

        // The task is kept for the session and the reply is not an error: the
        // command ran, and only the save failed.
        assertEquals("Cinders and ashes! I couldn't save your tasks: '" + folder.resolve("tasklist.txt")
                + "' is a folder, and I need it to be a file. Move the folder out of the way.\n"
                + "Coupled up! This wagon is on the train now:\n"
                + "   [T][ ] read book\n"
                + "That's 1 wagon(s) behind me now.",
                thomas.getResponse("todo read book"));
        assertFalse(thomas.hasErrored());
        assertEquals("AddCommand", thomas.getCommandType());
        assertEquals("Here is every wagon on my train:\n1. [T][ ] read book", thomas.getResponse("list"));
    }

    // ---- the console session ----

    @Test
    public void run_byeTyped_printsGreetingThenFarewellOnce() {
        // ExitCommand says the farewell as its reply, so run must not say it
        // again on the way out.
        assertEquals(GREETING + FAREWELL, runSession("bye\n"));
    }

    @Test
    public void run_inputRunsOut_farewellStillPrinted() {
        // A piped session ends without a bye; the farewell is owed all the same.
        assertEquals(GREETING + FAREWELL, runSession(""));
    }

    @Test
    public void run_commandThenInputRunsOut_replyThenFarewell() {
        assertEquals(GREETING
                + DIVIDER + "     Here is every wagon on my train:\n" + DIVIDER
                + FAREWELL,
                runSession("list\n"));
    }

    @Test
    public void run_commandsAfterBye_areNotRead() {
        // bye ends the loop even with lines left, so nothing after it is answered.
        assertEquals(GREETING + FAREWELL, runSession("bye\nlist\n"));
    }

    @Test
    public void run_rejectedLine_errorIsABlockAndTheSessionContinues() {
        assertEquals(GREETING
                + DIVIDER + "     Cinders and ashes! I don't know that signal. What does it mean?\n" + DIVIDER
                + DIVIDER + "     Here is every wagon on my train:\n" + DIVIDER
                + FAREWELL,
                runSession("blah\nlist\n"));
    }

    @Test
    public void run_multiLineError_everyLineIndented() {
        // The duplicate refusal's second line goes through showError's split,
        // so it gets the indent rather than sitting flush left.
        String added = DIVIDER + "     Coupled up! This wagon is on the train now:\n"
                + "        [T][ ] read book\n"
                + "     That's 1 wagon(s) behind me now.\n" + DIVIDER;
        assertEquals(GREETING + added
                + DIVIDER + "     Bust my buffers! That wagon is already on my train, at number 1:\n"
                + "        [T][ ] read book\n" + DIVIDER
                + FAREWELL,
                runSession("todo read book\ntodo read book\n"));
    }

    @Test
    public void run_saveFileIsAFolder_warnsAfterGreetingAndCarriesOn() throws IOException {
        Files.createDirectory(folder.resolve("tasklist.txt"));

        // The complaint is its own block after the greeting, and the session
        // goes on with an empty list rather than stopping.
        assertEquals(GREETING
                + DIVIDER + "     Cinders and ashes! I couldn't read your saved tasks: '"
                + folder.resolve("tasklist.txt")
                + "' is a folder, and I need it to be a file. Move the folder out of the way.\n"
                + "     Setting off with an empty train.\n" + DIVIDER
                + DIVIDER + "     Here is every wagon on my train:\n" + DIVIDER
                + FAREWELL,
                runSession("list\n"));
    }

    @Test
    public void run_damagedSaveFileLines_eachWarnedInItsOwnBlock() throws IOException {
        writeSaveFile("X | 0 | mystery", "T | 0 | read book", "T | 0");

        assertEquals(GREETING
                + DIVIDER + "     Cinders and ashes! I left a saved line in the yard, I couldn't read it: "
                + "unknown task type 'X': X | 0 | mystery\n" + DIVIDER
                + DIVIDER + "     Cinders and ashes! I left a saved line in the yard, I couldn't read it: "
                + "too few fields: T | 0\n" + DIVIDER
                + DIVIDER + "     Here is every wagon on my train:\n"
                + "     1. [T][ ] read book\n" + DIVIDER
                + FAREWELL,
                runSession("list\n"));
    }

    @Test
    public void run_saveFileIsAFolder_saveWarningPrintedInTheReplyBlock() throws IOException {
        Files.createDirectory(folder.resolve("tasklist.txt"));
        String loadWarning = DIVIDER + "     Cinders and ashes! I couldn't read your saved tasks: '"
                + folder.resolve("tasklist.txt")
                + "' is a folder, and I need it to be a file. Move the folder out of the way.\n"
                + "     Setting off with an empty train.\n" + DIVIDER;

        // The warning ends in a newline, so the confirmation follows it inside
        // one block rather than in a block of its own.
        assertEquals(GREETING + loadWarning
                + DIVIDER + "     Cinders and ashes! I couldn't save your tasks: '"
                + folder.resolve("tasklist.txt")
                + "' is a folder, and I need it to be a file. Move the folder out of the way.\n"
                + "     Coupled up! This wagon is on the train now:\n"
                + "        [T][ ] read book\n"
                + "     That's 1 wagon(s) behind me now.\n" + DIVIDER
                + FAREWELL,
                runSession("todo read book\n"));
    }
}
