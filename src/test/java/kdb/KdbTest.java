package kdb;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests GUI command responses and their error status. */
class KdbTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void executeCommandResult_allCommands_returnExpectedResponses() {
        Kdb bot = createBot();

        Kdb.CommandResponse listResponse = bot.executeCommandResult("list");
        assertFalse(listResponse.isError());
        assertTrue(listResponse.text().contains("current match sheet"));
        assertFalse(bot.executeCommandResult("todo write report").isError());
        assertFalse(bot.executeCommandResult("high").isError());
        assertFalse(bot.executeCommandResult("deadline submit report /by 2/12/2019 1800")
                .isError());
        assertFalse(bot.executeCommandResult("medium").isError());
        assertFalse(bot.executeCommandResult("event project meeting /from Monday 2pm /to 3pm")
                .isError());
        assertFalse(bot.executeCommandResult("low").isError());
        assertFalse(bot.executeCommandResult("find report").isError());
        assertFalse(bot.executeCommandResult("mark 1").isError());
        assertFalse(bot.executeCommandResult("unmark 1").isError());
        assertFalse(bot.executeCommandResult("delete 1").isError());
        assertFalse(bot.executeCommandResult("bye").isError());
    }

    @Test
    void executeCommandResult_unknownCommand_isMarkedAsError() {
        Kdb.CommandResponse response = createBot().executeCommandResult("remove everything");

        assertTrue(response.isError());
        assertTrue(response.text().contains("That pass went astray"));
    }

    @Test
    void executeCommandResult_missingArguments_areMarkedAsErrors() {
        Kdb bot = createBot();

        assertTrue(bot.executeCommandResult("find").isError());
        assertTrue(bot.executeCommandResult("todo").isError());
        assertTrue(bot.executeCommandResult("deadline").isError());
        assertTrue(bot.executeCommandResult("event").isError());
        assertTrue(bot.executeCommandResult("mark").isError());
        assertTrue(bot.executeCommandResult("unmark").isError());
        assertTrue(bot.executeCommandResult("delete").isError());
        assertTrue(bot.executeCommandResult("   ").isError());
        assertTrue(bot.executeCommandResult(null).isError());
    }

    @Test
    void executeCommandResult_invalidPriority_keepsPriorityPromptActive() {
        Kdb bot = createBot();

        assertFalse(bot.executeCommandResult("todo read book").isError());

        Kdb.CommandResponse invalidPriority = bot.executeCommandResult("urgent");
        Kdb.CommandResponse validPriority = bot.executeCommandResult("high");

        assertTrue(invalidPriority.isError());
        assertTrue(invalidPriority.text().contains("Please try again"));
        assertFalse(validPriority.isError());
        assertTrue(validPriority.text().contains("Priority set to high"));
    }

    @Test
    void executeCommandResult_invalidDateOrEventFormat_isMarkedAsError() {
        Kdb bot = createBot();

        assertTrue(bot.executeCommandResult("deadline submit report /by not-a-date")
                .isError());
        assertTrue(bot.executeCommandResult("event meeting /from Monday 2pm")
                .isError());
    }

    @Test
    void executeCommandResult_invalidTaskNumbers_areMarkedAsErrors() {
        Kdb bot = createBot();
        bot.executeCommandResult("todo read book");
        bot.executeCommandResult("medium");

        assertTrue(bot.executeCommandResult("mark zero").isError());
        assertTrue(bot.executeCommandResult("mark 2").isError());
        assertTrue(bot.executeCommandResult("delete -1").isError());
    }

    @Test
    void executeCommandResult_tasksArePersistedBetweenBotInstances() {
        Path taskFile = temporaryDirectory.resolve("tasks.txt");
        Kdb firstBot = new Kdb(new Storage(taskFile.toString()));

        firstBot.executeCommandResult("todo persistent task");
        firstBot.executeCommandResult("medium");

        Kdb secondBot = new Kdb(new Storage(taskFile.toString()));
        Kdb.CommandResponse response = secondBot.executeCommandResult("list");

        assertFalse(response.isError());
        assertTrue(response.text().contains("persistent task"));
    }

    @Test
    void welcomeMessage_includesIntroductionAndAllCommandPurposes() {
        String welcomeMessage = Ui.welcomeMessage();

        assertTrue(welcomeMessage.contains("Hello, I am KDB"));
        assertTrue(welcomeMessage.contains("todo <description> - Add a task"));
        assertTrue(welcomeMessage.contains("deadline <description> /by <date/time>"));
        assertTrue(welcomeMessage.contains("event <description> /from <start> /to <end>"));
        assertTrue(welcomeMessage.contains("list - Show all tasks."));
        assertTrue(welcomeMessage.contains("find <keyword> - Find tasks"));
        assertTrue(welcomeMessage.contains("mark <task number> - Mark a task as done."));
        assertTrue(welcomeMessage.contains("unmark <task number> - Mark a task as not done."));
        assertTrue(welcomeMessage.contains("delete <task number> - Delete a task."));
        assertTrue(welcomeMessage.contains("bye - Exit KDB."));
    }

    @Test
    void welcomeBanner_containsTheOriginalAsciiArt() {
        assertTrue(Ui.welcomeBanner().contains("mm   mm"));
        assertTrue(Ui.welcomeBanner().contains("##mmmm##"));
    }

    /** Creates a bot backed by an isolated temporary task file. */
    private Kdb createBot() {
        Path taskFile = temporaryDirectory.resolve("tasks-" + System.nanoTime() + ".txt");
        return new Kdb(new Storage(taskFile.toString()));
    }
}
