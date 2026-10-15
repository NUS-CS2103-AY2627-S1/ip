package michael;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MichaelUndoTest {

    @Test
    void undoRestoresTheStateBeforeTheMostRecentMutation() throws Exception {
        Michael michael = new Michael(Files.createTempFile("michael-undo-", ".txt").toString());

        michael.processCommand("todo buy milk");
        assertEquals("Undid the last command.", michael.processCommand("undo"));
        assertEquals("🌑 Your task orbit is empty.", michael.processCommand("list"));
        assertEquals("There is nothing to undo.", michael.processCommand("undo"));
    }

    @Test
    void readOnlyCommandsDoNotConsumeUndo() throws Exception {
        Michael michael = new Michael(Files.createTempFile("michael-undo-", ".txt").toString());

        michael.processCommand("todo buy milk");
        michael.processCommand("list");
        assertEquals("Undid the last command.", michael.processCommand("undo"));
    }

    @Test
    void byeReturnsGoodbyeMessage() throws Exception {
        Michael michael = new Michael(Files.createTempFile("michael-bye-", ".txt").toString());

        assertEquals("👋 Disengaging from mission control. Safe travels among the stars!",
                michael.processCommand("bye"));
    }

    @Test
    void findReturnsMatchingTasks() throws Exception {
        Michael michael = new Michael(Files.createTempFile("michael-find-", ".txt").toString());

        michael.processCommand("todo buy milk");

        assertEquals("📡 Matching signals from your task orbit:\n[[T][ ] buy milk]",
                michael.processCommand("find milk"));
    }

    @Test
    void invalidCommandSetsErrorFlag() throws Exception {
        Michael michael = new Michael(Files.createTempFile("michael-error-", ".txt").toString());

        michael.processCommand("unknown");

        assertEquals(true, michael.wasLastResponseError());
    }
}
