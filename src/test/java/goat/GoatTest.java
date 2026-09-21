package goat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests the interface the graphical front end uses.
 *
 * getResponse() is the whole of the connection between the GUI and the rest of
 * the program, so it is worth pinning down: it must return the reply rather
 * than print it, must turn an error into a reply instead of throwing, and must
 * keep the task list alive from one call to the next.
 */
public class GoatTest {

    private static Goat guiGoat(Path folder) {
        return Goat.forGui(folder.resolve("goat.txt").toString());
    }

    @Test
    public void getWelcomeMessage_freshStart_greetsWithoutTheAsciiBanner(@TempDir Path folder) {
        String welcome = guiGoat(folder).getWelcomeMessage();

        assertTrue(welcome.contains("Goat here."), welcome);
        assertTrue(welcome.contains("I keep the list."), welcome);
        // The banner is drawn out of underscores that only line up in a
        // monospaced terminal, so a GUI must not receive it.
        assertFalse(welcome.contains("____"), welcome);
    }

    @Test
    public void getResponse_addTask_returnsTheConfirmation(@TempDir Path folder) {
        Goat goat = guiGoat(folder);
        goat.getWelcomeMessage();

        String response = goat.getResponse("todo read book");

        assertTrue(response.contains("On the list:"), response);
        assertTrue(response.contains("[T][ ] read book"), response);
    }

    @Test
    public void getResponse_severalCommands_listSurvivesBetweenCalls(@TempDir Path folder) {
        Goat goat = guiGoat(folder);
        goat.getWelcomeMessage();

        goat.getResponse("todo read book");
        goat.getResponse("todo join sports club");
        String listing = goat.getResponse("list");

        assertTrue(listing.contains("1.[T][ ] read book"), listing);
        assertTrue(listing.contains("2.[T][ ] join sports club"), listing);
    }

    @Test
    public void getResponse_eachCall_returnsOnlyThatCallsReply(@TempDir Path folder) {
        Goat goat = guiGoat(folder);
        goat.getWelcomeMessage();
        goat.getResponse("todo read book");

        String second = goat.getResponse("todo join sports club");

        // The buffer is emptied each time, so a reply never carries the
        // previous one along with it.
        assertFalse(second.contains("read book"), second);
    }

    @Test
    public void getResponse_unknownCommand_returnsTheComplaintInsteadOfThrowing(
            @TempDir Path folder) {
        Goat goat = guiGoat(folder);
        goat.getWelcomeMessage();

        String response = goat.getResponse("blah");

        // A GUI has nowhere to put an exception, so errors come back as text.
        assertTrue(response.startsWith("Hm."), response);
    }

    @Test
    public void getResponse_badTaskNumber_returnsTheComplaint(@TempDir Path folder) {
        Goat goat = guiGoat(folder);
        goat.getWelcomeMessage();

        assertTrue(goat.getResponse("delete 9").startsWith("Hm."));
    }

    @Test
    public void isLastResponseAnError_afterAGoodCommand_false(@TempDir Path folder) {
        Goat goat = guiGoat(folder);
        goat.getWelcomeMessage();

        goat.getResponse("todo read book");

        assertFalse(goat.isLastResponseAnError());
    }

    @Test
    public void isLastResponseAnError_afterAnUnknownCommand_true(@TempDir Path folder) {
        Goat goat = guiGoat(folder);
        goat.getWelcomeMessage();

        goat.getResponse("blah");

        // The window shows a complaint differently from a result, so it has to
        // be able to tell them apart without reading the text.
        assertTrue(goat.isLastResponseAnError());
    }

    @Test
    public void isLastResponseAnError_afterRecoveringFromAnError_false(@TempDir Path folder) {
        Goat goat = guiGoat(folder);
        goat.getWelcomeMessage();
        goat.getResponse("blah");

        goat.getResponse("todo read book");

        // The flag describes the last reply, not whether one ever failed.
        assertFalse(goat.isLastResponseAnError());
    }

    @Test
    public void isLastResponseAnError_afterABadTaskNumber_true(@TempDir Path folder) {
        Goat goat = guiGoat(folder);
        goat.getWelcomeMessage();

        goat.getResponse("delete 9");

        assertTrue(goat.isLastResponseAnError());
    }

    @Test
    public void isLastResponseAnError_damagedSaveFile_trueAfterTheGreeting(@TempDir Path folder)
            throws Exception {
        Path file = folder.resolve("goat.txt");
        java.nio.file.Files.writeString(file, "D | 0 | return book | 2 Dec 2019\n");
        Goat goat = Goat.forGui(file.toString());

        goat.getWelcomeMessage();

        // The complaint rides along with the greeting, so the opening bubble
        // has to be shown as an error rather than a normal reply.
        assertTrue(goat.isLastResponseAnError());
    }

    @Test
    public void isExitRequested_beforeBye_false(@TempDir Path folder) {
        Goat goat = guiGoat(folder);
        goat.getWelcomeMessage();
        goat.getResponse("list");

        assertFalse(goat.isExitRequested());
    }

    @Test
    public void getResponse_bye_saysGoodbyeAndAsksToExit(@TempDir Path folder) {
        Goat goat = guiGoat(folder);
        goat.getWelcomeMessage();

        String response = goat.getResponse("bye");

        // run() shows the farewell after its loop; there is no loop here, so
        // getResponse has to produce it.
        assertTrue(response.contains("Off up the hill."), response);
        assertTrue(goat.isExitRequested());
    }

    @Test
    public void getResponse_changes_reachTheSaveFile(@TempDir Path folder) {
        Path file = folder.resolve("goat.txt");
        Goat first = Goat.forGui(file.toString());
        first.getWelcomeMessage();
        first.getResponse("todo read book");

        // A second chatbot over the same file sees what the first one saved.
        Goat second = Goat.forGui(file.toString());
        second.getWelcomeMessage();

        assertTrue(second.getResponse("list").contains("read book"));
    }

    @Test
    public void getWelcomeMessage_damagedSaveFile_warnsInTheGreeting(@TempDir Path folder)
            throws Exception {
        Path file = folder.resolve("goat.txt");
        java.nio.file.Files.writeString(file, "D | 0 | return book | 2 Dec 2019\n");

        String welcome = Goat.forGui(file.toString()).getWelcomeMessage();

        // The GUI shows one opening bubble, so the complaint has to travel
        // with the greeting or the user never sees it.
        assertTrue(welcome.contains("Goat here."), welcome);
        assertTrue(welcome.contains("damaged on line 1"), welcome);
    }

    @Test
    public void getResponse_emptyTaskListThenFind_reportsNoMatches(@TempDir Path folder) {
        Goat goat = guiGoat(folder);
        goat.getWelcomeMessage();

        assertEquals("Nothing matches that.", goat.getResponse("find book"));
    }
}
