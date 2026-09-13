package yappy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class YappyTest {
    @TempDir
    private Path tempDirectory;

    @Test
    public void getResponse_addListAndReload_returnsPersistentGuiMessages() {
        Path dataFile = tempDirectory.resolve("data/yappy.txt");
        Yappy yappy = new Yappy(dataFile);

        String welcomeMessage = yappy.getWelcomeMessage();
        String addResponse = yappy.getResponse("todo read book");
        String addCommandType = yappy.getCommandType();
        String listResponse = yappy.getResponse("list");
        String reloadedListResponse = new Yappy(dataFile).getResponse("list");

        assertTrue(welcomeMessage.contains("Hello! I'm Yappy."));
        assertTrue(addResponse.contains("[T][ ] read book"));
        assertEquals("AddCommand", addCommandType);
        assertTrue(listResponse.contains("1.[T][ ] read book"));
        assertEquals(listResponse, reloadedListResponse);
    }

    @Test
    public void getResponse_invalidAndByeCommands_returnsExpectedGuiMessages() {
        Yappy yappy = new Yappy(tempDirectory.resolve("yappy.txt"));

        String invalidResponse = yappy.getResponse("todo");
        String invalidCommandType = yappy.getCommandType();
        boolean exitsAfterInvalidCommand = yappy.shouldExit();
        String invalidByeResponse = yappy.getResponse("bye later");
        boolean exitsAfterInvalidByeCommand = yappy.shouldExit();
        String goodbyeResponse = yappy.getResponse("bye");
        boolean exitsAfterByeCommand = yappy.shouldExit();

        assertEquals("OOPS!!! The description of a todo cannot be empty.", invalidResponse);
        assertEquals("ErrorCommand", invalidCommandType);
        assertFalse(exitsAfterInvalidCommand);
        assertEquals("OOPS!!! The bye command does not take extra arguments.", invalidByeResponse);
        assertFalse(exitsAfterInvalidByeCommand);
        assertEquals("Bye. Hope to see you again soon!", goodbyeResponse);
        assertTrue(exitsAfterByeCommand);
    }

    @Test
    public void getResponse_flexibleSpacingAndCase_addsTask() {
        Yappy yappy = new Yappy(tempDirectory.resolve("yappy.txt"));

        String response = yappy.getResponse("  TODO    Prepare Demo  ");

        assertTrue(response.contains("[T][ ] Prepare Demo"));
    }

    @Test
    public void getResponse_remind_returnsReminderMessageAndDoesNotChangeSavedTasks() {
        Path dataFile = tempDirectory.resolve("yappy.txt");
        Yappy yappy = new Yappy(dataFile);
        yappy.getResponse("deadline submit report /by 2099-01-01");

        String reminderResponse = yappy.getResponse("remind 99999");
        String listResponse = new Yappy(dataFile).getResponse("list");

        assertTrue(reminderResponse.contains("[D][ ] submit report (by: Jan 01 2099)"));
        assertEquals("Here are the tasks in your list:\n1.[D][ ] submit report (by: Jan 01 2099)",
                listResponse);
    }

    @Test
    public void getResponse_remindWithNoUpcomingDeadlines_returnsClearMessage() {
        Yappy yappy = new Yappy(tempDirectory.resolve("yappy.txt"));

        String reminderResponse = yappy.getResponse("remind 7");

        assertEquals("Here are your incomplete deadlines due within 7 day(s):\n  None. You're all clear!",
                reminderResponse);
        assertEquals("ReminderCommand", yappy.getCommandType());
    }
}
