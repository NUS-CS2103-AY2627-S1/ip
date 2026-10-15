package jeff;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import jeff.storage.Storage;

/**
 * Exercises {@link Jeff#getResponse(String)} and {@link Jeff#getWelcomeMessage()} end
 * to end: command routing, the error path of every command, and how storage failures
 * (a corrupted save file on load, or a write failure on save) get folded into the
 * response text. Each test builds its own {@link Jeff} against a {@link Storage}
 * pointed at {@code tempDir}, so tests never touch the project's real save file.
 */
public class JeffTest {
    @TempDir
    Path tempDir;

    private Jeff newJeff() {
        return new Jeff(new Storage(tempDir.resolve("jeff.txt").toString()));
    }

    @Test
    public void getWelcomeMessage_freshStorage_greetsWithoutWarning() {
        String welcome = newJeff().getWelcomeMessage();

        assertTrue(welcome.contains("Hello! I'm Jeff"));
        assertFalse(welcome.contains("OOPS!!!"));
    }

    @Test
    public void getWelcomeMessage_corruptedSaveFile_appendsLoadWarning() throws IOException {
        Path file = tempDir.resolve("jeff.txt");
        Files.writeString(file, "this line will not parse\n");
        Jeff jeff = new Jeff(new Storage(file.toString()));

        String welcome = jeff.getWelcomeMessage();

        assertTrue(welcome.contains("Hello! I'm Jeff"));
        assertTrue(welcome.contains("OOPS!!!"));
        assertTrue(welcome.contains("corrupted"));
    }

    @Test
    public void getResponse_bye_returnsFarewell() {
        assertTrue(newJeff().getResponse("bye").contains("Bye for now!"));
    }

    @Test
    public void getResponse_unknownCommand_returnsError() {
        assertTrue(newJeff().getResponse("frobnicate").contains("OOPS!!!"));
    }

    @Test
    public void getResponse_listWithNoTasks_showsHeaderOnly() {
        assertTrue(newJeff().getResponse("list").contains("Here's everything on your plate:"));
    }

    @Test
    public void getResponse_todoValidDescription_addsTaskAndListsIt() {
        Jeff jeff = newJeff();

        String response = jeff.getResponse("todo read book");

        assertTrue(response.contains("Got it! I've added this task:"));
        assertTrue(response.contains("[T][ ] read book"));
        assertTrue(jeff.getResponse("list").contains("[T][ ] read book"));
    }

    @Test
    public void getResponse_todoBlankDescription_returnsError() {
        assertTrue(newJeff().getResponse("todo").contains("OOPS!!!"));
    }

    @Test
    public void getResponse_deadlineValidArgs_addsTaskWithByDate() {
        String response = newJeff().getResponse("deadline return book /by 2019-10-15");

        assertTrue(response.contains("[D][ ] return book (by: Oct 15 2019)"));
    }

    @Test
    public void getResponse_deadlineMissingBySegment_returnsError() {
        assertTrue(newJeff().getResponse("deadline return book").contains("OOPS!!!"));
    }

    @Test
    public void getResponse_deadlineMalformedDate_returnsError() {
        assertTrue(newJeff().getResponse("deadline return book /by not-a-date").contains("OOPS!!!"));
    }

    @Test
    public void getResponse_eventValidArgs_addsTaskWithFromAndTo() {
        String response = newJeff().getResponse("event trip /from 2019-11-01 /to 2019-11-05");

        assertTrue(response.contains("[E][ ] trip (from: Nov 01 2019 to: Nov 05 2019)"));
    }

    @Test
    public void getResponse_eventMissingToSegment_returnsError() {
        assertTrue(newJeff().getResponse("event trip /from 2019-11-01").contains("OOPS!!!"));
    }

    @Test
    public void getResponse_eventFromAfterTo_returnsError() {
        String response = newJeff().getResponse("event trip /from 2019-11-05 /to 2019-11-01");

        assertTrue(response.contains("OOPS!!!"));
        assertTrue(response.contains("/from date can't be after its /to date"));
    }

    @Test
    public void getResponse_markValidIndex_marksTaskDone() {
        Jeff jeff = newJeff();
        jeff.getResponse("todo read book");

        String response = jeff.getResponse("mark 1");

        assertTrue(response.contains("[T][X] read book"));
    }

    @Test
    public void getResponse_markNonNumeric_returnsError() {
        Jeff jeff = newJeff();
        jeff.getResponse("todo read book");

        assertTrue(jeff.getResponse("mark abc").contains("OOPS!!!"));
    }

    @Test
    public void getResponse_markOutOfRange_returnsError() {
        assertTrue(newJeff().getResponse("mark 1").contains("OOPS!!!"));
    }

    @Test
    public void getResponse_unmarkValidIndex_unmarksTask() {
        Jeff jeff = newJeff();
        jeff.getResponse("todo read book");
        jeff.getResponse("mark 1");

        String response = jeff.getResponse("unmark 1");

        assertTrue(response.contains("[T][ ] read book"));
    }

    @Test
    public void getResponse_deleteValidIndex_removesTaskFromList() {
        Jeff jeff = newJeff();
        jeff.getResponse("todo read book");

        String response = jeff.getResponse("delete 1");

        assertTrue(response.contains("Done! I've removed this task:"));
        assertTrue(response.contains("You now have 0 tasks in the list."));
        assertFalse(jeff.getResponse("list").contains("read book"));
    }

    @Test
    public void getResponse_findWithKeyword_returnsOnlyMatchingTasks() {
        Jeff jeff = newJeff();
        jeff.getResponse("todo read book");
        jeff.getResponse("todo write essay");

        String response = jeff.getResponse("find book");

        assertTrue(response.contains("read book"));
        assertFalse(response.contains("write essay"));
    }

    @Test
    public void getResponse_findNoKeyword_returnsError() {
        assertTrue(newJeff().getResponse("find").contains("OOPS!!!"));
    }

    @Test
    public void getResponse_sort_ordersTasksChronologically() {
        Jeff jeff = newJeff();
        jeff.getResponse("deadline late task /by 2025-05-01");
        jeff.getResponse("deadline early task /by 2025-01-01");

        String response = jeff.getResponse("sort");
        int earlyIndex = response.indexOf("early task");
        int lateIndex = response.indexOf("late task");

        assertTrue(earlyIndex >= 0 && lateIndex >= 0 && earlyIndex < lateIndex);
    }

    @Test
    public void getResponse_addTask_persistsAcrossJeffInstances() {
        Path file = tempDir.resolve("jeff.txt");
        new Jeff(new Storage(file.toString())).getResponse("todo read book");

        String listing = new Jeff(new Storage(file.toString())).getResponse("list");

        assertTrue(listing.contains("[T][ ] read book"));
    }

    @Test
    public void getResponse_todoWhenSaveFails_appendsSaveWarningToConfirmation() throws IOException {
        // Making the save file path point at an existing directory forces Storage#save's
        // Files.writeString call to fail with a real IOException, without needing a mock.
        Path pathThatIsActuallyADirectory = tempDir.resolve("jeff.txt");
        Files.createDirectory(pathThatIsActuallyADirectory);
        Jeff jeff = new Jeff(new Storage(pathThatIsActuallyADirectory.toString()));

        String response = jeff.getResponse("todo read book");

        assertTrue(response.contains("Got it! I've added this task:"));
        assertTrue(response.contains("OOPS!!!"));
    }
}
