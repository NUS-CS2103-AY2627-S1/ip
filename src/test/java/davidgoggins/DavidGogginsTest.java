package davidgoggins;

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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Tests {@link DavidGoggins} end to end, through the same methods the two UIs call.
 *
 * <p>Most tests go through {@link DavidGoggins#getResponse(String)}, which the GUI uses:
 * it returns the reply as text, so no window is needed. Each chatbot saves into its own
 * temporary folder, so tests never share tasks or touch the real save file.
 */
public class DavidGogginsTest {

    /** A new, empty folder for each test, created and deleted by JUnit. */
    @TempDir
    Path tempDir;

    private final ByteArrayOutputStream printed = new ByteArrayOutputStream();

    private PrintStream originalOut;

    private InputStream originalIn;

    @BeforeEach
    public void redirectConsole() {
        originalOut = System.out;
        originalIn = System.in;
        System.setOut(new PrintStream(printed, true, StandardCharsets.UTF_8));
    }

    @AfterEach
    public void restoreConsole() {
        System.setOut(originalOut);
        System.setIn(originalIn);
    }

    private Path saveFile() {
        return tempDir.resolve("tasks.txt");
    }

    /** Returns a chatbot that keeps its tasks in this test's temporary folder. */
    private DavidGoggins newChatbot() {
        return new DavidGoggins(saveFile().toString());
    }

    /** Sends each input in turn and returns the reply to the last one. */
    private static String replyAfter(DavidGoggins chatbot, String... inputs) {
        String reply = "";
        for (String input : inputs) {
            reply = chatbot.getResponse(input);
        }
        return reply;
    }

    @Test
    public void getResponse_validTodo_confirmedWithoutError() {
        DavidGoggins chatbot = newChatbot();
        String reply = chatbot.getResponse("  todo read book  ");
        assertTrue(reply.contains("[T][ ] read book"), reply);
        assertTrue(reply.contains("You have 1 task in the list."), reply);
        assertFalse(chatbot.isLastResponseError());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "bogus", "todo", "mark", "mark two", "delete 1", "list all",
        "help me", "bye now", "find", "deadline x", "event x"})
    public void getResponse_badInput_flaggedAsError(String input) {
        DavidGoggins chatbot = newChatbot();
        chatbot.getResponse(input);
        assertTrue(chatbot.isLastResponseError(), input);
    }

    /** The error flag describes only the last reply, so it must reset on success. */
    @Test
    public void isLastResponseError_errorThenValidCommand_false() {
        DavidGoggins chatbot = newChatbot();
        chatbot.getResponse("bogus");
        chatbot.getResponse("list");
        assertFalse(chatbot.isLastResponseError());
    }

    @Test
    public void getResponse_unknownCommand_pointsToHelp() {
        String reply = newChatbot().getResponse("dance");
        assertTrue(reply.contains("I don't know the command \"dance\""), reply);
        assertTrue(reply.contains("Type help"), reply);
    }

    @Test
    public void getResponse_emptyList_emptyListMessage() {
        String reply = newChatbot().getResponse("list");
        assertTrue(reply.contains("Your list is empty"), reply);
    }

    @Test
    public void getResponse_listWithNothingDone_startCallout() {
        String reply = replyAfter(newChatbot(), "todo read book", "todo run", "list");
        assertTrue(reply.contains("1.[T][ ] read book"), reply);
        assertTrue(reply.contains("0 of 2 done. Stop planning and start doing."), reply);
    }

    @Test
    public void getResponse_listWithSomeDone_keepGoingCallout() {
        String reply = replyAfter(newChatbot(), "todo read book", "todo run", "mark 2", "list");
        assertTrue(reply.contains("1 of 2 done. You're not finished."), reply);
    }

    @Test
    public void getResponse_listWithAllDone_newChallengeCallout() {
        String reply = replyAfter(newChatbot(), "todo read book", "mark 1", "list");
        assertTrue(reply.contains("All 1 done. Now go find something harder."), reply);
    }

    @Test
    public void getResponse_markThenUnmark_statusToggled() {
        DavidGoggins chatbot = newChatbot();
        String marked = replyAfter(chatbot, "todo read book", "mark 1");
        String unmarked = chatbot.getResponse("unmark 1");
        assertTrue(marked.contains("[T][X] read book"), marked);
        assertTrue(unmarked.contains("[T][ ] read book"), unmarked);
    }

    @Test
    public void getResponse_markAlreadyDoneTask_warningShown() {
        DavidGoggins chatbot = newChatbot();
        String reply = replyAfter(chatbot, "todo read book", "mark 1", "mark 1");
        assertTrue(reply.contains("Task 1 is already done"), reply);
        assertTrue(chatbot.isLastResponseWarning());
        assertFalse(chatbot.isLastResponseError());
    }

    @Test
    public void getResponse_unmarkNotDoneTask_warningShown() {
        DavidGoggins chatbot = newChatbot();
        String reply = replyAfter(chatbot, "todo read book", "unmark 1");
        assertTrue(reply.contains("Task 1 is already not done"), reply);
        assertTrue(chatbot.isLastResponseWarning());
    }

    @Test
    public void isLastResponseWarning_warningThenValidCommand_false() {
        DavidGoggins chatbot = newChatbot();
        replyAfter(chatbot, "todo read book", "unmark 1", "list");
        assertFalse(chatbot.isLastResponseWarning());
    }

    @Test
    public void getResponse_markWithoutNumber_asksWhichTask() {
        String reply = newChatbot().getResponse("unmark");
        assertTrue(reply.contains("Which task? Give me the number, e.g. unmark 2."), reply);
    }

    @Test
    public void getResponse_numberPastEndOfList_validRangeSuggested() {
        String reply = replyAfter(newChatbot(), "todo read book", "mark 5");
        assertTrue(reply.contains("pick a number from 1 to 1"), reply);
    }

    @Test
    public void getResponse_numberOnEmptyList_addTaskFirstSuggested() {
        String reply = newChatbot().getResponse("delete 1");
        assertTrue(reply.contains("your list is empty, so add a task first"), reply);
    }

    @Test
    public void getResponse_deleteWithoutNumber_asksWhichTask() {
        String reply = newChatbot().getResponse("delete");
        assertTrue(reply.contains("Which task?"), reply);
    }

    @Test
    public void getResponse_delete_taskRemovedAndCountShown() {
        String reply = replyAfter(newChatbot(), "todo read book", "todo run", "delete 1");
        assertTrue(reply.contains("[T][ ] read book"), reply);
        assertTrue(reply.contains("You have 1 task in the list."), reply);
    }

    @Test
    public void getResponse_findWithMatches_matchesNumberedFromOne() {
        String reply = replyAfter(newChatbot(), "todo run", "todo read book", "find BOOK");
        assertTrue(reply.contains("Here are the matching tasks"), reply);
        assertTrue(reply.contains("1.[T][ ] read book"), reply);
    }

    @Test
    public void getResponse_findWithoutMatches_noMatchMessage() {
        String reply = replyAfter(newChatbot(), "todo run", "find swim");
        assertTrue(reply.contains("No tasks match \"swim\""), reply);
    }

    @Test
    public void getResponse_duplicateTask_refusedAndListUnchanged() {
        DavidGoggins chatbot = newChatbot();
        String reply = replyAfter(chatbot, "deadline return book /by 2026-09-10",
                "deadline Return  Book /by 2026-09-10");
        assertTrue(chatbot.isLastResponseError());
        assertTrue(reply.contains("You already logged that as task 1"), reply);
        assertTrue(chatbot.getResponse("list").contains("0 of 1 done"));
    }

    @Test
    public void getResponse_separatorInTask_refused() {
        String reply = newChatbot().getResponse("todo read | write");
        assertTrue(reply.contains("cannot contain the \"|\" character"), reply);
    }

    @Test
    public void getResponse_bye_farewellWithoutError() {
        DavidGoggins chatbot = newChatbot();
        String reply = chatbot.getResponse("BYE");
        assertTrue(reply.contains("Stay hard!"), reply);
        assertFalse(chatbot.isLastResponseError());
    }

    @Test
    public void getResponse_help_helpWindowConfirmed() {
        String reply = newChatbot().getResponse("help");
        assertTrue(reply.contains("help window is open"), reply);
    }

    @Test
    public void isExitCommand_variousInputs_onlyBareByeAccepted() {
        DavidGoggins chatbot = newChatbot();
        assertTrue(chatbot.isExitCommand("  Bye "));
        assertFalse(chatbot.isExitCommand("bye now"));
        assertFalse(chatbot.isExitCommand("goodbye"));
    }

    @Test
    public void isHelpCommand_variousInputs_onlyBareHelpAccepted() {
        DavidGoggins chatbot = newChatbot();
        assertTrue(chatbot.isHelpCommand(" HELP "));
        assertFalse(chatbot.isHelpCommand("help deadline"));
    }

    /** The help page must document every command the chatbot understands. */
    @ParameterizedTest
    @ValueSource(strings = {"todo", "deadline", "event", "list", "find", "mark", "unmark", "delete", "help", "bye"})
    public void getHelp_always_documentsEachCommand(String command) {
        assertTrue(newChatbot().getHelp().lines().anyMatch(line -> line.startsWith(command)), command);
    }

    @Test
    public void getGreeting_always_introducesChatbot() {
        assertTrue(newChatbot().getGreeting().contains("David Goggins"));
    }

    @Test
    public void getRestoredTasks_noSaveFile_emptyString() {
        assertEquals("", newChatbot().getRestoredTasks());
    }

    /** Tasks added in one session must be there when the app is next started. */
    @Test
    public void getRestoredTasks_tasksSavedByEarlierSession_tasksListed() {
        replyAfter(newChatbot(), "todo read book", "mark 1");
        String restored = newChatbot().getRestoredTasks();
        assertTrue(restored.contains("1.[T][X] read book"), restored);
    }

    @Test
    public void getLoadWarnings_cleanSaveFile_emptyString() throws IOException {
        Files.writeString(saveFile(), "T | 0 | read book\n");
        assertEquals("", newChatbot().getLoadWarnings());
    }

    @Test
    public void getLoadWarnings_corruptSaveFile_warningReturned() throws IOException {
        Files.writeString(saveFile(), "T | 0 | read book\ngarbage\n");
        String warnings = newChatbot().getLoadWarnings();
        assertTrue(warnings.contains("skipped 1 unreadable line"), warnings);
    }

    /** Drives the text UI's loop with scripted input, as the text-UI test plan does. */
    @Test
    public void run_scriptedInput_greetingRepliesAndFarewellPrinted() {
        System.setIn(new ByteArrayInputStream("todo read book\nbogus\nbye\nlist\n"
                .getBytes(StandardCharsets.UTF_8)));
        // The chatbot's Ui reads System.in when it is built, so build it after the swap.
        newChatbot().run();

        String output = printed.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("[T][ ] read book"), output);
        assertTrue(output.contains("NO EXCUSES! I don't know the command \"bogus\""), output);
        assertTrue(output.contains("Stay hard!"), output);
        assertFalse(output.contains("Here's what you signed up for"), "nothing after bye should run");
    }

    @Test
    public void run_corruptSaveFile_warningPrintedBeforeGreeting() throws IOException {
        Files.writeString(saveFile(), "garbage\n");
        System.setIn(new ByteArrayInputStream(new byte[0]));
        newChatbot().run();

        String output = printed.toString(StandardCharsets.UTF_8);
        assertTrue(output.startsWith(" Warning: skipped 1 unreadable line"), output);
    }
}
