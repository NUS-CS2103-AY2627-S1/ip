package lion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests responses produced by {@link Lion#getResponse(String)}.
 *
 * <p>Storage is redirected to a fresh temporary directory for every test (see
 * {@link #setUp()}), so these tests can freely exercise add/mark/delete/save without
 * ever touching the application's real save file.
 */
public class LionTest {

    @TempDir
    Path tempDir;

    private Lion lion;

    @BeforeEach
    void setUp() {
        Storage.setDirectoryPathForTesting(tempDir.toString());
        lion = new Lion();
    }

    @AfterEach
    void tearDown() {
        Storage.setDirectoryPathForTesting(null);
    }

    @Test
    void getResponse_help_listsAllCommandsWithDescriptions() {
        String response = lion.getResponse("help");

        assertTrue(response.startsWith("Here are the available commands:"));
        assertTrue(response.contains("todo - " + CommandType.TODO.getDescription()));
        assertTrue(response.contains("help - " + CommandType.HELP.getDescription()));
        assertTrue(response.contains("bye - " + CommandType.BYE.getDescription()));
        assertFalse(response.contains("unknown"));
    }

    @Test
    void getResponse_unknownCommand_mentionsHelp() {
        String response = lion.getResponse("gibberish");

        assertTrue(response.startsWith("ROAR!!!"));
        assertTrue(response.contains("help"));
    }

    @Test
    void getResponse_bye_saysGoodbye() {
        assertEquals("Roar! Until next time.", lion.getResponse("bye"));
    }

    @Test
    void isByeResponse_byeReply_returnsTrue() {
        assertTrue(Lion.isByeResponse(lion.getResponse("bye")));
    }

    @Test
    void isTaskListResponse_ordinaryReply_returnsFalse() {
        assertFalse(Lion.isTaskListResponse(lion.getResponse("bye")));
    }

    @Test
    void constructor_saveFilePathIsActuallyADirectory_fallsBackToEmptyTaskList() throws IOException {
        // An environment problem while loading (e.g. a directory sitting where the save
        // file is expected) must not prevent the app from starting; it should fall back
        // to an empty task list instead of propagating the IOException.
        Path brokenDir = tempDir.resolve("broken");
        Files.createDirectories(brokenDir);
        new File(brokenDir.toFile(), "lion.txt").mkdirs();
        Storage.setDirectoryPathForTesting(brokenDir.toString());

        Lion brokenLion = new Lion();

        assertTrue(Lion.isTaskListResponse(brokenLion.getResponse("list")));
        assertFalse(brokenLion.getResponse("list").contains("1."));
    }

    @Test
    void getResponse_todoWhenSaveFails_stillConfirmsButNotesTheFailure() throws IOException {
        // A regular file where the data directory should be blocks it from ever being
        // created, so the write fails; the task should still be reported as added
        // (it is, in memory), with the save failure appended as a secondary notice.
        File fileBlockingDirectory = new File(tempDir.toFile(), "not-a-directory");
        fileBlockingDirectory.createNewFile();
        Storage.setDirectoryPathForTesting(fileBlockingDirectory.getPath());

        String response = lion.getResponse("todo read book");

        assertFalse(Lion.isErrorResponse(response));
        assertTrue(response.contains("[T] [ ] read book"));
        assertTrue(response.contains("Lion couldn't stash your tasks safely"));
    }

    @Test
    void getResponse_markOutOfRange_isRejectedAsError() {
        String response = lion.getResponse("mark 999999");

        assertTrue(Lion.isErrorResponse(response));
    }

    @Test
    void getResponse_markZeroOrNegative_isRejectedAsError() {
        assertTrue(Lion.isErrorResponse(lion.getResponse("mark 0")));
        assertTrue(Lion.isErrorResponse(lion.getResponse("mark -1")));
    }

    @Test
    void getResponse_markNonNumeric_isRejectedAsError() {
        String response = lion.getResponse("mark abc");

        assertTrue(Lion.isErrorResponse(response));
    }

    @Test
    void getResponse_markMissingNumber_isRejectedAsError() {
        String response = lion.getResponse("mark");

        assertTrue(Lion.isErrorResponse(response));
    }

    @Test
    void getResponse_commandContainingPipeCharacter_isRejectedAsError() {
        String response = lion.getResponse("todo buy milk | bread");

        assertTrue(Lion.isErrorResponse(response));
    }

    @Test
    void getResponse_deadlineWithNonExistentDate_isRejectedAsError() {
        // 30 February does not exist.
        String response = lion.getResponse("deadline return book /by 30/2/2026 1800");

        assertTrue(Lion.isErrorResponse(response));
    }

    @Test
    void getResponse_eventStartAtOrAfterEnd_isRejectedAsError() {
        String sameTime = lion.getResponse("event clash /from 2/12/2026 1800 /to 2/12/2026 1800");
        String startAfterEnd = lion.getResponse("event clash /from 2/12/2026 1900 /to 2/12/2026 1800");

        assertTrue(Lion.isErrorResponse(sameTime));
        assertTrue(Lion.isErrorResponse(startAfterEnd));
    }

    @Test
    void getResponse_todo_addsTaskAndConfirms() {
        String response = lion.getResponse("todo read book");

        assertFalse(Lion.isErrorResponse(response));
        assertTrue(response.contains("[T] [ ] read book"));
        assertTrue(response.contains("1 tasks"));
    }

    @Test
    void getResponse_todoEmptyDescription_isRejectedAsError() {
        assertTrue(Lion.isErrorResponse(lion.getResponse("todo")));
    }

    @Test
    void getResponse_deadline_addsTaskAndConfirms() {
        String response = lion.getResponse("deadline return book /by 2/12/2026 1800");

        assertFalse(Lion.isErrorResponse(response));
        assertTrue(response.contains("[D] [ ] return book (by: Dec 02 2026 6:00 PM)"));
    }

    @Test
    void getResponse_event_addsTaskAndConfirms() {
        String response = lion.getResponse("event meeting /from Monday 2pm /to Monday 4pm");

        assertFalse(Lion.isErrorResponse(response));
        assertTrue(response.contains("[E] [ ] meeting (from: Monday 2pm to: Monday 4pm)"));
    }

    @Test
    void getResponse_duplicateTodo_warnsButStillAdds() {
        lion.getResponse("todo read book");

        String response = lion.getResponse("todo read book");

        assertFalse(Lion.isErrorResponse(response));
        assertTrue(response.contains("Heads up"));
        long occurrences = lion.getResponse("list").lines()
                .filter(line -> line.contains("read book"))
                .count();
        assertEquals(2, occurrences);
    }

    @Test
    void getResponse_list_showsAddedTasksAsTaskListResponse() {
        lion.getResponse("todo read book");
        lion.getResponse("todo write notes");

        String response = lion.getResponse("list");

        assertTrue(Lion.isTaskListResponse(response));
        assertTrue(response.contains("1. [T] [ ] read book"));
        assertTrue(response.contains("2. [T] [ ] write notes"));
    }

    @Test
    void getResponse_listWithNoTasks_showsEmptyTaskListResponse() {
        String response = lion.getResponse("list");

        assertTrue(Lion.isTaskListResponse(response));
        assertFalse(response.contains("1."));
    }

    @Test
    void getResponse_mark_marksTaskDone() {
        lion.getResponse("todo read book");

        String response = lion.getResponse("mark 1");

        assertFalse(Lion.isErrorResponse(response));
        assertTrue(response.contains("[X] read book"));
    }

    @Test
    void getResponse_unmark_marksTaskNotDone() {
        lion.getResponse("todo read book");
        lion.getResponse("mark 1");

        String response = lion.getResponse("unmark 1");

        assertFalse(Lion.isErrorResponse(response));
        assertTrue(response.contains("[ ] read book"));
    }

    @Test
    void getResponse_delete_removesTaskAndUpdatesRemainingList() {
        lion.getResponse("todo read book");
        lion.getResponse("todo write notes");

        String response = lion.getResponse("delete 1");

        assertFalse(Lion.isErrorResponse(response));
        assertTrue(response.contains("[T] [ ] read book"));
        assertTrue(response.contains("1 tasks"));

        String remaining = lion.getResponse("list");
        assertTrue(remaining.contains("write notes"));
        assertFalse(remaining.contains("read book"));
    }

    @Test
    void getResponse_find_returnsOnlyMatchingTasksAsTaskListResponse() {
        lion.getResponse("todo read book");
        lion.getResponse("todo write notes");

        String response = lion.getResponse("find book");

        assertTrue(Lion.isTaskListResponse(response));
        assertTrue(response.contains("read book"));
        assertFalse(response.contains("write notes"));
    }

    @Test
    void getResponse_findEmptyKeyword_isRejectedAsError() {
        assertTrue(Lion.isErrorResponse(lion.getResponse("find")));
    }

    @Test
    void getResponse_todo_persistsAcrossNewLionInstance() {
        lion.getResponse("todo read book");

        Lion reloaded = new Lion();

        assertTrue(Lion.isTaskListResponse(reloaded.getResponse("list")));
        assertTrue(reloaded.getResponse("list").contains("read book"));
    }
}
