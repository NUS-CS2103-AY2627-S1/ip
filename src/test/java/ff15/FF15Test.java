package ff15;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests {@link FF15} the way its two front ends use it: the GUI asks for a
 * greeting, then a reply per line and two flags about that reply; the console
 * hands it standard input and lets it run to "bye".
 *
 * <p>The constructor prints the greeting, so the console is silenced for every
 * test and captured only where a test is about what was printed. Every test
 * gets its own temporary folder, so nothing here can touch the real save files.
 */
public class FF15Test {

    @TempDir
    Path tempDir;

    private PrintStream realOut;
    private InputStream realIn;

    @BeforeEach
    public void silenceTheConsole() {
        realOut = System.out;
        realIn = System.in;
        System.setOut(new PrintStream(OutputStream.nullOutputStream()));
    }

    @AfterEach
    public void restoreTheConsole() {
        System.setOut(realOut);
        System.setIn(realIn);
    }

    private Path taskFile() {
        return tempDir.resolve("data").resolve("ff15.txt");
    }

    private Path contactFile() {
        return tempDir.resolve("data").resolve("contacts.txt");
    }

    /** Starts a chatbot on this test's own save files, which may or may not exist yet. */
    private FF15 freshFf15() {
        return new FF15(taskFile().toString(), contactFile().toString());
    }

    /** Starts a chatbot on this test's own save files, whose AI always replies {@code reply}. */
    private FF15 ff15WhoseAiReplies(String reply) {
        return new FF15(taskFile().toString(), contactFile().toString(), (systemPrompt, userPrompt) -> reply);
    }

    // --- the greeting -----------------------------------------------------------

    @Test
    public void getStartupMessage_nothingSaved_isTheGreetingAlone() {
        String greeting = freshFf15().getStartupMessage();
        assertTrue(greeting.contains("Assistant TO the Regional Manager"), greeting);
        assertFalse(greeting.contains("GOD"), greeting);
    }

    @Test
    public void getStartupMessage_calledAgain_hasNothingLeftToSay() {
        FF15 ff15 = freshFf15();
        ff15.getStartupMessage();
        assertEquals("", ff15.getStartupMessage());
    }

    @Test
    public void getStartupMessage_taskFileHasABadLine_reportsItAndKeepsTheRest() throws Exception {
        Files.createDirectories(taskFile().getParent());
        Files.writeString(taskFile(), "T | 0 | good one\nX | 0 | broken\nT | 0 | good two\n");

        FF15 ff15 = freshFf15();
        String greeting = ff15.getStartupMessage();

        assertTrue(greeting.contains("skipped"), greeting);
        assertTrue(greeting.contains("line 2"), greeting);
        String list = ff15.getResponse("list");
        assertTrue(list.contains("good one") && list.contains("good two"), list);
        assertFalse(list.contains("broken"), list);
    }

    @Test
    public void getStartupMessage_taskFileIsAFolder_reportsAndStartsEmpty() throws Exception {
        Files.createDirectories(taskFile());

        FF15 ff15 = freshFf15();
        String greeting = ff15.getStartupMessage();

        assertTrue(greeting.contains("Couldn't read your saved tasks"), greeting);
        assertTrue(ff15.getResponse("list").contains("Nothing on the list"));
    }

    @Test
    public void getStartupMessage_contactFileIsAFolder_reportsAndStartsEmpty() throws Exception {
        Files.createDirectories(contactFile());

        FF15 ff15 = freshFf15();
        String greeting = ff15.getStartupMessage();

        assertTrue(greeting.contains("Couldn't read your saved contacts"), greeting);
        assertTrue(ff15.getResponse("contact list").contains("Here are the contacts"));
    }

    // --- one reply per line ------------------------------------------------------

    @Test
    public void getResponse_validCommand_repliesAndIsNotAnError() {
        FF15 ff15 = freshFf15();

        String reply = ff15.getResponse("todo read book");

        assertTrue(reply.contains("read book"), reply);
        assertFalse(ff15.isLastReplyError());
        assertFalse(ff15.isFinished());
    }

    @Test
    public void getResponse_unknownCommand_repliesWithAnErrorAndSaysSo() {
        FF15 ff15 = freshFf15();

        String reply = ff15.getResponse("blah");

        assertTrue(reply.contains("GOD"), reply);
        assertTrue(ff15.isLastReplyError());
        assertFalse(ff15.isFinished());
    }

    @Test
    public void getResponse_successAfterAnError_clearsTheErrorFlag() {
        FF15 ff15 = freshFf15();
        ff15.getResponse("blah");
        ff15.getResponse("todo read book");
        assertFalse(ff15.isLastReplyError());
    }

    @Test
    public void getResponse_saveFails_repliesWithAnErrorAndSaysSo() throws Exception {
        FF15 ff15 = freshFf15();
        // A file now sits where the data folder needs to be, so the next save cannot create it.
        Files.writeString(tempDir.resolve("data"), "in the way");

        String reply = ff15.getResponse("todo read book");

        assertTrue(reply.contains("Couldn't save your tasks"), reply);
        assertTrue(ff15.isLastReplyError());
    }

    @Test
    public void getResponse_bye_endsTheSession() {
        FF15 ff15 = freshFf15();
        assertFalse(ff15.isFinished());

        String reply = ff15.getResponse("bye");

        assertTrue(reply.contains("See ya tomorrow, boss."), reply);
        assertTrue(ff15.isFinished());
        assertFalse(ff15.isLastReplyError());
    }

    @Test
    public void getResponse_addedTask_isStillThereForTheNextSession() {
        freshFf15().getResponse("todo read book");

        FF15 next = freshFf15();
        next.getStartupMessage();

        assertTrue(next.getResponse("list").contains("read book"));
    }

    @Test
    public void getResponse_contactCommands_reachTheContactList() {
        FF15 ff15 = freshFf15();
        ff15.getResponse("contact add John /phone 91234567");

        String list = ff15.getResponse("contact list");

        assertTrue(list.contains("John") && list.contains("91234567"), list);
        assertFalse(ff15.isLastReplyError());
    }

    @Test
    public void getResponse_everyReply_isNonEmpty() {
        FF15 ff15 = freshFf15();
        for (String line : new String[] {"list", "contact list", "find zzz", "on 2019", "blah", "mark 9"}) {
            assertFalse(ff15.getResponse(line).isEmpty(), line);
        }
    }

    // --- the AI commands ---------------------------------------------------------

    @Test
    public void getSuggestedCommand_afterAnAiRequest_isTheCommandAndNothingRan() {
        FF15 ff15 = ff15WhoseAiReplies("todo read book");

        String reply = ff15.getResponse("@do remind me to read");

        assertTrue(reply.contains("todo read book"), reply);
        assertEquals("todo read book", ff15.getSuggestedCommand());
        assertFalse(ff15.isLastReplyError());
        assertTrue(ff15.getResponse("list").contains("Nothing on the list"));
    }

    @Test
    public void getSuggestedCommand_afterTheNextReply_isEmptyAgain() {
        FF15 ff15 = ff15WhoseAiReplies("todo read book");
        ff15.getResponse("@do remind me to read");

        ff15.getResponse("todo read book");

        assertEquals("", ff15.getSuggestedCommand());
    }

    @Test
    public void getResponse_aiCommandWithNoAi_repliesWithAnError() {
        FF15 ff15 = freshFf15();

        String reply = ff15.getResponse("@ai what can you do?");

        assertTrue(reply.contains("There's no AI here"), reply);
        assertTrue(ff15.isLastReplyError());
        assertEquals("", ff15.getSuggestedCommand());
    }

    // --- the console loop --------------------------------------------------------

    /** Runs a console session fed {@code lines}, returning everything it printed. */
    private String consoleSession(String... lines) {
        String typed = String.join("\n", lines) + "\n";
        System.setIn(new ByteArrayInputStream(typed.getBytes(StandardCharsets.UTF_8)));
        ByteArrayOutputStream printed = new ByteArrayOutputStream();
        System.setOut(new PrintStream(printed, true, StandardCharsets.UTF_8));

        freshFf15().run();

        return printed.toString(StandardCharsets.UTF_8);
    }

    @Test
    public void run_readsUntilBye_printingGreetingRepliesAndFarewell() {
        String printed = consoleSession("todo read book", "list", "bye");

        assertTrue(printed.contains("Assistant TO the Regional Manager"), printed);
        assertTrue(printed.contains("read book"), printed);
        assertTrue(printed.contains("See ya tomorrow, boss."), printed);
        assertTrue(printed.contains("_____"), "replies should be framed by divider lines");
    }

    @Test
    public void run_errorMidSession_isReportedAndTheSessionContinues() {
        String printed = consoleSession("blah", "todo read book", "bye");

        assertTrue(printed.contains("GOD"), printed);
        assertTrue(printed.contains("read book"), printed);
        assertTrue(printed.contains("See ya tomorrow, boss."), printed);
    }

    @Test
    public void run_saveFailsMidSession_isReportedAndTheSessionContinues() throws Exception {
        Files.writeString(tempDir.resolve("data"), "in the way");

        String printed = consoleSession("todo read book", "bye");

        assertTrue(printed.contains("Couldn't save your tasks"), printed);
        assertTrue(printed.contains("See ya tomorrow, boss."), printed);
    }
}
