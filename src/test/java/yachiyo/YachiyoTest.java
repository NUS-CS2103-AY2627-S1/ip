package yachiyo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static yachiyo.exception.ErrorCategory.INVALID_OPERATION;
import static yachiyo.exception.ErrorCategory.SYSTEM_ERROR;
import static yachiyo.exception.ErrorCategory.WARNING;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class YachiyoTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void getResponse_addThenList_taskStoredAndShown() {
        Path dataFilePath = temporaryDirectory.resolve("yachiyo.txt");
        Yachiyo yachiyo = new Yachiyo(dataFilePath);

        String addResponse = yachiyo.getResponse("todo prepare slides");
        String listResponse = yachiyo.getResponse("list");

        assertTrue(addResponse.contains("I've added this to our lineup"));
        assertTrue(addResponse.contains("[T][ ] prepare slides"));
        assertTrue(listResponse.contains("1. [T][ ] prepare slides"));
    }

    @Test
    public void getResponse_newInstance_taskLoadedFromStorage() {
        Path dataFilePath = temporaryDirectory.resolve("yachiyo.txt");
        new Yachiyo(dataFilePath).getResponse("todo preserve this task");

        String response = new Yachiyo(dataFilePath).getResponse("list");

        assertTrue(response.contains("1. [T][ ] preserve this task"));
    }

    @Test
    public void getResponse_editDescription_taskUpdatedAndStored() {
        Path dataFilePath = temporaryDirectory.resolve("yachiyo.txt");
        Yachiyo yachiyo = new Yachiyo(dataFilePath);
        yachiyo.getResponse("todo prepare slides");

        String editResponse = yachiyo.getResponse(
                "edit 1 /description prepare presentation slides"
        );
        String storedTaskResponse = new Yachiyo(dataFilePath).getResponse("list");

        assertTrue(editResponse.contains("I've updated this task"));
        assertTrue(editResponse.contains("[T][ ] prepare presentation slides"));
        assertTrue(storedTaskResponse.contains("1. [T][ ] prepare presentation slides"));
    }

    @Test
    public void getResponse_editDeadline_taskUpdatedAndStored() {
        Path dataFilePath = temporaryDirectory.resolve("yachiyo.txt");
        Yachiyo yachiyo = new Yachiyo(dataFilePath);
        yachiyo.getResponse("deadline submit report /by 20/9/2026 1700");

        String editResponse = yachiyo.getResponse("edit 1 /by 21/9/2026 1800");
        String storedTaskResponse = new Yachiyo(dataFilePath).getResponse("list");

        assertTrue(editResponse.contains("I've updated this task"));
        assertTrue(editResponse.contains("by: Sep 21 2026, 6:00 PM"));
        assertTrue(storedTaskResponse.contains("by: Sep 21 2026, 6:00 PM"));
    }

    @Test
    public void getResponse_eventEditedAndMarked_changesPersistAcrossRestart() {
        Path dataFilePath = temporaryDirectory.resolve("yachiyo.txt");
        Yachiyo yachiyo = new Yachiyo(dataFilePath);
        yachiyo.getResponse(
                "event Orientation /from 20/8/2026 0900 /to 22/8/2026 1700");

        String markResponse = yachiyo.getResponse("mark 1");
        yachiyo.getResponse("edit 1 /from 20/8/2026 1000");
        yachiyo.getResponse("edit 1 /to 23/8/2026 1800");
        Yachiyo reloadedYachiyo = new Yachiyo(dataFilePath);
        String storedTaskResponse = reloadedYachiyo.getResponse("list");

        assertTrue(markResponse.contains("Everything in our lineup is complete"));
        assertTrue(storedTaskResponse.contains(
                "1. [E][X] Orientation (from: Aug 20 2026, 10:00 AM, "
                        + "to: Aug 23 2026, 6:00 PM)"));
        assertEquals(1, reloadedYachiyo.getTaskCount());
        assertEquals(0, reloadedYachiyo.getRemainingTaskCount());
    }

    @Test
    public void getResponse_unmarkAndDelete_changesPersistAcrossRestart() {
        Path dataFilePath = temporaryDirectory.resolve("yachiyo.txt");
        Yachiyo yachiyo = new Yachiyo(dataFilePath);
        yachiyo.getResponse("todo Read book");
        yachiyo.getResponse("deadline Submit report /by 20/9/2026 1700");
        yachiyo.getResponse("mark 1");

        String unmarkResponse = yachiyo.getResponse("unmark 1");
        String deleteResponse = yachiyo.getResponse("delete 2");
        Yachiyo reloadedYachiyo = new Yachiyo(dataFilePath);
        String storedTaskResponse = reloadedYachiyo.getResponse("list");

        assertTrue(unmarkResponse.contains("marked it as not done"));
        assertTrue(deleteResponse.contains("taken this task out of our lineup"));
        assertTrue(storedTaskResponse.contains("1. [T][ ] Read book"));
        assertFalse(storedTaskResponse.contains("Submit report"));
        assertEquals(1, reloadedYachiyo.getTaskCount());
        assertEquals(1, reloadedYachiyo.getRemainingTaskCount());
    }

    @Test
    public void getResponse_find_matchesWithoutChangingStoredTasks() throws IOException {
        Path dataFilePath = temporaryDirectory.resolve("yachiyo.txt");
        Yachiyo yachiyo = new Yachiyo(dataFilePath);
        yachiyo.getResponse("todo Read book");
        yachiyo.getResponse("deadline Submit report /by 20/8/2026 1700");
        yachiyo.getResponse("todo Return BOOK");
        String storedDataBeforeSearch = Files.readString(dataFilePath);

        String matchingResponse = yachiyo.getResponse("find book");
        String noMatchResponse = yachiyo.getResponse("find presentation");

        assertTrue(matchingResponse.contains("matching \"book\""));
        assertTrue(matchingResponse.contains("1. [T][ ] Read book"));
        assertTrue(matchingResponse.contains("3. [T][ ] Return BOOK"));
        assertFalse(matchingResponse.contains("Submit report"));
        assertEquals("I couldn't find any tasks matching \"presentation\".",
                noMatchResponse);
        assertEquals(storedDataBeforeSearch, Files.readString(dataFilePath));
    }

    @Test
    public void getResponse_onDate_matchesWithoutChangingStoredTasks() throws IOException {
        Path dataFilePath = temporaryDirectory.resolve("yachiyo.txt");
        Yachiyo yachiyo = new Yachiyo(dataFilePath);
        yachiyo.getResponse("todo Read book");
        yachiyo.getResponse("deadline Submit report /by 20/8/2026 1700");
        yachiyo.getResponse(
                "event Orientation /from 19/8/2026 0900 /to 21/8/2026 1700");
        yachiyo.getResponse("deadline Pay bill /by 22/8/2026 1200");
        String storedDataBeforeSearch = Files.readString(dataFilePath);

        String matchingResponse = yachiyo.getResponse("on 20/8/2026");
        String noMatchResponse = yachiyo.getResponse("on 18/8/2026");

        assertTrue(matchingResponse.contains("on Aug 20 2026"));
        assertTrue(matchingResponse.contains("2. [D][ ] Submit report"));
        assertTrue(matchingResponse.contains("3. [E][ ] Orientation"));
        assertFalse(matchingResponse.contains("Read book"));
        assertFalse(matchingResponse.contains("Pay bill"));
        assertEquals("There are no deadlines or events on Aug 18 2026.",
                noMatchResponse);
        assertEquals(storedDataBeforeSearch, Files.readString(dataFilePath));
    }

    @Test
    public void getResponse_invalidCommand_errorReturned() {
        Yachiyo yachiyo = new Yachiyo(temporaryDirectory.resolve("yachiyo.txt"));

        String response = yachiyo.getResponse("dance");

        assertEquals("Oh? I don’t recognize that command just yet. Could you try another one?", response);
        assertEquals(Optional.of(WARNING), yachiyo.getLastErrorCategory());
    }

    @Test
    public void getResponse_invalidOperation_errorCategoryReturned() {
        Yachiyo yachiyo = new Yachiyo(temporaryDirectory.resolve("yachiyo.txt"));

        yachiyo.getResponse("delete 1");

        assertEquals(Optional.of(INVALID_OPERATION), yachiyo.getLastErrorCategory());
    }

    @Test
    public void getResponse_malformedData_systemErrorCategoryReturned() throws IOException {
        Path dataFilePath = temporaryDirectory.resolve("yachiyo.txt");
        Files.writeString(dataFilePath, "malformed task data");
        Yachiyo yachiyo = new Yachiyo(dataFilePath);

        String response = yachiyo.getResponse("list");

        assertEquals("Oh no! Some task data in the file isn't in the expected format.", response);
        assertEquals(Optional.of(SYSTEM_ERROR), yachiyo.getLastErrorCategory());
    }

    @Test
    public void getResponse_byeWithMalformedData_farewellReturnedAndExitRequested()
            throws IOException {
        Path dataFilePath = temporaryDirectory.resolve("yachiyo.txt");
        Files.writeString(dataFilePath, "malformed task data");
        Yachiyo yachiyo = new Yachiyo(dataFilePath);

        String response = yachiyo.getResponse("bye");

        assertEquals("Until we meet again. Take care!~", response);
        assertTrue(yachiyo.isExitRequested());
        assertTrue(yachiyo.getLastErrorCategory().isEmpty());
        assertFalse(yachiyo.hasLoadedTasks());
    }

    @Test
    public void getResponse_successAfterError_errorCategoryCleared() {
        Yachiyo yachiyo = new Yachiyo(temporaryDirectory.resolve("yachiyo.txt"));
        yachiyo.getResponse("dance");

        yachiyo.getResponse("list");

        assertFalse(yachiyo.getLastErrorCategory().isPresent());
    }

    @Test
    public void initialize_savedTasks_taskCountsReturned() {
        Path dataFilePath = temporaryDirectory.resolve("yachiyo.txt");
        Yachiyo originalYachiyo = new Yachiyo(dataFilePath);
        originalYachiyo.getResponse("todo prepare slides");
        originalYachiyo.getResponse("deadline submit report /by 20/9/2026 1700");
        originalYachiyo.getResponse("mark 1");
        Yachiyo reloadedYachiyo = new Yachiyo(dataFilePath);

        String response = reloadedYachiyo.initialize();

        assertEquals("", response);
        assertTrue(reloadedYachiyo.hasLoadedTasks());
        assertEquals(2, reloadedYachiyo.getTaskCount());
        assertEquals(1, reloadedYachiyo.getRemainingTaskCount());
    }

    @Test
    public void initialize_malformedData_systemErrorReturnedAndTasksUnavailable() throws IOException {
        Path dataFilePath = temporaryDirectory.resolve("yachiyo.txt");
        Files.writeString(dataFilePath, "malformed task data");
        Yachiyo yachiyo = new Yachiyo(dataFilePath);

        String response = yachiyo.initialize();

        assertEquals("Oh no! Some task data in the file isn't in the expected format.", response);
        assertEquals(Optional.of(SYSTEM_ERROR), yachiyo.getLastErrorCategory());
        assertFalse(yachiyo.hasLoadedTasks());
    }

    @Test
    public void getGreeting_guiOpened_introductionReturned() {
        Yachiyo yachiyo = new Yachiyo(temporaryDirectory.resolve("yachiyo.txt"));

        String greeting = yachiyo.getGreeting();

        assertTrue(greeting.endsWith("! Yachiyo here!\nWhat shall we accomplish today?"));
    }

    @Test
    public void getResponse_bye_farewellReturnedWithoutBreaker() {
        Yachiyo yachiyo = new Yachiyo(temporaryDirectory.resolve("yachiyo.txt"));

        String response = yachiyo.getResponse("bye");

        assertEquals("Until we meet again. Take care!~", response);
        assertTrue(yachiyo.isExitRequested());
    }
}
