package jonk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Tests command responses shared by Jonk's command-line and graphical interfaces.
 */
public class JonkTest {

    @TempDir
    private Path tempDirectory;

    @ParameterizedTest
    @ValueSource(strings = {
            "todo new task", "deadline new task /by 2026-09-17",
            "event new task /from 2026-09-17 /to 2026-09-18",
            "mark 1", "mark 2", "unmark 1", "unmark 2", "delete 1", "delete 2", "delete 3"
    })
    public void getResponse_saveFailure_rollsBackAndAllowsRetry(String command) throws IOException {
        Path dataFile = tempDirectory.resolve("jonk.txt");
        String originalData = "T | 0 | first\nT | 1 | middle\nT | 0 | last\n";
        Files.writeString(dataFile, originalData);
        Jonk jonk = new Jonk(dataFile.toString());
        String originalList = jonk.getResponse("list");

        // A non-empty directory forces a save failure without relying on OS permission rules.
        Files.delete(dataFile);
        Files.createDirectory(dataFile);
        Path sentinel = dataFile.resolve("keep.txt");
        Files.writeString(sentinel, originalData);

        assertEquals("Could not save tasks to " + dataFile + ". Your latest change was not applied.",
                jonk.getResponse(command));
        assertEquals(originalList, jonk.getResponse("list"));
        assertEquals(originalData, Files.readString(sentinel));
        try (Stream<Path> remainingFiles = Files.list(tempDirectory)) {
            assertEquals(List.of(dataFile), remainingFiles.toList());
        }

        Files.delete(sentinel);
        Files.delete(dataFile);
        Files.writeString(dataFile, originalData);
        String response = jonk.getResponse(command);

        assertFalse(response.contains("Could not save"));
        assertEquals(jonk.getResponse("list"), new Jonk(dataFile.toString()).getResponse("list"));
        if (command.startsWith("delete")) {
            assertEquals(2, Files.readAllLines(dataFile).size());
        } else if (command.startsWith("mark") || command.startsWith("unmark")) {
            int taskIndex = Integer.parseInt(command.substring(command.indexOf(' ') + 1)) - 1;
            String expectedStatus = command.startsWith("mark") ? "1" : "0";
            assertEquals(expectedStatus, Files.readAllLines(dataFile).get(taskIndex).split(" ")[2]);
        } else {
            assertEquals(4, Files.readAllLines(dataFile).size());
        }
    }

    @ParameterizedTest
    @CsvSource(delimiter = '|', value = {
            "mark 0 | That mission number is not on the flight plan.",
            "unmark -1 | That mission number is not on the flight plan.",
            "delete 2 | That mission number is not on the flight plan.",
            "mark | Mission control needs exactly one task number.",
            "unmark abc | Task coordinates must be a whole number.",
            "delete 2147483648 | Task coordinates must be a whole number.",
            "find | Send a keyword for Jonk to scan.",
            "list extra | Signal unclear. Type help to open the mission guide.",
            "bye extra | Signal unclear. Type help to open the mission guide.",
            "'' | Signal unclear. Type help to open the mission guide.",
            "todo | A todo mission needs a description.",
            "deadline task /by 2026-02-30 | Navigation dates must use yyyy-MM-dd format.",
            "event task /from 2026-09-17 /to invalid | Navigation dates must use yyyy-MM-dd format."
    })
    public void getResponse_invalidInput_preservesMemoryAndFile(String command, String expected) throws IOException {
        Path dataFile = tempDirectory.resolve("jonk.txt");
        String originalData = "T | 1 | original task\n";
        Files.writeString(dataFile, originalData);
        Jonk jonk = new Jonk(dataFile.toString());

        assertEquals(expected, jonk.getResponse(command));
        assertEquals("Flight plan, coming right up:\n\t1.[T][X] original task", jonk.getResponse("list"));
        assertEquals(originalData, Files.readString(dataFile));
    }

    @Test
    public void getResponse_restartAfterEachMutation_preservesAllTaskTypesAndStatuses() throws IOException {
        Path dataFile = tempDirectory.resolve("jonk.txt");
        String[] commands = {
                "todo read | notes \\ draft", "deadline submit /by 2024-02-29",
                "event meeting /from 2024-12-31 /to 2025-01-01", "mark 2", "mark 3", "unmark 3", "delete 1"
        };
        Jonk jonk = new Jonk(dataFile.toString());
        for (String command : commands) {
            String response = jonk.getResponse(command);
            assertFalse(response.contains("Could not"), response);
            String expectedList = jonk.getResponse("list");
            jonk = new Jonk(dataFile.toString());
            assertEquals(expectedList, jonk.getResponse("list"));
        }

        assertEquals(List.of("D | 1 | submit | 2024-02-29", "E | 0 | meeting | 2024-12-31 | 2025-01-01"),
                Files.readAllLines(dataFile));
        assertEquals("Scanner results—matching missions:\n\t1.[E][ ] meeting"
                + " (from: Dec 31 2024 to: Jan 1 2025)", jonk.getResponse("find meeting"));
        assertEquals("Scanner results—matching missions:", jonk.getResponse("find Meeting"));
    }

    @Test
    public void getWelcomeMessage_corruptLaterRecord_discardsPartialLoadAndPreservesFile() throws IOException {
        Path dataFile = tempDirectory.resolve("jonk.txt");
        String originalData = "T | 0 | valid first record\n\ninvalid\n";
        Files.writeString(dataFile, originalData);
        Jonk jonk = new Jonk(dataFile.toString());

        assertTrue(jonk.getWelcomeMessage().contains("invalid data at line 3"));
        assertEquals("Flight plan, coming right up:", jonk.getResponse("list"));
        assertEquals(originalData, Files.readString(dataFile));
    }

    @Test
    public void getResponse_help_returnsCommandReferenceWithoutCreatingDataFile() {
        Path dataFile = tempDirectory.resolve("jonk.txt");
        Jonk jonk = new Jonk(dataFile.toString());

        String response = jonk.getResponse("help");

        for (String command : new String[]{"help", "list", "todo DESCRIPTION", "deadline DESCRIPTION /by DATE",
                "event DESCRIPTION /from DATE /to DATE", "mark NUMBER", "unmark NUMBER", "delete NUMBER",
                "find KEYWORD", "bye"}) {
            assertTrue(response.contains("\n" + command + " - "), command);
        }
        assertTrue(response.contains("yyyy-MM-dd"));
        assertTrue(response.contains("Use task numbers from list, starting at 1"));
        assertTrue(response.contains("Find matches are case-sensitive"));
        assertEquals("Flight plan, coming right up:", jonk.getResponse("list"));
        assertFalse(Files.exists(dataFile));
    }

    @Test
    public void getResponse_helpWithSurroundingWhitespace_returnsHelp() {
        Jonk jonk = new Jonk(tempDirectory.resolve("jonk.txt").toString());

        assertEquals(jonk.getResponse("help"), jonk.getResponse(" \thelp \t"));
    }

    @Test
    public void getResponse_helpWithArguments_returnsUsageError() {
        Jonk jonk = new Jonk(tempDirectory.resolve("jonk.txt").toString());

        assertEquals("Extra signal detected. Type help on its own to open the mission guide.",
                jonk.getResponse("help todo"));
    }

    @Test
    public void getResponse_helpWithExistingTasks_preservesTasksAndDataFile() throws IOException {
        Path dataFile = tempDirectory.resolve("jonk.txt");
        String savedData = "T | 1 | read book\n\nD | 0 | return book | 2026-09-15\n";
        Files.writeString(dataFile, savedData);
        Jonk jonk = new Jonk(dataFile.toString());
        String originalList = jonk.getResponse("list");

        jonk.getResponse("help");
        jonk.getResponse("help todo");

        assertEquals(originalList, jonk.getResponse("list"));
        assertEquals(savedData, Files.readString(dataFile));
    }

    @Test
    public void getResponse_helpAfterLoadFailure_preservesInvalidData() throws IOException {
        Path dataFile = tempDirectory.resolve("jonk.txt");
        String savedData = "X | 0 | invalid task\n";
        Files.writeString(dataFile, savedData);
        Jonk jonk = new Jonk(dataFile.toString());

        assertTrue(jonk.getResponse("help").startsWith("Mission guide online. Here's what Jonk can do:"));
        assertEquals(savedData, Files.readString(dataFile));
    }

    @Test
    public void getWelcomeMessage_normalStartup_includesHelpHint() {
        Jonk jonk = new Jonk(tempDirectory.resolve("jonk.txt").toString());

        assertEquals("Systems online! I'm Jonk, your mission-control copilot."
                + "\nWhat shall we put on the flight plan?"
                + "\nType help to see the available commands.",
                jonk.getWelcomeMessage());
    }

    @Test
    public void getResponse_addThenList_returnsResponsesAndSavesTask() throws IOException {
        Path dataFile = tempDirectory.resolve("data/jonk.txt");
        Jonk jonk = new Jonk(dataFile.toString());

        String addResponse = jonk.getResponse("todo read book");
        String listResponse = jonk.getResponse("list");

        assertEquals("Mission logged:\n\t[T][ ] read book"
                + "\nFlight plan now holds 1 mission.", addResponse);
        assertEquals("Flight plan, coming right up:\n\t1.[T][ ] read book", listResponse);
        assertEquals(List.of("T | 0 | read book"), Files.readAllLines(dataFile));
    }

    @Test
    public void getResponse_missionLifecycle_usesThemedResponsesAndPreservesTaskBehavior() throws IOException {
        Path dataFile = tempDirectory.resolve("data/jonk.txt");
        Jonk jonk = new Jonk(dataFile.toString());
        jonk.getResponse("todo read book");

        assertEquals("Mission logged:\n\t[T][ ] return book\nFlight plan now holds 2 missions.",
                jonk.getResponse("todo return book"));
        assertEquals("Touchdown! Mission complete:\n\t[T][X] read book", jonk.getResponse("mark 1"));
        assertEquals("Course corrected. Mission active again:\n\t[T][ ] read book",
                jonk.getResponse("unmark 1"));
        assertEquals("Scanner results—matching missions:\n\t1.[T][ ] read book\n\t2.[T][ ] return book",
                jonk.getResponse("find book"));
        assertEquals("Mission scrubbed from the flight plan:\n\t[T][ ] read book"
                + "\nFlight plan now holds 1 mission.",
                jonk.getResponse("delete 1"));
        assertEquals("Mission scrubbed from the flight plan:\n\t[T][ ] return book"
                + "\nFlight plan now holds 0 missions.",
                jonk.getResponse("delete 1"));
        assertEquals("", Files.readString(dataFile));
    }

    @Test
    public void getResponse_invalidCommand_returnsErrorWithoutThrowing() {
        Jonk jonk = new Jonk(tempDirectory.resolve("jonk.txt").toString());

        assertEquals("Signal unclear. Type help to open the mission guide.", jonk.getResponse("blah"));
    }

    @Test
    public void getResponse_invalidDate_returnsFriendlyError() {
        Jonk jonk = new Jonk(tempDirectory.resolve("jonk.txt").toString());

        assertEquals("Navigation dates must use yyyy-MM-dd format.",
                jonk.getResponse("deadline invalid /by 2019-02-29"));
    }

    @Test
    public void getResponse_bye_returnsFarewell() {
        Jonk jonk = new Jonk(tempDirectory.resolve("jonk.txt").toString());

        assertEquals("Mission control signing off. Clear skies, explorer!", jonk.getResponse("bye"));
    }

    @Test
    public void getWelcomeMessage_invalidStoredData_includesLoadError() throws IOException {
        Path dataFile = tempDirectory.resolve("jonk.txt");
        Files.writeString(dataFile, "X | 0 | invalid task\n");
        Jonk jonk = new Jonk(dataFile.toString());

        String welcomeMessage = jonk.getWelcomeMessage();

        assertTrue(welcomeMessage.startsWith("Systems online! I'm Jonk, your mission-control copilot."));
        assertTrue(welcomeMessage.contains("invalid data at line 1"));
    }
}
