package leo;

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
 * End-to-end tests for {@link Leo}, driving it the same way the GUI
 * does - through {@link Leo#getResponse(String)} - rather than testing
 * any command handler in isolation, since they are all private and
 * only reachable through command dispatch. Each test starts from a
 * fresh {@code Leo}, backed by an empty save file, so tests cannot see
 * each other's tasks or a developer's real saved tasks; the original
 * save file (if any) is restored afterwards, same as {@link StorageTest}.
 */
public class LeoTest {
    private static final Path FILE_PATH = Paths.get("data", "leo.txt");

    private byte[] originalContent;
    private boolean originalFileExisted;

    @BeforeEach
    public void backUpAndClearSaveFile() throws IOException {
        originalFileExisted = Files.exists(FILE_PATH);
        if (originalFileExisted) {
            originalContent = Files.readAllBytes(FILE_PATH);
        }

        Path parent = FILE_PATH.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        Files.writeString(FILE_PATH, "");
    }

    @AfterEach
    public void restoreSaveFile() throws IOException {
        if (originalFileExisted) {
            Files.write(FILE_PATH, originalContent);
        } else if (Files.exists(FILE_PATH)) {
            Files.delete(FILE_PATH);
        }
    }

    @Test
    public void getResponse_byeCommand_showsGoodbye() {
        Leo leo = new Leo();

        String response = leo.getResponse("bye");

        assertTrue(response.contains("Hope to see you again soon"));
    }

    @Test
    public void getResponse_unknownCommand_showsHelpfulError() {
        Leo leo = new Leo();

        String response = leo.getResponse("frobnicate");

        assertTrue(response.contains("I don't understand"));
    }

    @Test
    public void getResponse_todoWithEmptyDescription_showsError() {
        Leo leo = new Leo();

        String response = leo.getResponse("todo");

        assertTrue(response.contains("description of a todo cannot be empty"));
    }

    @Test
    public void getResponse_todoThenList_showsAddedTask() {
        Leo leo = new Leo();

        leo.getResponse("todo read book");
        String response = leo.getResponse("list");

        assertTrue(response.contains("1. [T][ ] read book"));
    }

    @Test
    public void getResponse_deadlineMissingBy_showsFormatError() {
        Leo leo = new Leo();

        String response = leo.getResponse("deadline return book");

        assertTrue(response.contains("deadline DESCRIPTION /by TIME"));
    }

    @Test
    public void getResponse_deadlineWithDuplicateBy_showsDuplicateDelimiterError() {
        // A-MoreErrorHandling
        Leo leo = new Leo();

        String response = leo.getResponse(
                "deadline return book /by 2019-12-01 1800 /by 2019-12-02 1800");

        assertTrue(response.contains("'/by' only once"));
    }

    @Test
    public void getResponse_eventWithDuplicateFrom_showsDuplicateDelimiterError() {
        // A-MoreErrorHandling
        Leo leo = new Leo();

        String response = leo.getResponse(
                "event meet /from 2019-12-01 1000 /from 2019-12-02 1000 /to 2019-12-02 1100");

        assertTrue(response.contains("'/from' only once"));
    }

    @Test
    public void getResponse_eventWithDuplicateTo_showsDuplicateDelimiterError() {
        // A-MoreErrorHandling
        Leo leo = new Leo();

        String response = leo.getResponse(
                "event meet /from 2019-12-01 1000 /to 2019-12-01 1100 /to 2019-12-01 1200");

        assertTrue(response.contains("'/to' only once"));
    }

    @Test
    public void getResponse_invalidDateFormat_showsDateFormatError() {
        Leo leo = new Leo();

        String response = leo.getResponse("deadline return book /by not-a-date");

        assertTrue(response.contains("yyyy-MM-dd HHmm"));
    }

    @Test
    public void getResponse_duplicateTodo_addsItButWarns() {
        // A-MoreErrorHandling
        Leo leo = new Leo();

        leo.getResponse("todo read book");
        String response = leo.getResponse("todo read book");

        assertTrue(response.contains("Got it. I've added this task"));
        assertTrue(response.contains("already have"));

        String listResponse = leo.getResponse("list");
        assertTrue(listResponse.contains("1. [T][ ] read book"));
        assertTrue(listResponse.contains("2. [T][ ] read book"));
    }

    @Test
    public void getResponse_distinctTodos_noDuplicateWarning() {
        Leo leo = new Leo();

        leo.getResponse("todo read book");
        String response = leo.getResponse("todo return book");

        assertFalse(response.contains("already have"));
    }

    @Test
    public void getResponse_markThenUnmark_flipsStatusInList() {
        Leo leo = new Leo();
        leo.getResponse("todo read book");

        leo.getResponse("mark 1");
        assertTrue(leo.getResponse("list").contains("[T][X] read book"));

        leo.getResponse("unmark 1");
        assertTrue(leo.getResponse("list").contains("[T][ ] read book"));
    }

    @Test
    public void getResponse_markInvalidNumber_showsError() {
        Leo leo = new Leo();
        leo.getResponse("todo read book");

        String response = leo.getResponse("mark 5");

        assertTrue(response.contains("does not exist"));
    }

    @Test
    public void getResponse_markNonNumericArgument_showsError() {
        Leo leo = new Leo();
        leo.getResponse("todo read book");

        String response = leo.getResponse("mark abc");

        assertTrue(response.contains("valid task number"));
    }

    @Test
    public void getResponse_deleteTask_removesItFromList() {
        Leo leo = new Leo();
        leo.getResponse("todo a");
        leo.getResponse("todo b");

        String deleteResponse = leo.getResponse("delete 1");
        String listResponse = leo.getResponse("list");

        assertTrue(deleteResponse.contains("I've removed this task"));
        assertFalse(listResponse.contains("[T][ ] a"));
        assertTrue(listResponse.contains("1. [T][ ] b"));
    }

    @Test
    public void getResponse_findMatchingKeyword_returnsOnlyMatches() {
        Leo leo = new Leo();
        leo.getResponse("todo read book");
        leo.getResponse("todo return laptop");

        String response = leo.getResponse("find book");

        assertTrue(response.contains("read book"));
        assertFalse(response.contains("return laptop"));
    }

    @Test
    public void getResponse_findNoMatches_showsNoMatchesMessage() {
        Leo leo = new Leo();
        leo.getResponse("todo read book");

        String response = leo.getResponse("find xyz");

        assertTrue(response.contains("couldn't find any matching tasks"));
    }

    @Test
    public void getResponse_tasksPersistAcrossNewLeoInstances() {
        Leo firstLeo = new Leo();
        firstLeo.getResponse("todo read book");

        Leo secondLeo = new Leo();
        String response = secondLeo.getResponse("list");

        assertTrue(response.contains("1. [T][ ] read book"));
    }
}
