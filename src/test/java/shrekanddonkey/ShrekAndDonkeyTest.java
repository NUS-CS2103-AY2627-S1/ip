package shrekanddonkey;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests the response generation and welcome message in {@link ShrekAndDonkey}.
 */
public class ShrekAndDonkeyTest {

    @Test
    public void getWelcomeMessage_returnsNonEmptyMessage(@TempDir Path tempDir) {
        ShrekAndDonkey bot = createBot(tempDir);
        String welcome = bot.getWelcomeMessage();
        assertFalse(welcome.isEmpty());
        assertTrue(welcome.contains("ShrekAndDonkey"));
    }

    @Test
    public void getResponse_invalidCommand_returnsErrorMessage(@TempDir Path tempDir) {
        ShrekAndDonkey bot = createBot(tempDir);
        String response = bot.getResponse("invalid command");
        assertTrue(response.contains("What are ye doing in my swamp?!"));
    }

    @Test
    public void getResponse_validTodo_returnsTaskAddedMessage(@TempDir Path tempDir) {
        ShrekAndDonkey bot = createBot(tempDir);
        String response = bot.getResponse("todo read book");
        assertTrue(response.contains("Alright, added to the swamp list:"));
        assertTrue(response.contains("read book"));
    }

    @Test
    public void getResponse_sortCommand_ordersTasksByDate(@TempDir Path tempDir) {
        ShrekAndDonkey bot = createBot(tempDir);
        bot.getResponse("todo read book");
        bot.getResponse("deadline submit report /by 2026-10-15 18:00");

        String response = bot.getResponse("sort");

        assertTrue(response.indexOf("1.[D][ ] submit report") < response.indexOf("2.[T][ ] read book"));
    }

    @Test
    public void getResponse_commandsFlow_executesSuccessfully(@TempDir Path tempDir) {
        ShrekAndDonkey bot = createBot(tempDir);
        bot.getResponse("todo read book");
        bot.getResponse("deadline essay /by 2026-11-20 23:59");
        bot.getResponse("event gala /from 2026-12-01 18:00 /to 2026-12-01 22:00");

        String listResponse = bot.getResponse("list");
        assertTrue(listResponse.contains("1.[T][ ] read book"));
        assertTrue(listResponse.contains("2.[D][ ] essay"));
        assertTrue(listResponse.contains("3.[E][ ] gala"));

        String markResponse = bot.getResponse("mark 1");
        assertTrue(markResponse.contains("Shrek approves! Marked as done:"));
        assertTrue(markResponse.contains("[T][X] read book"));

        String unmarkResponse = bot.getResponse("unmark 1");
        assertTrue(unmarkResponse.contains("Back in the swamp it goes! Unmarked:"));
        assertTrue(unmarkResponse.contains("[T][ ] read book"));

        String findResponse = bot.getResponse("find essay");
        assertTrue(findResponse.contains("Donkey found these in the swamp:"));
        assertTrue(findResponse.contains("essay"));

        String deleteResponse = bot.getResponse("delete 1");
        assertTrue(deleteResponse.contains("Poof! Banished from the swamp:"));

        String exitResponse = bot.getResponse("bye");
        assertTrue(exitResponse.contains("GET OUT OF MY SWAMP!"));
    }

    @Test
    public void isExit_byeCommand_returnsTrue(@TempDir Path tempDir) {
        ShrekAndDonkey bot = createBot(tempDir);
        assertTrue(bot.isExit("bye"));
        assertFalse(bot.isExit("list"));
    }

    @Test
    public void isLastResponseError_invalidCommand_returnsTrue(@TempDir Path tempDir) {
        ShrekAndDonkey bot = createBot(tempDir);
        bot.getResponse("invalidcommand123");
        assertTrue(bot.isLastResponseError());
    }

    @Test
    public void isLastResponseError_validCommand_returnsFalse(@TempDir Path tempDir) {
        ShrekAndDonkey bot = createBot(tempDir);
        bot.getResponse("todo read book");
        assertFalse(bot.isLastResponseError());
    }

    @Test
    public void getResponse_stateChangingCommands_reloadedTaskListContainsChanges(@TempDir Path tempDir) {
        Path filePath = tempDir.resolve("data").resolve("happyFile.txt");
        ShrekAndDonkey bot = new ShrekAndDonkey(filePath.toString());
        bot.getResponse("todo buy onions");
        bot.getResponse("deadline submit report /by 2026-10-15 18:00");
        bot.getResponse("event swamp meeting /from 2026-10-20 14:00 /to 2026-10-20 16:00");
        bot.getResponse("mark 1");
        bot.getResponse("unmark 1");
        bot.getResponse("sort");
        bot.getResponse("delete 2");

        ShrekAndDonkey reloadedBot = new ShrekAndDonkey(filePath.toString());
        String response = reloadedBot.getResponse("list");

        assertTrue(response.contains("1.[D][ ] submit report"));
        assertTrue(response.contains("2.[T][ ] buy onions"));
        assertFalse(response.contains("swamp meeting"));
    }

    @Test
    public void getResponse_invalidStateChangingCommand_doesNotChangeSavedTasks(@TempDir Path tempDir) {
        Path filePath = tempDir.resolve("data").resolve("happyFile.txt");
        ShrekAndDonkey bot = new ShrekAndDonkey(filePath.toString());
        bot.getResponse("todo buy onions");
        bot.getResponse("mark invalid-number");

        String response = new ShrekAndDonkey(filePath.toString()).getResponse("list");

        assertTrue(response.contains("1.[T][ ] buy onions"));
    }

    /**
     * Creates a chatbot that stores data in a test-specific temporary directory.
     *
     * @param tempDir temporary test directory.
     * @return chatbot using an isolated task file.
     */
    private ShrekAndDonkey createBot(Path tempDir) {
        return new ShrekAndDonkey(tempDir.resolve("data").resolve("happyFile.txt").toString());
    }
}
