package lobby;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests Lobby's command-response interface used by the graphical UI.
 */
public class LobbyTest {

    @TempDir
    private Path temporaryDirectory;

    @Test
    public void processCommand_unknownCommand_clearsInputButStillReportsError() {
        Lobby lobby = new Lobby(temporaryDirectory.resolve("lobby.txt").toString());
        for (String command : List.of("hello", "lsit", "  HELO  ", "t odo buy milk")) {
            Lobby.Response response = lobby.processCommand(command);
            assertTrue(response.isError(), command);
            assertFalse(response.shouldRetainInput(), command);
        }
    }

    @Test
    public void processCommand_recognizedInvalidCommand_retainsInputForCorrection() {
        Lobby lobby = new Lobby(temporaryDirectory.resolve("lobby.txt").toString());
        for (String command : List.of("todo", "  TODO  ", "deadline report /by Monday", "mark abc", "find")) {
            Lobby.Response response = lobby.processCommand(command);
            assertTrue(response.isError(), command);
            assertTrue(response.shouldRetainInput(), command);
        }
    }

    @Test
    public void processCommand_invalidThenCorrectedCommand_reportsStatusAndExecutesOnce() {
        Lobby lobby = new Lobby(temporaryDirectory.resolve("lobby.txt").toString());
        for (String command : List.of("hello", "todo", "deadline report /by Monday", "delete 1", "find")) {
            assertTrue(lobby.processCommand(command).isError(), command);
        }
        Lobby.Response invalidDate = lobby.processCommand("deadline report /by Monday");
        assertTrue(invalidDate.text().contains("yyyy-MM-dd"));

        Lobby.Response corrected = lobby.processCommand("deadline report /by 2026-09-20");
        assertFalse(corrected.isError());
        assertFalse(corrected.shouldRetainInput());
        assertTrue(corrected.text().contains("Now you have 1 task in the list."));
        assertFalse(lobby.processCommand("list").isError());
        assertFalse(lobby.processCommand("find missing").isError());
        assertFalse(lobby.processCommand("bye").isError());
    }

    @Test
    public void processCommand_failedSaves_reportsErrorsAndKeepsTasks() throws IOException {
        Path saveFile = temporaryDirectory.resolve("lobby.txt");
        Lobby lobby = new Lobby(saveFile.toString());
        lobby.processCommand("todo original");
        String originalList = lobby.getResponse("list");
        blockSaving(saveFile);

        for (String command : List.of("todo unsaved", "mark 1", "unmark 1", "delete 1")) {
            Lobby.Response response = lobby.processCommand(command);
            assertTrue(response.isError(), command);
            assertTrue(response.shouldRetainInput(), command);
            assertTrue(response.text().contains("I couldn't save your changes."), command);
            assertEquals(originalList, lobby.getResponse("list"));
        }
    }

    @Test
    public void getResponse_addAndListTask_returnsUpdatedResponses() {
        Lobby lobby = new Lobby(temporaryDirectory.resolve("lobby.txt").toString());

        String addResponse = lobby.getResponse("todo read the JavaFX tutorial");
        String listResponse = lobby.getResponse("list");

        assertTrue(addResponse.contains("[T][ ] read the JavaFX tutorial"));
        assertTrue(addResponse.contains("Now you have 1 task in the list."));
        assertTrue(listResponse.contains("1.[T][ ] read the JavaFX tutorial"));
    }

    @Test
    public void getResponse_unknownCommand_returnsGuidance() {
        Lobby lobby = new Lobby(temporaryDirectory.resolve("lobby.txt").toString());

        String response = lobby.getResponse("hello");

        assertEquals(" Please use todo, deadline, event, list, find, mark, unmark, delete, or bye.", response);
    }

    @Test
    public void getResponse_commandWithOuterWhitespace_processesTrimmedCommand() {
        Lobby lobby = new Lobby(temporaryDirectory.resolve("lobby.txt").toString());

        String response = lobby.getResponse("  todo revise notes  ");

        assertTrue(response.contains("[T][ ] revise notes"));
    }

    @Test
    public void getResponse_invalidCommands_returnsErrorsWithoutChangingTasks() {
        Lobby lobby = new Lobby(temporaryDirectory.resolve("lobby.txt").toString());
        String emptyList = lobby.getResponse("list");
        String[][] invalidCommands = {
            {"todo", "A to-do needs a description. Try: todo <description>."},
            {"deadline report /by", "A deadline needs a time after /by."},
            {"event meeting /from 2pm /to", "An event needs an end time after /to."},
            {"find", "A search needs keywords. Try: find <keywords>."},
            {"mark", "Please use mark followed by a task number."},
            {"unmark wrong", "Please use unmark followed by a task number."},
            {"delete 1.5", "Please use delete followed by a task number."},
            {"mark 0", "Please enter the number of a task in the list."},
            {"unmark 1", "Please enter the number of a task in the list."},
            {"delete -1", "Please enter the number of a task in the list."}
        };

        for (String[] invalidCommand : invalidCommands) {
            assertEquals(" " + invalidCommand[1], lobby.getResponse(invalidCommand[0]), invalidCommand[0]);
            assertEquals(emptyList, lobby.getResponse("list"));
        }
    }

    @Test
    public void getResponse_search_filtersTasksAndLeavesSavedDataUnchanged() throws IOException {
        Path saveFile = temporaryDirectory.resolve("lobby.txt");
        Lobby lobby = new Lobby(saveFile.toString());
        lobby.getResponse("todo Finish marking lab reports");
        lobby.getResponse("todo prepare lab");
        lobby.getResponse("mark 1");
        String originalFile = Files.readString(saveFile);
        String originalList = lobby.getResponse("list");
        String heading = " Here are the matching tasks in your list:" + System.lineSeparator();

        assertEquals(heading + " 1.[T][X] Finish marking lab reports",
                lobby.getResponse("find LAB mark"));
        assertEquals(heading + " 1.[T][ ] prepare lab", lobby.getResponse("find prepare"));
        assertEquals(" No matching tasks found.", lobby.getResponse("find essays"));
        assertEquals(" No matching tasks found.", lobby.getResponse("find /status done"));
        assertEquals(originalList, lobby.getResponse("list"));
        assertEquals(originalFile, Files.readString(saveFile));
        lobby.getResponse("todo check /status done endpoint");
        assertEquals(heading + " 1.[T][ ] check /status done endpoint", lobby.getResponse("find /status done"));
    }

    @Test
    public void getResponse_mutatingCommands_persistsTaskTypesAndCompletionStates() throws IOException {
        Path saveFile = temporaryDirectory.resolve("lobby.txt");
        Lobby lobby = new Lobby(saveFile.toString());
        lobby.getResponse("todo first");
        lobby.getResponse("deadline report /by 2026-09-30");
        lobby.getResponse("event meeting /from 2pm /to 3pm");
        lobby.getResponse("mark 1");
        lobby.getResponse("mark 2");
        lobby.getResponse("unmark 1");

        assertEquals(List.of("T | 0 | first", "D | 1 | report | 2026-09-30", "E | 0 | meeting | 2pm | 3pm"),
                Files.readAllLines(saveFile));

        lobby.getResponse("delete 2");

        assertEquals(List.of("T | 0 | first", "E | 0 | meeting | 2pm | 3pm"), Files.readAllLines(saveFile));
    }

    @Test
    public void getResponse_failedSaves_restoresTasksAndSuppressesSuccessResponses() throws IOException {
        Path saveFile = temporaryDirectory.resolve("lobby.txt");
        Files.write(saveFile, List.of("T | 0 | first", "T | 1 | second"));
        Lobby lobby = new Lobby(saveFile.toString());
        String originalList = lobby.getResponse("list");
        blockSaving(saveFile);
        String[] commands = {
            "todo unsaved", "deadline report /by 2026-09-30", "event meeting /from 2pm /to 3pm",
            "mark 1", "mark 2", "unmark 1", "unmark 2", "delete 1", "delete 2"
        };

        for (String command : commands) {
            assertEquals(" I couldn't save your changes. Please check that data/lobby.txt is writable.",
                    lobby.getResponse(command), command);
            assertEquals(originalList, lobby.getResponse("list"), command);
        }
    }

    /**
     * Forces writes to fail by replacing the loaded save file with a nonempty directory.
     */
    private void blockSaving(Path saveFile) throws IOException {
        Files.delete(saveFile);
        Files.createDirectory(saveFile);
        Files.writeString(saveFile.resolve("blocker.txt"), "Prevent replacement of this directory.");
    }

    @Test
    public void getResponse_saveFails_restoresTasksForEveryMutation() throws IOException {
        List<String> commands = List.of("todo unsaved", "mark 2", "unmark 1", "delete 2",
                "mark 1", "unmark 2");
        for (int i = 0; i < commands.size(); i++) {
            Path saveFile = temporaryDirectory.resolve("case-" + i).resolve("lobby.txt");
            Files.createDirectories(saveFile.getParent());
            Files.write(saveFile, List.of("T | 1 | first", "T | 0 | middle", "T | 0 | last"));
            Lobby lobby = new Lobby(saveFile.toString());
            String originalList = lobby.getResponse("list");
            // A non-empty directory at the save path reliably prevents replacement on every platform.
            Files.delete(saveFile);
            Files.createDirectory(saveFile);
            Files.writeString(saveFile.resolve("blocker.txt"), "Prevent replacement");

            String response = lobby.getResponse(commands.get(i));

            assertTrue(response.contains("I couldn't save your changes."), commands.get(i));
            assertEquals(originalList, lobby.getResponse("list"), commands.get(i));
        }
    }

    @Test
    public void getResponse_invalidTaskNumbers_returnsGuidance() {
        Lobby lobby = new Lobby(temporaryDirectory.resolve("lobby.txt").toString());
        lobby.getResponse("todo existing task");

        for (String command : List.of("mark 0", "unmark 2", "delete -1")) {
            assertEquals(" Please enter the number of a task in the list.", lobby.getResponse(command));
        }
    }
}
