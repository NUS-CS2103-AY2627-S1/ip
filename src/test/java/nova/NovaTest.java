package nova;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests the response-oriented interface shared with Nova's JavaFX GUI.
 */
public class NovaTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void getResponse_addThenList_returnsResponsesAndRetainsTask() {
        Nova nova = new Nova(temporaryDirectory.resolve("nova.txt").toString());

        Nova.Response addResponse = nova.getResponse("todo read book");
        Nova.Response listResponse = nova.getResponse("list");

        String expectedAddMessage = String.join(System.lineSeparator(),
                "Mission logged and ready for launch:",
                "   [T][ ] read book",
                " You now have 1 mission in orbit.");
        assertEquals(expectedAddMessage, addResponse.message());
        assertFalse(addResponse.isExit());
        assertFalse(addResponse.isError());
        String expectedListMessage = String.join(System.lineSeparator(),
                "Here's your mission log:",
                " 1.[T][ ] read book");
        assertEquals(expectedListMessage, listResponse.message());
        assertFalse(listResponse.isExit());
        assertFalse(listResponse.isError());
    }

    @Test
    public void getResponse_editThenReload_preservesEditedTask() {
        Path dataFile = temporaryDirectory.resolve("nova.txt");
        Nova nova = new Nova(dataFile.toString());
        nova.getResponse("event meeting /from 2019-12-03 1400 /to 2019-12-03 1600");

        Nova.Response editResponse = nova.getResponse("edit 1 /to 1700");
        Nova reloadedNova = new Nova(dataFile.toString());
        Nova.Response listResponse = reloadedNova.getResponse("list");

        assertEquals(String.join(System.lineSeparator(),
                "Flight plan updated:",
                "   Before: [E][ ] meeting (from: Dec 03 2019, 2:00 PM to: Dec 03 2019, 4:00 PM)",
                "   After:  [E][ ] meeting (from: Dec 03 2019, 2:00 PM to: Dec 03 2019, 5:00 PM)"),
                editResponse.message());
        assertEquals(String.join(System.lineSeparator(),
                "Here's your mission log:",
                " 1.[E][ ] meeting (from: Dec 03 2019, 2:00 PM to: Dec 03 2019, 5:00 PM)"),
                listResponse.message());
    }

    @Test
    public void getResponse_invalidCommand_returnsErrorWithoutExiting() {
        Nova nova = new Nova(temporaryDirectory.resolve("nova.txt").toString());

        Nova.Response response = nova.getResponse("unknown");

        assertEquals("NAVIGATION ALERT: I don't recognize that command. Start with todo, deadline, event, "
                + "find, on, list, mark, unmark, delete, edit, or bye.", response.message());
        assertFalse(response.isExit());
        assertTrue(response.isError());
    }

    @Test
    public void getResponse_bye_returnsFarewellAndExitSignal() {
        Nova nova = new Nova(temporaryDirectory.resolve("nova.txt").toString());

        Nova.Response response = nova.getResponse("bye");

        assertEquals("Returning to base. Until our next mission!", response.message());
        assertTrue(response.isExit());
        assertFalse(response.isError());
    }

    @Test
    public void getWelcomeMessage_corruptData_includesLoadingWarning() throws IOException {
        Path dataFile = temporaryDirectory.resolve("nova.txt");
        Files.writeString(dataFile, "T | maybe | invalid\n", StandardCharsets.UTF_8);

        Nova nova = new Nova(dataFile.toString());

        assertEquals("Nova online! Your mission navigator is ready.\nWhat shall we launch today?\n\n"
                + "NAVIGATION ALERT: I couldn't load your saved tasks because line 1 is invalid: "
                + "the completion state must be 0 or 1. Starting with an empty task list.",
                nova.getWelcomeMessage());
    }
}
