package steph;

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
 * Unit tests for {@link Steph#getResponse(String)} and the small pieces of
 * state the GUI reads alongside it ({@link Steph#getCommandType()},
 * {@link Steph#isLastResponseError()}, and {@link Steph#isExit()}).
 *
 * <p>{@code getResponse} is the single entry point both front ends funnel
 * through: it parses a line, mutates the task list, saves, and returns the
 * reply text. The branching that matters here is which command ran (so the
 * GUI can tint the reply), whether a failed command clears that and flags it
 * as an error, whether "bye" flips the exit flag, and whether a mutating
 * command is actually persisted. Each test gets its own {@link TempDir} save
 * file so nothing touches the real {@code ./data/steph.txt}.
 */
public class StephTest {

    @TempDir
    private Path tempDir;

    private Steph newSteph() {
        return new Steph(tempDir.resolve("steph.txt").toString());
    }

    @Test
    public void getResponse_todoCommand_addsTaskAndTracksCommandType() {
        Steph steph = newSteph();

        String response = steph.getResponse("todo buy milk");

        assertEquals("Got it. I've added this task:\n  [T][ ] buy milk\n"
                + "Now you have 1 tasks in the list.", response);
        assertEquals("TODO", steph.getCommandType());
        assertFalse(steph.isLastResponseError());
    }

    @Test
    public void getResponse_unknownCommand_returnsErrorAndClearsCommandType() {
        Steph steph = newSteph();
        steph.getResponse("todo buy milk"); // leaves commandType == "TODO"

        String response = steph.getResponse("sing a song");

        assertEquals("Hmm.. I don't understand that command: \"sing\".", response);
        assertEquals("", steph.getCommandType());
        assertTrue(steph.isLastResponseError());
    }

    @Test
    public void getResponse_markCommand_marksTaskAndTracksCommandType() {
        Steph steph = newSteph();
        steph.getResponse("todo borrow book");

        String response = steph.getResponse("mark 1");

        assertEquals("Awesome! I've marked this task as done:\n  [T][X] borrow book", response);
        assertEquals("MARK", steph.getCommandType());
    }

    @Test
    public void getResponse_deleteCommand_removesTaskAndTracksCommandType() {
        Steph steph = newSteph();
        steph.getResponse("todo one");
        steph.getResponse("todo two");

        String response = steph.getResponse("delete 1");

        assertEquals("Okay! I've removed this task:\n  [T][ ] one\n"
                + "Now you have 1 tasks in the list.", response);
        assertEquals("DELETE", steph.getCommandType());
    }

    @Test
    public void getResponse_listCommand_tracksCommandType() {
        Steph steph = newSteph();
        steph.getResponse("todo read book");

        String response = steph.getResponse("list");

        assertEquals("Here are the tasks in your list:\n1.[T][ ] read book", response);
        assertEquals("LIST", steph.getCommandType());
        assertFalse(steph.isLastResponseError());
    }

    @Test
    public void getResponse_bye_setsExitFlagAndReturnsFarewell() {
        Steph steph = newSteph();
        assertFalse(steph.isExit());

        String response = steph.getResponse("bye");

        assertEquals("Goodbye. Hope to see you again soon!", response);
        assertTrue(steph.isExit());
        assertEquals("", steph.getCommandType());
    }

    @Test
    public void getResponse_mutatingCommand_isPersistedForTheNextSession() {
        newSteph().getResponse("todo persist me");

        // A fresh Steph on the same save file should load what the first one wrote.
        String response = newSteph().getResponse("list");

        assertEquals("Here are the tasks in your list:\n1.[T][ ] persist me", response);
    }

    @Test
    public void getResponse_eventClashesWithExistingPendingEvent_repliesWithClashMessage() {
        Steph steph = newSteph();
        steph.getResponse("event trip /from 2019-10-15 /to 2019-10-15");

        String response = steph.getResponse("event conflict /from 2019-10-15 /to 2019-10-15");

        assertEquals("This clashes with an existing event:\n"
                + "  [E][ ] trip (from: Oct 15 2019 to: Oct 15 2019)\n"
                + "Add '/force' to the command if you want to schedule it anyway.", response);
        assertEquals("", steph.getCommandType());
        assertTrue(steph.isLastResponseError());
    }

    @Test
    public void getResponse_blockedEventAdd_notPersistedAndTaskCountUnchanged() {
        newSteph().getResponse("event trip /from 2019-10-15 /to 2019-10-15");
        Steph steph = newSteph();
        steph.getResponse("event conflict /from 2019-10-15 /to 2019-10-15");

        String response = steph.getResponse("list");

        assertEquals("Here are the tasks in your list:\n"
                + "1.[E][ ] trip (from: Oct 15 2019 to: Oct 15 2019)", response);
    }

    @Test
    public void getResponse_eventWithTrailingForceFlag_bypassesClashCheckAndAdds() {
        Steph steph = newSteph();
        steph.getResponse("event trip /from 2019-10-15 /to 2019-10-15");

        String response = steph.getResponse("event conflict /from 2019-10-15 /to 2019-10-15 /force");

        assertEquals("Got it. I've added this task:\n"
                + "  [E][ ] conflict (from: Oct 15 2019 to: Oct 15 2019)\n"
                + "Now you have 2 tasks in the list.", response);
        assertEquals("EVENT", steph.getCommandType());
    }

    @Test
    public void getResponse_unmarkCommand_revertsTaskAndTracksCommandType() {
        Steph steph = newSteph();
        steph.getResponse("todo borrow book");
        steph.getResponse("mark 1");

        String response = steph.getResponse("unmark 1");

        assertEquals("OK, I've marked this task as not done yet:\n  [T][ ] borrow book", response);
        assertEquals("UNMARK", steph.getCommandType());
        assertFalse(steph.isLastResponseError());
    }

    @Test
    public void getResponse_markWithIndexOutOfRange_returnsError() {
        Steph steph = newSteph();
        steph.getResponse("todo one");

        String response = steph.getResponse("mark 5");

        assertTrue(response.contains("Please type \"mark <task-number>\""), response);
        assertTrue(steph.isLastResponseError());
        assertEquals("", steph.getCommandType());
    }

    @Test
    public void getResponse_unmarkAndDeleteWithBadIndex_returnErrorsQuotingTheCommand() {
        Steph steph = newSteph();

        assertTrue(steph.getResponse("unmark abc").contains("\"unmark <task-number>\""));
        assertTrue(steph.getResponse("delete 1").contains("\"delete <task-number>\""));
        assertTrue(steph.isLastResponseError());
    }

    @Test
    public void getResponse_deadlineCommand_addsDeadline() {
        Steph steph = newSteph();

        String response = steph.getResponse("deadline return book /by 2019-10-15 1800");

        // AM/PM marker case varies by JDK locale data, so compare lower-cased.
        assertEquals("got it. i've added this task:\n  [d][ ] return book (by: oct 15 2019, 6:00pm)\n"
                + "now you have 1 tasks in the list.", response.toLowerCase());
        assertEquals("DEADLINE", steph.getCommandType());
    }

    @Test
    public void getResponse_deadlineWithoutBy_returnsUsageError() {
        Steph steph = newSteph();

        String response = steph.getResponse("deadline return book");

        assertTrue(response.contains("deadline <task-name> /by"), response);
        assertTrue(steph.isLastResponseError());
    }

    @Test
    public void getResponse_todoWithoutName_returnsUsageError() {
        Steph steph = newSteph();

        assertTrue(steph.getResponse("todo").contains("todo <task-name>"));
        assertTrue(steph.isLastResponseError());
    }

    @Test
    public void getResponse_eventCommand_addsEventAndTracksCommandType() {
        Steph steph = newSteph();

        String response = steph.getResponse("event camp /from 2019-10-15 /to 2019-10-16");

        assertEquals("Got it. I've added this task:\n  [E][ ] camp (from: Oct 15 2019 to: Oct 16 2019)\n"
                + "Now you have 1 tasks in the list.", response);
        assertEquals("EVENT", steph.getCommandType());
    }

    @Test
    public void getResponse_findWithMatches_listsMatchesRenumbered() {
        Steph steph = newSteph();
        steph.getResponse("todo read book");
        steph.getResponse("todo buy milk");
        steph.getResponse("todo return BOOK");

        String response = steph.getResponse("find book");

        assertEquals("Here are the matching tasks in your list:\n1.[T][ ] read book\n2.[T][ ] return BOOK",
                response);
        assertEquals("FIND", steph.getCommandType());
        assertFalse(steph.isLastResponseError());
    }

    @Test
    public void getResponse_findWithNoMatches_saysNothingFound() {
        Steph steph = newSteph();
        steph.getResponse("todo read book");

        assertEquals("I couldn't find any tasks matching \"zzz\".", steph.getResponse("find zzz"));
    }

    @Test
    public void getResponse_findWithoutKeyword_returnsUsageError() {
        Steph steph = newSteph();

        assertTrue(steph.getResponse("find").contains("find <keyword>"));
        assertTrue(steph.isLastResponseError());
    }

    @Test
    public void getResponse_emptyInput_returnsErrorNotACrash() {
        Steph steph = newSteph();

        String response = steph.getResponse("   ");

        assertTrue(response.startsWith("Hmm.. I don't understand that command"), response);
        assertTrue(steph.isLastResponseError());
    }

    @Test
    public void getResponse_surroundingWhitespace_isIgnored() {
        Steph steph = newSteph();

        assertEquals("Here are the tasks in your list:", steph.getResponse("  list  "));
        assertEquals("LIST", steph.getCommandType());
    }

    @Test
    public void getResponse_byeWithSurroundingWhitespace_setsExitFlag() {
        Steph steph = newSteph();

        steph.getResponse(" bye ");

        assertTrue(steph.isExit());
    }

    @Test
    public void getResponse_successAfterError_clearsErrorFlag() {
        Steph steph = newSteph();
        steph.getResponse("nonsense");
        assertTrue(steph.isLastResponseError());

        steph.getResponse("list");

        assertFalse(steph.isLastResponseError());
    }

    @Test
    public void getResponse_markDeleteAndUnmark_arePersisted() {
        Steph first = newSteph();
        first.getResponse("todo one");
        first.getResponse("todo two");
        first.getResponse("todo three");
        first.getResponse("mark 1");
        first.getResponse("mark 2");
        first.getResponse("unmark 2");
        first.getResponse("delete 3");

        String response = newSteph().getResponse("list");

        assertEquals("Here are the tasks in your list:\n1.[T][X] one\n2.[T][ ] two", response);
    }

    @Test
    public void getResponse_listAndFind_doNotCreateTheSaveFile() {
        Steph steph = newSteph();

        steph.getResponse("list");
        steph.getResponse("find x");

        assertFalse(Files.exists(tempDir.resolve("steph.txt")));
    }

    @Test
    public void getResponse_saveFails_repliesWithSaveErrorAndFlagsError() throws IOException {
        // The save file's parent is a regular file, so Storage cannot create it.
        Path blocker = tempDir.resolve("blocker");
        Files.writeString(blocker, "not a directory");
        Steph steph = new Steph(blocker.resolve("steph.txt").toString());

        String response = steph.getResponse("todo read");

        assertTrue(response.startsWith("Sorry, I couldn't save your tasks: "), response);
        assertTrue(steph.isLastResponseError());
        assertEquals("", steph.getCommandType());
    }

    @Test
    public void constructor_saveFileUnreadable_startsEmptyAndShowsLoadingError() throws IOException {
        // A directory where the file should be makes Files.readAllLines throw an IOException.
        Path directoryInPlaceOfFile = tempDir.resolve("steph.txt");
        Files.createDirectory(directoryInPlaceOfFile);
        PrintStream originalOut = System.out;
        ByteArrayOutputStream captured = new ByteArrayOutputStream();
        System.setOut(new PrintStream(captured, true, StandardCharsets.UTF_8));
        Steph steph;
        try {
            steph = new Steph(directoryInPlaceOfFile.toString());
        } finally {
            System.setOut(originalOut);
        }

        assertTrue(captured.toString(StandardCharsets.UTF_8).contains("couldn't read the save file"));
        assertEquals("Here are the tasks in your list:", steph.getResponse("list"));
    }

    @Test
    public void getWelcomeMessage_returnsGreeting() {
        assertEquals("Hello! I'm Steph.\nGlad to see you!\nWhat can I help you with?", Steph.getWelcomeMessage());
    }

    @Test
    public void run_commandsThenBye_printsRepliesAndGoodbyeAndStopsReading() {
        String out = runWithInput("todo a\nlist\nbye\ntodo never-read\n");

        assertTrue(out.contains("Hello! I'm Steph."), out);
        assertTrue(out.contains("Got it. I've added this task:\n  [T][ ] a"), out);
        assertTrue(out.contains("Here are the tasks in your list:\n1.[T][ ] a"), out);
        assertTrue(out.contains("Goodbye. Hope to see you again soon!"), out);
        assertFalse(out.contains("never-read"), out);
    }

    @Test
    public void run_inputEndsWithoutBye_stillSaysGoodbye() {
        String out = runWithInput("list\n");

        assertTrue(out.contains("Here are the tasks in your list:"), out);
        assertTrue(out.trim().endsWith("Goodbye. Hope to see you again soon!\n"
                + "____________________________________________________________"), out);
    }

    /** Runs the console loop with {@code input} as System.in and returns everything it printed. */
    private String runWithInput(String input) {
        InputStream originalIn = System.in;
        PrintStream originalOut = System.out;
        ByteArrayOutputStream captured = new ByteArrayOutputStream();
        try {
            // Steph's Ui builds its Scanner in the constructor, so System.in must be swapped first.
            System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
            System.setOut(new PrintStream(captured, true, StandardCharsets.UTF_8));
            newSteph().run();
        } finally {
            System.setIn(originalIn);
            System.setOut(originalOut);
        }
        return captured.toString(StandardCharsets.UTF_8).replace("\r\n", "\n");
    }
}
