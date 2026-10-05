package friday;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests the response method used by the GUI.
 */
class FridayTest {
    private static final DateTimeFormatter FILE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");

    @TempDir
    private Path tempDir;

    @Test
    void getResponse_addTaskThenList_returnsExpectedMessages() {
        Friday friday = new Friday(tempDir.resolve("duke.txt").toString());

        String addResponse = friday.getResponse("todo read book");
        String listResponse = friday.getResponse("list");

        assertTrue(addResponse.contains("Certainly, sir. I have added this task:"));
        assertTrue(addResponse.contains("[T][ ] read book"));
        assertTrue(listResponse.contains("1. [T][ ] read book"));
    }

    @Test
    void getResponse_bye_returnsGoodbyeMessage() {
        Friday friday = new Friday(tempDir.resolve("duke.txt").toString());

        String response = friday.getResponse("bye");

        assertTrue(response.contains("Powering down. I remain at your service, sir."));
    }

    @Test
    void getResponse_sortCommand_returnsTasksSortedByDate() {
        Friday friday = new Friday(tempDir.resolve("duke.txt").toString());

        friday.getResponse("deadline later task /by 5/12/2025 1800");
        friday.getResponse("todo floating task");
        friday.getResponse("deadline earlier task /by 2/12/2025 1800");
        String response = friday.getResponse("sort");

        assertTrue(response.indexOf("earlier task") < response.indexOf("later task"));
        assertTrue(response.indexOf("later task") < response.indexOf("floating task"));
    }

    @Test
    void getResponse_repeatCommand_marksTaskAsRecurring() {
        Friday friday = new Friday(tempDir.resolve("duke.txt").toString());

        friday.getResponse("todo submit report");
        String response = friday.getResponse("repeat 1 weekly");

        assertTrue(response.contains("This task will now repeat weekly"));
        assertTrue(response.contains("[T][ ] submit report (repeats: weekly)"));
    }

    @Test
    void getResponse_remindersCommand_returnsDatedTasksSortedByDate() {
        Friday friday = new Friday(tempDir.resolve("duke.txt").toString());

        friday.getResponse("deadline later task /by 5/12/2025 1800");
        friday.getResponse("todo floating task");
        friday.getResponse("deadline earlier task /by 2/12/2025 1800");
        String response = friday.getResponse("reminders");

        assertTrue(response.contains("Your reminders, arranged chronologically"));
        assertTrue(response.indexOf("earlier task") < response.indexOf("later task"));
        assertTrue(!response.contains("floating task"));
    }

    @Test
    void getWelcome_taskDueWithinTwoDays_returnsUpcomingReminder() {
        Friday friday = new Friday(tempDir.resolve("duke.txt").toString());
        String reminderDateTime = LocalDateTime.now().plusDays(1).format(FILE_FORMAT);

        friday.getResponse("deadline submit report /by " + reminderDateTime);
        String welcomeMessage = friday.getWelcome();

        assertTrue(welcomeMessage.contains("Items requiring attention within 2 days"));
        assertTrue(welcomeMessage.contains("submit report"));
    }

    @Test
    void getResponse_blankInput_returnsErrorAndMarksResponseAsError() {
        Friday friday = new Friday(tempDir.resolve("duke.txt").toString());

        String response = friday.getResponse("   ");

        assertTrue(response.contains("please enter a command"));
        assertTrue(friday.wasLastResponseError());
    }

    @Test
    void getResponse_extraDetailsForList_returnsErrorWithoutChangingTasks() {
        Friday friday = new Friday(tempDir.resolve("duke.txt").toString());
        friday.getResponse("todo read book");

        String response = friday.getResponse("list please");

        assertTrue(response.contains("does not accept extra details"));
        assertTrue(friday.wasLastResponseError());
    }

    @Test
    void getResponse_taskLifecycleCommands_updateTheSelectedTask() {
        Friday friday = new Friday(tempDir.resolve("duke.txt").toString());
        friday.getResponse("todo read book");

        assertTrue(friday.getResponse("mark 1").contains("[T][X] read book"));
        assertTrue(friday.getResponse("unmark 1").contains("[T][ ] read book"));
        assertTrue(friday.getResponse("find book").contains("read book"));
        assertTrue(friday.getResponse("delete 1").contains("0 tasks"));
    }

    @Test
    void getResponse_helpAndCaseInsensitiveCommands_returnSuccessfulResponses() {
        Friday friday = new Friday(tempDir.resolve("duke.txt").toString());

        assertTrue(friday.getResponse("HELP").contains("Available directives"));
        assertTrue(friday.getResponse("TODO read book").contains("read book"));
        assertFalse(friday.wasLastResponseError());
    }

    @Test
    void getResponse_invalidCommandAndTaskNumber_returnErrors() {
        Friday friday = new Friday(tempDir.resolve("duke.txt").toString());

        assertTrue(friday.getResponse("dance").contains("no clue"));
        assertTrue(friday.wasLastResponseError());
        assertTrue(friday.getResponse("mark 9").contains("not in the list"));
        assertTrue(friday.getResponse("delete one").contains("valid task number"));
    }
}
