package grower;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import grower.ui.ResponseType;

/**
 * Verifies command processing and persistence across application sessions.
 */
public class GrowerTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void getResponse_addThenList_returnsCommandOutput() {
        Grower grower = new Grower(getDataFilePath());

        String addResponse = grower.getResponse("todo read book");
        String listResponse = grower.getResponse("list");

        assertTrue(addResponse.contains("Added:"));
        assertTrue(addResponse.contains("read book"));
        assertTrue(listResponse.contains("1. [T][ ] read book"));
    }

    @Test
    public void getResponse_find_returnsMatchesInOrderWithConsecutiveNumbers() {
        Grower grower = new Grower(getDataFilePath());
        grower.getResponse("todo read book");
        grower.getResponse("todo exercise");
        grower.getResponse("todo read notes");

        assertEquals("1. [T][ ] read book" + System.lineSeparator()
                + "2. [T][ ] read notes", grower.getResponse("find read"));
        assertEquals("No results!!!!", grower.getResponse("find missing"));
    }

    @Test
    public void constructor_existingData_restoresTasksForGuiAndCliLogic() {
        Grower firstSession = new Grower(getDataFilePath());
        firstSession.getResponse("todo persist me");

        Grower secondSession = new Grower(getDataFilePath());

        assertTrue(secondSession.getResponse("list").contains("persist me"));
    }

    @Test
    public void getResponse_sort_updatesTaskNumbersAndPersistsOrder() {
        Grower grower = new Grower(getDataFilePath());
        grower.getResponse("event meeting /from 12/9/2026 1000 /to 12/9/2026 1400");
        grower.getResponse("deadline submit work /by 12/9/2026 1200");
        grower.getResponse("todo read book");

        String sortedResponse = grower.getResponse("sort");

        assertTrue(sortedResponse.contains("1. [T][ ] read book"));
        assertTrue(sortedResponse.contains("2. [D][ ] submit work"));
        assertTrue(sortedResponse.contains("3. [E][ ] meeting"));
        assertTrue(grower.isRunning());
        Grower restored = new Grower(getDataFilePath());
        assertEquals(sortedResponse, restored.getResponse("list"));
        restored.getResponse("mark 1");
        assertTrue(restored.getResponse("list").contains("1. [T][X] read book"));
    }

    @Test
    public void getResponse_invalidCommand_returnsErrorMessage() {
        Grower grower = new Grower(getDataFilePath());

        String response = grower.getResponse("dance");

        assertTrue(response.contains("beyond my strength"));
        assertTrue(grower.isRunning());
    }

    @Test
    public void getResponse_bye_stopsFurtherInputLoop() {
        Grower grower = new Grower(getDataFilePath());

        String response = grower.getResponse("bye");

        assertTrue(response.contains("May the shade of many trees provide comfort as you depart!"));
        assertFalse(grower.isRunning());
    }

    @Test
    public void getResponse_failedSave_preservesTasksAndAllowsRetry() throws IOException {
        Path parent = temporaryDirectory.resolve("data");
        // Start with a missing file, then make its parent unusable after loading.
        Grower grower = new Grower(parent.resolve("tasks.txt").toString());
        Files.writeString(parent, "blocks directory creation");

        String response = grower.getResponse("todo read book");
        assertTrue(response.contains("not applied"));
        assertFalse(response.contains("Added:"));
        assertEquals(ResponseType.ERROR, grower.getResponseType());
        assertFalse(grower.getResponse("list").contains("read book"));

        Files.delete(parent);
        assertTrue(grower.getResponse("todo read book").contains("Added:"));
        assertTrue(new Grower(parent.resolve("tasks.txt").toString())
                .getResponse("list").contains("1. [T][ ] read book"));
    }

    @Test
    public void getResponse_failedMarkDeleteOrSort_preservesOriginalState() throws IOException {
        Grower grower = new Grower(getDataFilePath());
        grower.getResponse("deadline later /by 1/1/2027 1200");
        grower.getResponse("todo first");
        String original = grower.getResponse("list");
        Path file = Path.of(getDataFilePath());
        Files.delete(file);
        Files.createDirectory(file);
        Files.writeString(file.resolve("blocker"), "keep");

        for (String command : List.of("mark 1", "delete 1", "sort")) {
            assertTrue(grower.getResponse(command).contains("not applied"));
            assertEquals(original, grower.getResponse("list"));
        }
        grower.getResponse("bye");
        assertFalse(grower.isRunning());
    }

    @Test
    public void constructor_corruptFile_preservesOriginalAndReportsProblem() throws IOException {
        Path file = Path.of(getDataFilePath());
        String contents = "T | 0 | valid\nT | invalid | damaged\n";
        Files.writeString(file, contents);
        Grower grower = new Grower(getDataFilePath());

        assertTrue(grower.getStartupMessage().contains("disabled"));
        assertTrue(grower.getResponse("list").contains("valid"));
        assertTrue(grower.getResponse("todo new task").contains("disabled"));
        grower.getResponse("bye");
        assertEquals(contents, Files.readString(file));
    }

    @Test
    public void getResponse_readOnlyCommands_doNotCreateDataFile() {
        Grower grower = new Grower(getDataFilePath());
        for (String command : List.of("list", "find missing", "echo hello", "sort", "bye")) {
            grower.getResponse(command);
        }
        assertFalse(Files.exists(Path.of(getDataFilePath())));
    }

    @Test
    public void constructor_unreadableFile_disablesChangesButAllowsExit() {
        Grower grower = new Grower(temporaryDirectory.toString());
        assertTrue(grower.getStartupMessage().contains("Could not load"));
        assertTrue(grower.getResponse("todo task").contains("disabled"));
        grower.getResponse("bye");
        assertFalse(grower.isRunning());
    }

    /**
     * Returns an isolated storage location for a test.
     */
    private String getDataFilePath() {
        return temporaryDirectory.resolve("grower.txt").toString();
    }
}
