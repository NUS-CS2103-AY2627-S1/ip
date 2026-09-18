package milo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Milo.getResponse() is what the GUI calls for every message, so it needs
 * to cover what {@code run()} otherwise handles inline: a normal command,
 * an invalid one, and "bye" ending the session.
 */
class MiloTest {
    @TempDir
    Path tempDir;

    private Milo newMilo() {
        return new Milo(tempDir.resolve("milo.txt").toString());
    }

    @Test
    void getResponse_validCommand_returnsConfirmationAndStaysOpen() {
        Milo milo = newMilo();
        String response = milo.getResponse("todo read book");
        assertTrue(response.contains("[T][ ] read book"));
        assertFalse(milo.isExit());
    }

    @Test
    void getResponse_invalidCommand_returnsErrorMessage() {
        Milo milo = newMilo();
        String response = milo.getResponse("blah");
        assertTrue(response.contains("\"blah\" isn't a trick I know"));
        assertFalse(milo.isExit());
    }

    @Test
    void getResponse_bye_returnsGoodbyeAndSetsExit() {
        Milo milo = newMilo();
        String response = milo.getResponse("bye");
        assertEquals("Off I go. Your list is safe with me!", response);
        assertTrue(milo.isExit());
    }

    @Test
    void isExit_beforeAnyCommand_isFalse() {
        assertFalse(newMilo().isExit());
    }

    @Test
    void isError_beforeAnyCommand_isFalse() {
        assertFalse(newMilo().isError());
    }

    @Test
    void isError_afterAValidCommand_isFalse() {
        Milo milo = newMilo();
        milo.getResponse("todo read book");
        assertFalse(milo.isError());
    }

    @Test
    void isError_afterAnInvalidCommand_isTrue() {
        Milo milo = newMilo();
        milo.getResponse("blah");
        assertTrue(milo.isError());
    }

    @Test
    void isError_clearsOnceAValidCommandRuns() {
        Milo milo = newMilo();
        milo.getResponse("blah");
        milo.getResponse("todo read book");
        assertFalse(milo.isError());
    }

    @Test
    void getResponse_duplicateTask_isReportedAsAnError() {
        Milo milo = newMilo();
        milo.getResponse("todo read book");
        String response = milo.getResponse("todo read book");
        assertTrue(response.contains("already on your list"));
        assertTrue(milo.isError());
    }

    @Test
    void getResponse_undoAfterAdd_removesTheTask() {
        Milo milo = newMilo();
        milo.getResponse("todo read book");
        milo.getResponse("undo");
        assertTrue(milo.getResponse("list").contains("Your list is empty"));
    }

    @Test
    void getResponse_blankInput_isReportedRatherThanCrashing() {
        Milo milo = newMilo();
        String response = milo.getResponse("   ");
        assertTrue(milo.isError());
        assertTrue(!response.isEmpty());
    }

    @Test
    void getWelcomeMessage_emptyList_isJustTheGreeting() {
        String welcome = newMilo().getWelcomeMessage();
        assertEquals("Woof! Milo here.\nWhat are we getting done today?", welcome);
    }

    @Test
    void getWelcomeMessage_afterTasksWereSaved_mentionsHowManyWereLoaded() {
        Milo first = newMilo();
        first.getResponse("todo read book");

        Milo second = newMilo();
        String welcome = second.getWelcomeMessage();
        assertTrue(welcome.contains("Woof! Milo here."));
        assertTrue(welcome.contains("Fetched 1 task from last time."));
    }
}
