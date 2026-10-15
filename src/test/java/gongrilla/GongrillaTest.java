package gongrilla;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class GongrillaTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    void getResponse_addTodo_returnsConfirmationAndTracksCommandType() {
        Gongrilla gongrilla = new Gongrilla(temporaryDirectory.resolve("gongrilla.txt"));

        String response = gongrilla.getResponse("todo read book");

        assertTrue(response.contains("read book"));
        assertEquals("AddCommand", gongrilla.getCommandType());
    }

    @Test
    void getResponse_invalidCommand_returnsErrorAndTracksError() {
        Gongrilla gongrilla = new Gongrilla(temporaryDirectory.resolve("gongrilla.txt"));

        String response = gongrilla.getResponse("unknown command");

        assertTrue(response.contains("no know"));
        assertTrue(response.contains("Type help. Gongrilla show commands."));
        assertEquals("Error", gongrilla.getCommandType());
    }

    @Test
    void getResponse_help_listsEveryCommandWithoutChangingTasksOrStorage() throws Exception {
        Path dataPath = temporaryDirectory.resolve("gongrilla.txt");
        Gongrilla gongrilla = new Gongrilla(dataPath);
        gongrilla.getResponse("todo buy bananas");
        String savedData = Files.readString(dataPath);
        String taskList = gongrilla.getResponse("list");

        String response = gongrilla.getResponse("  HeLp  ");

        assertEquals("HelpCommand", gongrilla.getCommandType());
        for (String command : List.of("help", "list", "todo", "deadline", "event",
                "find", "mark", "unmark", "delete", "bye")) {
            assertTrue(response.lines().anyMatch(line -> line.startsWith(command + " ")), command);
        }
        assertTrue(response.contains("D/M/YYYY"));
        assertTrue(response.contains("HHMM"));
        assertEquals(savedData, Files.readString(dataPath));
        assertEquals(taskList, gongrilla.getResponse("list"));
    }

    @Test
    void getResponse_helpOnEmptyList_doesNotCreateDataFile() {
        Path dataPath = temporaryDirectory.resolve("new.txt");
        Gongrilla gongrilla = new Gongrilla(dataPath);

        assertTrue(gongrilla.getResponse("help").contains("help - show this guide"));
        assertFalse(Files.exists(dataPath));
    }

    @Test
    void getResponse_helpWithExtraText_rejectsCommandAndSuggestsHelp() {
        Gongrilla gongrilla = new Gongrilla(temporaryDirectory.resolve("gongrilla.txt"));
        for (String input : List.of("help extra", "helpful")) {
            assertTrue(gongrilla.getResponse(input).contains("Type help."));
            assertEquals("Error", gongrilla.getCommandType());
        }
    }
}
