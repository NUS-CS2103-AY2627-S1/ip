package seedu.mattchatbot;

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

/**
 * Tests the chatbot end to end: a command goes in as the user would type it,
 * and what comes back is checked against what the user would see.
 * <p>
 * These go through the same path the window does, so between them they cover
 * dispatch, the wording of every reply, whether a turn is marked as a
 * complaint, and the trip out to the save file and back. Each test works in a
 * temporary folder, so none of them touches the real list.
 */
public class MattChatBotTest {

    @TempDir
    private Path tempDir;

    private MattChatBot botAt(String name) {
        return MattChatBot.forGui(tempDir.resolve(name).toString());
    }

    private MattChatBot freshBot() {
        return botAt("tasks.txt");
    }

    /** Runs commands in order and returns what the last one said. */
    private static Reply after(MattChatBot bot, String... commands) {
        Reply reply = new Reply("", false);
        for (String command : commands) {
            reply = bot.getResponse(command);
        }
        return reply;
    }

    // ---------- greeting ----------

    @Test
    public void getWelcomeMessage_noSavedFile_greetsWithoutComplaint() {
        Reply welcome = freshBot().getWelcomeMessage();
        assertFalse(welcome.isError());
        assertTrue(welcome.text().contains("Winston, at your service."), welcome.text());
    }

    @Test
    public void getWelcomeMessage_damagedSavedFile_complainsAndNamesTheLine() throws Exception {
        Path file = tempDir.resolve("damaged.txt");
        Files.write(file, List.of("T | 0 | read book", "X | 0 | nonsense"));
        Reply welcome = MattChatBot.forGui(file.toString()).getWelcomeMessage();
        assertTrue(welcome.isError());
        assertTrue(welcome.text().contains("line 2: Unknown task type: X"), welcome.text());
    }

    // ---------- adding ----------

    @Test
    public void getResponse_todo_addedAndCounted() {
        Reply reply = after(freshBot(), "todo read book");
        assertFalse(reply.isError());
        assertEquals("Very good. I have noted it:" + System.lineSeparator()
                + "  [T][ ] read book" + System.lineSeparator()
                + "You now have 1 entry in your list.", reply.text());
    }

    @Test
    public void getResponse_deadline_addedWithDueDate() {
        Reply reply = after(freshBot(), "deadline return book /by 2019-10-15");
        assertTrue(reply.text().contains("[D][ ] return book (by: Oct 15 2019)"), reply.text());
    }

    @Test
    public void getResponse_event_addedWithBothTimes() {
        Reply reply = after(freshBot(),
                "event project meeting /from 2019-10-15 1400 /to 2019-10-15 1600");
        assertTrue(reply.text().contains(
                "[E][ ] project meeting (from: Oct 15 2019, 2:00pm to: Oct 15 2019, 4:00pm)"),
                reply.text());
    }

    @Test
    public void getResponse_secondEntryAdded_countTurnsPlural() {
        Reply reply = after(freshBot(), "todo read book", "todo buy bread");
        assertTrue(reply.text().contains("You now have 2 entries in your list."), reply.text());
    }

    @Test
    public void getResponse_sameEntryTwice_refused() {
        Reply reply = after(freshBot(), "todo read book", "todo read book");
        assertTrue(reply.isError());
        assertTrue(reply.text().startsWith("That is already on your list:"), reply.text());
    }

    // ---------- listing ----------

    @Test
    public void getResponse_listWhenEmpty_saysSoAndSuggestsCommands() {
        Reply reply = after(freshBot(), "list");
        assertEquals("Your list is quite empty. You might begin with todo, deadline or event.",
                reply.text());
    }

    @Test
    public void getResponse_listWithEntries_numberedFromOne() {
        Reply reply = after(freshBot(), "todo read book", "todo buy bread", "list");
        assertEquals("Your list, as it stands:" + System.lineSeparator()
                + "1.[T][ ] read book" + System.lineSeparator()
                + "2.[T][ ] buy bread", reply.text());
    }

    // ---------- marking ----------

    @Test
    public void getResponse_mark_entryShownComplete() {
        Reply reply = after(freshBot(), "todo read book", "mark 1");
        assertEquals("Excellent. I have marked it complete:" + System.lineSeparator()
                + "  [T][X] read book", reply.text());
    }

    @Test
    public void getResponse_unmark_entryShownOutstanding() {
        Reply reply = after(freshBot(), "todo read book", "mark 1", "unmark 1");
        assertEquals("Very well. It is outstanding once more:" + System.lineSeparator()
                + "  [T][ ] read book", reply.text());
    }

    @Test
    public void getResponse_markPastEndOfList_refused() {
        Reply reply = after(freshBot(), "todo read book", "mark 9");
        assertTrue(reply.isError());
        assertTrue(reply.text().contains("there is no entry 9"), reply.text());
    }

    // ---------- deleting and updating ----------

    @Test
    public void getResponse_delete_entryRemovedAndCountFalls() {
        Reply reply = after(freshBot(), "todo read book", "todo buy bread", "delete 1");
        assertEquals("Consider it struck from the list:" + System.lineSeparator()
                + "  [T][ ] read book" + System.lineSeparator()
                + "You now have 1 entry in your list.", reply.text());
    }

    @Test
    public void getResponse_update_onlyTheNamedPartChanges() {
        Reply reply = after(freshBot(),
                "event meeting /from 2019-10-15 1400 /to 2019-10-15 1600",
                "update 1 /to 2019-10-15 1800");
        assertEquals("Duly amended:" + System.lineSeparator()
                + "  [E][ ] meeting (from: Oct 15 2019, 2:00pm to: Oct 15 2019, 6:00pm)",
                reply.text());
    }

    // ---------- searching ----------

    @Test
    public void getResponse_findWithMatches_matchesListed() {
        Reply reply = after(freshBot(), "todo read book", "todo buy bread", "find book");
        assertEquals("The entries matching your enquiry:" + System.lineSeparator()
                + "1.[T][ ] read book", reply.text());
    }

    @Test
    public void getResponse_findWithNoMatches_saysSo() {
        Reply reply = after(freshBot(), "todo read book", "find piano");
        assertEquals("Nothing matches \"piano\", I'm afraid.", reply.text());
    }

    @Test
    public void getResponse_onDateWithEntries_entriesListed() {
        Reply reply = after(freshBot(), "deadline return book /by 2019-10-15", "on 2019-10-15");
        assertEquals("Your engagements for Oct 15 2019:" + System.lineSeparator()
                + "1.[D][ ] return book (by: Oct 15 2019)", reply.text());
    }

    @Test
    public void getResponse_onDateWithNothing_saysSo() {
        Reply reply = after(freshBot(), "deadline return book /by 2019-10-15", "on 2019-12-25");
        assertEquals("Nothing at all on Dec 25 2019.", reply.text());
    }

    // ---------- input the chatbot cannot act on ----------

    @Test
    public void getResponse_blankInput_nothingSaidAndNotAComplaint() {
        Reply reply = after(freshBot(), "   ");
        assertEquals("", reply.text());
        assertFalse(reply.isError());
    }

    @Test
    public void getResponse_unknownCommand_markedAsComplaint() {
        Reply reply = after(freshBot(), "blah");
        assertTrue(reply.isError());
        assertTrue(reply.text().startsWith("I regret I did not follow \"blah\"."), reply.text());
    }

    @Test
    public void getResponse_leadingAndTrailingSpaces_commandStillUnderstood() {
        Reply reply = after(freshBot(), "   todo read book   ");
        assertFalse(reply.isError());
        assertTrue(reply.text().contains("[T][ ] read book"), reply.text());
    }

    // ---------- leaving ----------

    @Test
    public void isExit_beforeAndAfterBye_reportsCorrectly() {
        MattChatBot bot = freshBot();
        after(bot, "todo read book");
        assertFalse(bot.isExit());
        Reply reply = after(bot, "bye");
        assertTrue(bot.isExit());
        assertEquals("Very good. I shall be here when you return.", reply.text());
    }

    // ---------- the trip out to disk and back ----------

    @Test
    public void getResponse_changesSurviveARestart() {
        after(freshBot(), "todo read book", "deadline return book /by 2019-10-15", "mark 1");
        // A second chatbot on the same file starts from what the first wrote.
        Reply reply = after(freshBot(), "list");
        assertEquals("Your list, as it stands:" + System.lineSeparator()
                + "1.[T][X] read book" + System.lineSeparator()
                + "2.[D][ ] return book (by: Oct 15 2019)", reply.text());
    }

    @Test
    public void getResponse_deletionSurvivesARestart() {
        after(freshBot(), "todo read book", "todo buy bread", "delete 1");
        assertTrue(after(freshBot(), "list").text().contains("1.[T][ ] buy bread"));
    }

    // ---------- the console loop ----------

    @Test
    public void run_commandsFromStandardInput_conversationPrinted() throws Exception {
        String typed = String.join(System.lineSeparator(),
                "todo read book", "list", "bye") + System.lineSeparator();
        String printed = runConsole(tempDir.resolve("console.txt").toString(), typed);
        assertTrue(printed.contains("Good day. Winston, at your service."), printed);
        assertTrue(printed.contains("1.[T][ ] read book"), printed);
        assertTrue(printed.contains("Very good. I shall be here when you return."), printed);
    }

    @Test
    public void run_inputEndsWithoutBye_stillSaysGoodbye() throws Exception {
        // Reaching the end of piped input must end the session cleanly rather
        // than throwing, which is what a user pressing Ctrl-D looks like.
        String printed = runConsole(tempDir.resolve("eof.txt").toString(),
                "todo read book" + System.lineSeparator());
        assertTrue(printed.contains("Very good. I shall be here when you return."), printed);
    }

    @Test
    public void run_badCommandPartWayThrough_sessionCarriesOn() throws Exception {
        String typed = String.join(System.lineSeparator(),
                "blah", "todo read book", "bye") + System.lineSeparator();
        String printed = runConsole(tempDir.resolve("carryon.txt").toString(), typed);
        assertTrue(printed.contains("I regret I did not follow"), printed);
        assertTrue(printed.contains("[T][ ] read book"), printed);
    }

    /**
     * Runs a console session on the given file with the given typed input, and
     * returns everything it printed.
     */
    private static String runConsole(String filePath, String typed) {
        InputStream realIn = System.in;
        PrintStream realOut = System.out;
        ByteArrayOutputStream printed = new ByteArrayOutputStream();
        try {
            System.setIn(new ByteArrayInputStream(typed.getBytes(StandardCharsets.UTF_8)));
            System.setOut(new PrintStream(printed, true, StandardCharsets.UTF_8));
            // Built after the streams are swapped: the Ui takes its scanner on
            // construction, so a chatbot made earlier would read the real one.
            new MattChatBot(filePath).run();
        } finally {
            System.setIn(realIn);
            System.setOut(realOut);
        }
        return printed.toString(StandardCharsets.UTF_8);
    }

    @Test
    public void getWelcomeMessage_severalDamagedLines_wordedInThePlural() throws Exception {
        Path file = tempDir.resolve("verydamaged.txt");
        Files.write(file, List.of("X | 0 | one", "Y | 0 | two"));
        Reply welcome = MattChatBot.forGui(file.toString()).getWelcomeMessage();
        assertTrue(welcome.text().contains(
                "I was unable to make sense of 2 lines in your saved file,"), welcome.text());
    }

    @Test
    public void getWelcomeMessage_saveFileCannotBeRead_complainsAndStartsEmpty() throws Exception {
        Path blocked = tempDir.resolve("blocked.txt");
        Files.createDirectory(blocked);
        MattChatBot bot = MattChatBot.forGui(blocked.toString());
        Reply welcome = bot.getWelcomeMessage();
        assertTrue(welcome.isError());
        assertTrue(welcome.text().contains("I was unable to read your saved list from"),
                welcome.text());
        // Unreadable is not fatal: the session still runs, on an empty list.
        assertTrue(after(bot, "list").text().startsWith("Your list is quite empty."));
    }

    @Test
    public void run_blankLineTyped_ignoredAndSessionCarriesOn() throws Exception {
        String typed = String.join(System.lineSeparator(),
                "", "   ", "todo read book", "bye") + System.lineSeparator();
        String printed = runConsole(tempDir.resolve("blank.txt").toString(), typed);
        assertTrue(printed.contains("[T][ ] read book"), printed);
    }
}
