package stephen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests the UI-independent facade used by the JavaFX controller. */
class ChatbotTest {
    @TempDir
    Path tempDirectory;

    /** Verifies the normal graphical-interface startup message. */
    @Test
    void getStartupMessageReadableStorageReturnsOrbitGreeting() {
        Chatbot chatbot = new Chatbot(tempDirectory.resolve("tasks.txt"));

        assertEquals("Orbit online.\nReady to plan your next move?",
                chatbot.getStartupMessage());
    }

    /** Verifies that an unreadable data path produces a recoverable startup notice. */
    @Test
    void getStartupMessageUnreadableStorageReturnsRecoveryNotice() throws IOException {
        Path directoryAsFile = tempDirectory.resolve("tasks.txt");
        Files.createDirectory(directoryAsFile);

        Chatbot chatbot = new Chatbot(directoryAsFile);

        assertEquals("Orbit online.\nReady to plan your next move?\n"
                + "Navigation alert: I couldn't load your tasks. Starting with an empty list.",
                chatbot.getStartupMessage());
    }

    /** Verifies stateful command execution and clean response text. */
    @Test
    void getResponseValidCommandsPreservesStateAndOmitsConsoleDividers() {
        Chatbot chatbot = new Chatbot(tempDirectory.resolve("tasks.txt"));

        ChatbotResponse addResponse = chatbot.getResponse("todo read book");
        ChatbotResponse listResponse = chatbot.getResponse("list");

        assertEquals("Mission logged. I've added this task:\n"
                + "  [T][ ] read book\n"
                + "You now have 1 task on the radar.", addResponse.message());
        assertFalse(addResponse.isExit());
        assertFalse(addResponse.isError());
        assertEquals("Current mission plan:\n1.[T][ ] read book",
                listResponse.message());
    }

    /** Verifies that invalid input becomes friendly response text without ending the session. */
    @Test
    void getResponseInvalidCommandReturnsErrorAndContinues() {
        ChatbotResponse response = new Chatbot(tempDirectory.resolve("tasks.txt"))
                .getResponse("unknown");

        assertEquals("Navigation alert: I don't recognise that command.", response.message());
        assertFalse(response.isExit());
        assertTrue(response.isError());
    }

    /** Verifies that the existing exit command is surfaced to graphical clients. */
    @Test
    void getResponseByeReturnsFarewellAndExitSignal() {
        ChatbotResponse response = new Chatbot(tempDirectory.resolve("tasks.txt"))
                .getResponse("bye");

        assertEquals("Orbit signing off. Keep moving forward!", response.message());
        assertTrue(response.isExit());
        assertFalse(response.isError());
    }
}
