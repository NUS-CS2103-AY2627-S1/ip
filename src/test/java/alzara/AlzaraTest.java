package alzara;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import alzara.storage.Storage;
import alzara.task.Task;
import alzara.task.ToDo;
import alzara.ui.Ui;

/**
 * Tests for {@link Alzara}'s GUI entry points ({@link Alzara#getWelcomeMessage()}
 * and {@link Alzara#getResponse(String)}). {@code run()} isn't tested here -
 * it blocks on {@code System.in} via {@link alzara.ui.Ui#readCommand()}, so
 * it's exercised instead by the console UI test plan.
 *
 * <p>Every test uses the {@link Alzara#Alzara(boolean, Storage)} constructor
 * with a {@link Storage} backed by a fresh JUnit {@code @TempDir}, so no test
 * touches the real save file. {@link Alzara#getWelcomeMessage()} is always
 * called first, since that's what actually loads the task list - the same
 * order {@code MainWindow.setAlzara} uses in the real GUI.
 */
class AlzaraTest {
    // getWelcomeMessage() should return the same greeting Ui.showWelcome() prints.
    @Test
    void getWelcomeMessage_returnsGreeting(@TempDir File dataDir) {
        Alzara alzara = new Alzara(true, new Storage(dataDir));

        String welcome = alzara.getWelcomeMessage();

        assertEquals("And as it was foretold,\n"
                + "You find yourself face to face with the great Alzara.\n"
                + "What can I do for you?", welcome);
    }

    // getWelcomeMessage() should also load whatever was already saved, so a
    // subsequent getResponse("list") sees it.
    @Test
    void getWelcomeMessage_loadsPreviouslySavedTasks(@TempDir File dataDir) {
        Storage storage = new Storage(dataDir);
        ArrayList<Task> seed = new ArrayList<>();
        seed.add(new ToDo("read book"));
        storage.save(seed, new Ui(true));
        Alzara alzara = new Alzara(true, storage);

        alzara.getWelcomeMessage();
        String response = alzara.getResponse("list");

        assertEquals("Behold, all that awaits you:\n1.[T][ ] read book", response);
    }

    // A valid command should return its normal formatted reply.
    @Test
    void getResponse_validCommand_returnsFormattedReply(@TempDir File dataDir) {
        Alzara alzara = new Alzara(true, new Storage(dataDir));
        alzara.getWelcomeMessage();

        String response = alzara.getResponse("todo read book");

        assertEquals("You have something to do...\n[T][ ] read book\nYou have 1 tasks.", response);
    }

    // An invalid command should return its error message and mark the response as an error.
    @Test
    void getResponse_invalidCommand_returnsErrorAndSetsIsLastResponseError(@TempDir File dataDir) {
        Alzara alzara = new Alzara(true, new Storage(dataDir));
        alzara.getWelcomeMessage();

        String response = alzara.getResponse("mark");

        assertEquals(AlzaraException.MISSING_TASK_NUMBER_MESSAGE, response);
        assertTrue(alzara.isLastResponseError());
    }

    // A valid command right after an error should reset isLastResponseError to false.
    @Test
    void getResponse_validCommandAfterError_isLastResponseErrorResetsToFalse(@TempDir File dataDir) {
        Alzara alzara = new Alzara(true, new Storage(dataDir));
        alzara.getWelcomeMessage();
        alzara.getResponse("mark");

        alzara.getResponse("todo read book");

        assertFalse(alzara.isLastResponseError());
    }

    // isExit() should be false before any command has been answered.
    @Test
    void isExit_beforeAnyResponse_returnsFalse(@TempDir File dataDir) {
        Alzara alzara = new Alzara(true, new Storage(dataDir));
        alzara.getWelcomeMessage();

        assertFalse(alzara.isExit());
    }

    // isExit() should be false after a non-exit command.
    @Test
    void isExit_afterNonExitCommand_returnsFalse(@TempDir File dataDir) {
        Alzara alzara = new Alzara(true, new Storage(dataDir));
        alzara.getWelcomeMessage();

        alzara.getResponse("todo read book");

        assertFalse(alzara.isExit());
    }

    // getResponse("bye") should return the goodbye message and set isExit() to true.
    @Test
    void getResponse_byeCommand_returnsGoodbyeAndSetsIsExitTrue(@TempDir File dataDir) {
        Alzara alzara = new Alzara(true, new Storage(dataDir));
        alzara.getWelcomeMessage();

        String response = alzara.getResponse("bye");

        assertEquals("Our audience has ended. Until we meet again.", response);
        assertTrue(alzara.isExit());
    }
}
