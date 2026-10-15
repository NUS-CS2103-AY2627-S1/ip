package billy;

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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests {@link Billy#getResponse(String)}, the way the window asks Billy to do
 * something and gets back what to show.
 *
 * <p>This is worth testing in isolation because it is the one path the text UI
 * test plan cannot reach: those tests type into a console, and this method
 * exists precisely because the window has no console. A fault here would show up
 * only by opening the window and reading it, which no automated test does.
 *
 * <p>What matters is that the answer comes back as text rather than being
 * printed, that a bad command is answered rather than thrown, and that
 * {@code bye} is reported so the window knows to close. The wording itself is
 * the commands' business and is tested through them.
 *
 * <p>Every test works inside a {@link TempDir}, so none of them reads or writes
 * a real save file.
 */
public class BillyTest {

    @TempDir
    private Path folder;

    /** Returns a Billy saving into this test's own folder, with no tasks yet. */
    private Billy billyWithEmptyList() {
        return new Billy(folder.resolve("billy.txt"));
    }

    @Test
    public void getResponse_addATask_confirmationReturned() {
        String response = billyWithEmptyList().getResponse("todo read book");
        assertEquals("Consider it written down:\n  [T][ ] read book\n"
                + "That's 1 task on the books.", response);
    }

    @Test
    public void getResponse_severalCommands_listRemembersThem() {
        Billy billy = billyWithEmptyList();
        billy.getResponse("todo read book");
        billy.getResponse("todo write essay");
        assertEquals("Behold, your list:\n1.[T][ ] read book\n2.[T][ ] write essay",
                billy.getResponse("list"));
    }

    @Test
    public void getResponse_unknownCommand_explanationReturnedNotThrown() {
        String response = billyWithEmptyList().getResponse("blah");
        assertTrue(response.startsWith("I don't know what 'blah' means."), response);
    }

    @Test
    public void getResponse_badTaskNumber_explanationReturnedNotThrown() {
        String response = billyWithEmptyList().getResponse("mark 1");
        assertFalse(response.isEmpty());
    }

    @Test
    public void getResponse_noDividerLines_plainTextReturned() {
        // The console fences messages between dividers; a dialog box shows where
        // a message ends by itself, so they would only be clutter in the window.
        String response = billyWithEmptyList().getResponse("list");
        assertFalse(response.contains("____"), response);
    }

    @Test
    public void getResponse_anyCommand_nothingLeftOverForTheNextOne() {
        Billy billy = billyWithEmptyList();
        billy.getResponse("todo read book");
        // A second answer must not repeat the first: each call starts collecting
        // afresh, so the window shows one reply per command rather than a growing
        // transcript.
        assertFalse(billy.getResponse("list").contains("Consider it written down:"));
    }

    @Test
    public void isExitRequested_beforeAnyCommand_false() {
        assertFalse(billyWithEmptyList().isExitRequested());
    }

    @Test
    public void isExitRequested_ordinaryCommand_false() {
        Billy billy = billyWithEmptyList();
        billy.getResponse("todo read book");
        assertFalse(billy.isExitRequested());
    }

    @Test
    public void isExitRequested_bye_trueAndFarewellReturned() {
        Billy billy = billyWithEmptyList();
        String response = billy.getResponse("bye");
        assertTrue(billy.isExitRequested());
        // The console says goodbye after its loop ends. The window has no loop,
        // so the farewell has to come back with the answer or never be seen.
        assertEquals("Off you go! I'll be right here, guarding the list. Vigilantly.", response);
    }

    @Test
    public void isLastResponseError_beforeAnyCommand_false() {
        assertFalse(billyWithEmptyList().isLastResponseError());
    }

    @Test
    public void isLastResponseError_commandThatWorked_false() {
        Billy billy = billyWithEmptyList();
        billy.getResponse("todo read book");
        assertFalse(billy.isLastResponseError());
    }

    @Test
    public void isLastResponseError_unknownCommand_true() {
        // The window shows a failure differently from an ordinary reply, and the
        // answer is only text by the time it gets there, so this flag is the
        // whole of what tells the two apart.
        Billy billy = billyWithEmptyList();
        billy.getResponse("blah");
        assertTrue(billy.isLastResponseError());
    }

    @Test
    public void isLastResponseError_badTaskNumber_true() {
        Billy billy = billyWithEmptyList();
        billy.getResponse("mark 1");
        assertTrue(billy.isLastResponseError());
    }

    @Test
    public void isLastResponseError_goodCommandAfterABadOne_false() {
        // Left standing from the previous command, this would show every reply
        // after the user's first typo as a failure.
        Billy billy = billyWithEmptyList();
        billy.getResponse("blah");
        billy.getResponse("todo read book");
        assertFalse(billy.isLastResponseError());
    }

    @Test
    public void isStartupMessageError_noSavedFile_false() {
        assertFalse(billyWithEmptyList().isStartupMessageError());
    }

    @Test
    public void isStartupMessageError_savedTasksLoaded_false() throws IOException {
        Path file = folder.resolve("billy.txt");
        Files.write(file, List.of("T | 0 | read book"));
        assertFalse(new Billy(file).isStartupMessageError());
    }

    @Test
    public void isStartupMessageError_damagedLineSkipped_false() throws IOException {
        // A skipped line is news, not a failure: the list did load. Reporting it
        // as an error would greet the user with a red card over one bad line.
        Path file = folder.resolve("billy.txt");
        Files.write(file, List.of("T | 0 | read book", "this line is not a task"));
        assertFalse(new Billy(file).isStartupMessageError());
    }

    @Test
    public void getStartupMessage_noSavedFile_nothingToSay() {
        assertNull(billyWithEmptyList().getStartupMessage());
    }

    @Test
    public void getStartupMessage_savedTasks_saysHowManyWereLoaded() throws IOException {
        Path file = folder.resolve("billy.txt");
        Files.write(file, List.of("T | 0 | read book", "T | 1 | write essay"));
        assertEquals("Welcome back! Nothing moved while you were gone: 2 tasks still waiting.",
                new Billy(file).getStartupMessage());
    }

    @Test
    public void getStartupMessage_damagedLine_saysWhatWasSkipped() throws IOException {
        Path file = folder.resolve("billy.txt");
        Files.write(file, List.of("T | 0 | read book", "this line is not a task"));
        String message = new Billy(file).getStartupMessage();
        assertTrue(message.contains("1 task still waiting"), message);
        assertTrue(message.contains("1 line"), message);
        // Singular throughout: "1 line were gibberish" would read as a fault in
        // Billy rather than in the file it is complaining about.
        assertTrue(message.contains("was gibberish"), message);
        assertTrue(message.contains("skipped it"), message);
    }

    @Test
    public void getStartupMessage_severalDamagedLines_wordedInThePlural() throws IOException {
        Path file = folder.resolve("billy.txt");
        Files.write(file, List.of("T | 0 | read book", "not a task", "also not a task"));
        String message = new Billy(file).getStartupMessage();
        assertTrue(message.contains("2 lines"), message);
        assertTrue(message.contains("were gibberish"), message);
        assertTrue(message.contains("skipped them"), message);
    }

    // ---------------------------------------------------------------
    // The console conversation
    // ---------------------------------------------------------------

    /**
     * Runs a whole console conversation and returns everything printed.
     *
     * <p>Billy builds its Ui, and so its Scanner, in its own constructor, so the
     * input has to be in place before the Billy is made.
     */
    private String converse(String typed) {
        InputStream realIn = System.in;
        PrintStream realOut = System.out;
        ByteArrayOutputStream printed = new ByteArrayOutputStream();
        try {
            System.setIn(new ByteArrayInputStream(typed.getBytes(StandardCharsets.UTF_8)));
            System.setOut(new PrintStream(printed, true, StandardCharsets.UTF_8));
            new Billy(folder.resolve("billy.txt")).run();
        } finally {
            System.setIn(realIn);
            System.setOut(realOut);
        }
        return printed.toString(StandardCharsets.UTF_8);
    }

    @Test
    public void run_aShortConversation_greetsAnswersAndSignsOff() {
        String printed = converse("todo read book\nlist\nbye\n");
        assertTrue(printed.contains(billy.ui.Ui.getGreeting()), printed);
        assertTrue(printed.contains("Consider it written down:"), printed);
        assertTrue(printed.contains("1.[T][ ] read book"), printed);
        assertTrue(printed.contains(billy.ui.Ui.getFarewell()), printed);
    }

    @Test
    public void run_badCommand_conversationCarriesOn() {
        // The whole point of catching the exception in the loop: one mistake
        // must not end the session.
        String printed = converse("blah\ntodo read book\nbye\n");
        assertTrue(printed.contains("I don't know what 'blah' means"), printed);
        assertTrue(printed.contains("Consider it written down:"), printed);
    }

    @Test
    public void run_inputRunsOutWithoutBye_stillSignsOff() {
        // What happens when the user presses Ctrl+D rather than typing `bye`.
        assertTrue(converse("todo read book\n").contains(billy.ui.Ui.getFarewell()));
    }

    @Test
    public void run_noInputAtAll_greetsAndSignsOff() {
        String printed = converse("");
        assertTrue(printed.contains(billy.ui.Ui.getGreeting()), printed);
        assertTrue(printed.contains(billy.ui.Ui.getFarewell()), printed);
    }

    @Test
    public void run_tasksFromAnEarlierRun_reportedOnStartup() throws IOException {
        Files.write(folder.resolve("billy.txt"), List.of("T | 0 | read book"));
        assertTrue(converse("bye\n").contains("1 task still waiting"));
    }

    @Test
    public void run_savedListUnreadable_reportedAndSessionCarriesOn() throws IOException {
        // Bytes that are not valid UTF-8, so reading the file throws rather than
        // producing lines. A damaged *line* is skipped; a file that cannot be
        // read at all is a different path, and this is the one that exercises it.
        Files.write(folder.resolve("billy.txt"), new byte[] {(byte) 0xC3, (byte) 0x28, 0x0A});
        String printed = converse("todo read book\nbye\n");
        assertTrue(printed.contains("couldn't read"), printed);
        // Billy is still usable afterwards: the cost is the old list, not the session.
        assertTrue(printed.contains("Consider it written down:"), printed);
    }

    @Test
    public void constructor_savedListUnreadable_startupMessageIsAnError() throws IOException {
        Files.write(folder.resolve("billy.txt"), new byte[] {(byte) 0xC3, (byte) 0x28, 0x0A});
        Billy billy = new Billy(folder.resolve("billy.txt"));
        // The window paints this one differently from ordinary news, so the two
        // have to be distinguishable.
        assertTrue(billy.isStartupMessageError());
        assertTrue(billy.getStartupMessage().contains("couldn't read"));
    }

    @Test
    public void constructor_savedListUnreadable_billyStillUsable() throws IOException {
        Files.write(folder.resolve("billy.txt"), new byte[] {(byte) 0xC3, (byte) 0x28, 0x0A});
        Billy billy = new Billy(folder.resolve("billy.txt"));
        assertTrue(billy.getResponse("todo read book").contains("Consider it written down"));
    }
}
