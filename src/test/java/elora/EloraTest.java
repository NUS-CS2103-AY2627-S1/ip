package elora;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Integration-style tests that drive Elora through {@link Elora#getResponse}
 * exactly as the GUI would, covering the command pipeline end to end
 * (parsing, validation, task list mutation, and persistence) rather than
 * any single class in isolation.
 */
class EloraTest {

    @TempDir
    Path tempDir;

    private Elora newElora() {
        return new Elora(tempDir.resolve("elora.txt").toString());
    }

    @Test
    void todo_validDescription_addsTaskSuccessfully() {
        Response response = newElora().getResponse("todo read book");
        assertFalse(response.isError());
        assertTrue(response.getText().contains("read book"));
    }

    @Test
    void todo_missingDescription_returnsError() {
        Response response = newElora().getResponse("todo");
        assertTrue(response.isError());
    }

    @Test
    void todo_leadingAndTrailingWhitespace_isStillParsedCorrectly() {
        Response response = newElora().getResponse("   todo read book   ");
        assertFalse(response.isError());
        assertTrue(response.getText().contains("read book"));
    }

    @Test
    void todo_pipeCharacterInDescription_returnsError() {
        Response response = newElora().getResponse("todo read | book");
        assertTrue(response.isError());
    }

    @Test
    void todo_duplicateDescription_returnsError() {
        Elora elora = newElora();
        elora.getResponse("todo read book");
        Response response = elora.getResponse("todo read book");
        assertTrue(response.isError());
    }

    @Test
    void deadline_validInput_addsTaskSuccessfully() {
        Response response = newElora().getResponse("deadline return book /by 2019-10-15");
        assertFalse(response.isError());
        assertTrue(response.getText().contains("Oct 15 2019"));
    }

    @Test
    void deadline_missingByKeyword_returnsError() {
        Response response = newElora().getResponse("deadline return book");
        assertTrue(response.isError());
    }

    @Test
    void deadline_nonExistentDate_returnsError() {
        Response response = newElora().getResponse("deadline return book /by 2019-02-30");
        assertTrue(response.isError());
    }

    @Test
    void deadline_duplicateByKeyword_returnsError() {
        Response response = newElora().getResponse(
                "deadline return book /by 2019-10-15 /by 2019-10-20");
        assertTrue(response.isError());
    }

    @Test
    void deadline_sameDescriptionAndDateTwice_returnsErrorOnSecondAdd() {
        Elora elora = newElora();
        elora.getResponse("deadline return book /by 2019-10-15");
        Response response = elora.getResponse("deadline return book /by 2019-10-15");
        assertTrue(response.isError());
    }

    @Test
    void event_validInput_addsTaskSuccessfully() {
        Response response = newElora().getResponse("event meeting /from Mon 2pm /to 4pm");
        assertFalse(response.isError());
        assertTrue(response.getText().contains("meeting"));
    }

    @Test
    void event_duplicateFromKeyword_returnsError() {
        Response response = newElora().getResponse(
                "event meeting /from Mon 2pm /from Tue 2pm /to 4pm");
        assertTrue(response.isError());
    }

    @Test
    void event_duplicateToKeyword_returnsError() {
        Response response = newElora().getResponse(
                "event meeting /from Mon 2pm /to 4pm /to 5pm");
        assertTrue(response.isError());
    }

    @Test
    void event_endDateBeforeStartDate_returnsError() {
        Response response = newElora().getResponse(
                "event trip /from 2019-10-20 /to 2019-10-15");
        assertTrue(response.isError());
    }

    @Test
    void event_endDateSameAsStartDate_returnsError() {
        Response response = newElora().getResponse(
                "event trip /from 2019-10-15 /to 2019-10-15");
        assertTrue(response.isError());
    }

    @Test
    void event_endDateAfterStartDate_addsTaskSuccessfully() {
        Response response = newElora().getResponse(
                "event trip /from 2019-10-15 /to 2019-10-20");
        assertFalse(response.isError());
    }

    @Test
    void event_freeTextTimes_skipsChronologicalCheck() {
        Response response = newElora().getResponse("event meeting /from 4pm /to 2pm");
        assertFalse(response.isError());
    }

    @Test
    void mark_validIndex_marksTaskDone() {
        Elora elora = newElora();
        elora.getResponse("todo read book");
        Response response = elora.getResponse("mark 1");
        assertFalse(response.isError());
        assertTrue(response.getText().contains("[X]"));
    }

    @Test
    void mark_outOfRangeIndex_returnsError() {
        Elora elora = newElora();
        elora.getResponse("todo read book");
        assertTrue(elora.getResponse("mark 5").isError());
    }

    @Test
    void mark_nonNumericIndex_returnsError() {
        Elora elora = newElora();
        elora.getResponse("todo read book");
        assertTrue(elora.getResponse("mark abc").isError());
    }

    @Test
    void unmark_validIndex_marksTaskNotDone() {
        Elora elora = newElora();
        elora.getResponse("todo read book");
        elora.getResponse("mark 1");
        Response response = elora.getResponse("unmark 1");
        assertFalse(response.isError());
        assertTrue(response.getText().contains("[ ]"));
    }

    @Test
    void delete_validIndex_removesTask() {
        Elora elora = newElora();
        elora.getResponse("todo read book");
        Response response = elora.getResponse("delete 1");
        assertFalse(response.isError());
        assertTrue(elora.getResponse("list").getText().contains("Here are the tasks"));
        assertFalse(elora.getResponse("list").getText().contains("read book"));
    }

    @Test
    void list_emptyTaskList_stillReturnsHeaderNoError() {
        Response response = newElora().getResponse("list");
        assertFalse(response.isError());
    }

    @Test
    void find_matchingKeyword_returnsMatch() {
        Elora elora = newElora();
        elora.getResponse("todo read book");
        Response response = elora.getResponse("find book");
        assertFalse(response.isError());
        assertTrue(response.getText().contains("read book"));
    }

    @Test
    void find_missingKeyword_returnsError() {
        assertTrue(newElora().getResponse("find").isError());
    }

    @Test
    void sort_deadlinesOutOfOrder_sortsChronologically() {
        Elora elora = newElora();
        elora.getResponse("deadline submit report /by 2026-12-01");
        elora.getResponse("deadline pay bills /by 2019-10-15");
        Response response = elora.getResponse("sort");
        assertFalse(response.isError());
        int payBillsIndex = response.getText().indexOf("pay bills");
        int submitReportIndex = response.getText().indexOf("submit report");
        assertTrue(payBillsIndex < submitReportIndex);
    }

    @Test
    void on_validDate_returnsMatchingTasks() {
        Elora elora = newElora();
        elora.getResponse("deadline pay bills /by 2019-10-15");
        Response response = elora.getResponse("on 2019-10-15");
        assertFalse(response.isError());
        assertTrue(response.getText().contains("pay bills"));
    }

    @Test
    void on_invalidDate_returnsError() {
        assertTrue(newElora().getResponse("on 2019-02-30").isError());
    }

    @Test
    void unknownCommand_returnsError() {
        assertTrue(newElora().getResponse("frobnicate").isError());
    }

    @Test
    void bye_isExitCommand_returnsTrue() {
        assertTrue(newElora().isExitCommand("bye"));
    }

    @Test
    void nonByeCommand_isExitCommand_returnsFalse() {
        assertFalse(newElora().isExitCommand("todo read book"));
    }

    @Test
    void getWelcomeMessage_corruptedSaveFile_includesWarning() throws IOException {
        Path filePath = tempDir.resolve("elora.txt");
        Files.writeString(filePath, "not a valid line\n");

        String welcome = new Elora(filePath.toString()).getWelcomeMessage();

        assertTrue(welcome.contains("Heads up"));
        assertTrue(welcome.contains("not a valid line"));
    }

    @Test
    void getWelcomeMessage_cleanSaveFile_hasNoWarning() {
        String welcome = newElora().getWelcomeMessage();
        assertFalse(welcome.contains("Heads up"));
    }

    @Test
    void tasksPersistAcrossEloraInstances_sameFilePath() {
        Path filePath = tempDir.resolve("elora.txt");
        new Elora(filePath.toString()).getResponse("todo read book");

        Elora reloaded = new Elora(filePath.toString());
        assertTrue(reloaded.getResponse("list").getText().contains("read book"));
    }
}
