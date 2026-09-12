package minweeder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Integration tests that exercise {@link Minweeder#getResponse(String)} end-to-end,
 * including the interaction with {@link minweeder.storage.Storage}. The save file is
 * backed up before each test and restored afterwards, since Minweeder always persists
 * to the same fixed path.
 */
public class MinweederTest {
    private static final Path FILE_PATH = Paths.get("data", "minweeder.txt");
    private byte[] backup;
    private boolean isFileExisting;

    @BeforeEach
    public void backUpAndClearExistingSaveFile() throws IOException {
        isFileExisting = Files.exists(FILE_PATH);
        if (isFileExisting) {
            backup = Files.readAllBytes(FILE_PATH);
        }
        Files.deleteIfExists(FILE_PATH);
    }

    @AfterEach
    public void restoreExistingSaveFile() throws IOException {
        if (isFileExisting) {
            Files.write(FILE_PATH, backup);
        } else {
            Files.deleteIfExists(FILE_PATH);
        }
    }

    @Test
    public void getResponse_emptyCommand_returnsEmptyString() {
        Minweeder minweeder = new Minweeder();

        assertEquals("", minweeder.getResponse(""));
    }

    @Test
    public void getResponse_unknownCommand_returnsErrorAndSetsIsError() {
        Minweeder minweeder = new Minweeder();

        String response = minweeder.getResponse("frobnicate");

        assertTrue(response.startsWith("Erm...you can't do that..."));
        assertTrue(minweeder.isError());
        assertFalse(minweeder.isExit());
    }

    @Test
    public void getResponse_todo_addsTaskAndConfirms() {
        Minweeder minweeder = new Minweeder();

        String response = minweeder.getResponse("todo read book");

        assertTrue(response.contains("[T][ ] read book"));
        assertFalse(minweeder.isError());
    }

    @Test
    public void getResponse_todoMissingDescription_returnsError() {
        Minweeder minweeder = new Minweeder();

        String response = minweeder.getResponse("todo");

        assertTrue(minweeder.isError());
        assertTrue(response.contains("needs a description"));
    }

    @Test
    public void getResponse_duplicateTodo_returnsError() {
        Minweeder minweeder = new Minweeder();
        minweeder.getResponse("todo read book");

        String response = minweeder.getResponse("todo read book");

        assertTrue(minweeder.isError());
        assertTrue(response.contains("already have that exact"));
    }

    @Test
    public void getResponse_deadline_addsTaskWithParsedDate() {
        Minweeder minweeder = new Minweeder();

        String response = minweeder.getResponse("deadline return book /by 2/12/2019 1800");

        // The am/pm marker's case is locale/JDK-dependent (see DeadlineTest), so compare
        // case-insensitively rather than pinning one case.
        assertTrue(response.toLowerCase().contains("(by: dec 02 2019, 6:00pm)"));
        assertFalse(minweeder.isError());
    }

    @Test
    public void getResponse_deadlineInvalidDate_returnsError() {
        Minweeder minweeder = new Minweeder();

        String response = minweeder.getResponse("deadline return book /by not-a-date");

        assertTrue(minweeder.isError());
        assertTrue(response.contains("d/M/yyyy HHmm"));
    }

    @Test
    public void getResponse_event_addsTaskWithFromAndTo() {
        Minweeder minweeder = new Minweeder();

        String response = minweeder.getResponse("event project meeting /from Mon 2pm /to 4pm");

        assertTrue(response.contains("(from: Mon 2pm to: 4pm)"));
        assertFalse(minweeder.isError());
    }

    @Test
    public void getResponse_loanTo_addsLentLoan() {
        Minweeder minweeder = new Minweeder();

        String response = minweeder.getResponse("loan 50 /to Alice");

        assertTrue(response.contains("Lent $50.00 to Alice"));
        assertFalse(minweeder.isError());
    }

    @Test
    public void getResponse_loanFrom_addsBorrowedLoan() {
        Minweeder minweeder = new Minweeder();

        String response = minweeder.getResponse("loan 50 /from Bob");

        assertTrue(response.contains("Borrowed $50.00 from Bob"));
        assertFalse(minweeder.isError());
    }

    @Test
    public void getResponse_list_showsAddedTasks() {
        Minweeder minweeder = new Minweeder();
        minweeder.getResponse("todo read book");

        String response = minweeder.getResponse("list");

        assertTrue(response.contains("1. [T][ ] read book"));
    }

    @Test
    public void getResponse_listWithExtraArguments_returnsError() {
        Minweeder minweeder = new Minweeder();

        String response = minweeder.getResponse("list now");

        assertTrue(minweeder.isError());
        assertTrue(response.contains("doesn't take any extra input"));
    }

    @Test
    public void getResponse_mark_marksTaskAsDone() {
        Minweeder minweeder = new Minweeder();
        minweeder.getResponse("todo read book");

        String response = minweeder.getResponse("mark 1");

        assertTrue(response.contains("[T][X] read book"));
        assertFalse(minweeder.isError());
    }

    @Test
    public void getResponse_unmark_marksTaskAsNotDone() {
        Minweeder minweeder = new Minweeder();
        minweeder.getResponse("todo read book");
        minweeder.getResponse("mark 1");

        String response = minweeder.getResponse("unmark 1");

        assertTrue(response.contains("[T][ ] read book"));
        assertFalse(minweeder.isError());
    }

    @Test
    public void getResponse_markOutOfRangeIndex_returnsError() {
        Minweeder minweeder = new Minweeder();
        minweeder.getResponse("todo read book");

        String response = minweeder.getResponse("mark 5");

        assertTrue(minweeder.isError());
    }

    @Test
    public void getResponse_delete_removesTask() {
        Minweeder minweeder = new Minweeder();
        minweeder.getResponse("todo read book");

        String response = minweeder.getResponse("delete 1");

        assertTrue(response.contains("[T][ ] read book"));
        assertTrue(response.contains("Now you have 0 tasks"));
        assertFalse(minweeder.isError());
    }

    @Test
    public void getResponse_on_showsOnlyTasksOccurringOnDate() {
        Minweeder minweeder = new Minweeder();
        minweeder.getResponse("todo read book");
        minweeder.getResponse("deadline return book /by 2/12/2019 1800");

        String response = minweeder.getResponse("on 2/12/2019");

        assertTrue(response.contains("return book"));
        assertFalse(response.contains("read book"));
    }

    @Test
    public void getResponse_find_showsOnlyMatchingTasks() {
        Minweeder minweeder = new Minweeder();
        minweeder.getResponse("todo read book");
        minweeder.getResponse("todo join club");

        String response = minweeder.getResponse("find book");

        assertTrue(response.contains("read book"));
        assertFalse(response.contains("join club"));
    }

    @Test
    public void getResponse_bye_setsIsExitAndReturnsGoodbye() {
        Minweeder minweeder = new Minweeder();

        String response = minweeder.getResponse("bye");

        assertEquals("Goodbye! Hope you had a productive session :)", response);
        assertTrue(minweeder.isExit());
    }

    @Test
    public void getResponse_byeWithExtraArguments_returnsError() {
        Minweeder minweeder = new Minweeder();

        String response = minweeder.getResponse("bye now");

        assertTrue(minweeder.isError());
        assertFalse(minweeder.isExit());
    }

    @Test
    public void getResponse_errorAfterPriorSuccess_resetsIsErrorEachCall() {
        Minweeder minweeder = new Minweeder();
        minweeder.getResponse("frobnicate");
        assertTrue(minweeder.isError());

        minweeder.getResponse("todo read book");

        assertFalse(minweeder.isError());
    }

    @Test
    public void getWelcomeMessage_freshStart_containsGreetingOnly() {
        Minweeder minweeder = new Minweeder();

        String welcome = minweeder.getWelcomeMessage();

        assertEquals("Heyyo I'm Minweeder!\nLETS GET THINGS DONE RAHH", welcome);
    }

    @Test
    public void getWelcomeMessage_afterUnreadableSaveLines_mentionsSkippedLines() throws IOException {
        Files.createDirectories(FILE_PATH.getParent());
        Files.write(FILE_PATH, "X | 0 | mystery task\n".getBytes());

        Minweeder minweeder = new Minweeder();
        String welcome = minweeder.getWelcomeMessage();

        assertTrue(welcome.contains("line(s) of your save file were unreadable"));
    }
}
