package bot67;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import bot67.storage.Storage;
import bot67.ui.Ui;

/** Exercises Bot67's user-facing command flows through its public response API. */
class Bot67Test {
    @TempDir
    private Path directory;

    @Test
    void taskCommands_updateListStatusAndSavedData() {
        Path file = directory.resolve("tasks.txt");
        Bot67 bot = new Bot67(new Storage(file));

        assertTrue(bot.getResponse("list").contains("No tasks in the list yet"));
        assertTrue(bot.getResponse("todo read book").contains("You have 1 task in the list"));
        assertTrue(bot.getResponse("deadline submit report /by 2026-10-15")
                .contains("[D][ ] submit report (by: Oct 15 2026)"));
        assertTrue(bot.getResponse("event consultation /from Mon 2pm /to 4pm")
                .contains("You have 3 tasks in the list"));

        String list = bot.getResponse("list");
        assertTrue(list.contains("1.[T][ ] read book"));
        assertTrue(list.contains("2.[D][ ] submit report (by: Oct 15 2026)"));
        assertTrue(list.contains("3.[E][ ] consultation (from: Mon 2pm to: 4pm)"));

        assertTrue(bot.getResponse("mark 2").contains("[X] submit report"));
        assertTrue(bot.getResponse("unmark 2").contains("[ ] submit report"));
        assertTrue(bot.getResponse("delete 1").contains("Now you have 2 tasks in the list"));

        Bot67 reloaded = new Bot67(new Storage(file));
        assertEquals(bot.getResponse("list"), reloaded.getResponse("list"));
        assertFalse(reloaded.isLastResponseError());
    }

    @Test
    void findAndSort_reportMatchingEmptyAndOrderedResults() {
        Bot67 bot = createBot();
        bot.getResponse("todo Zebra task");
        bot.getResponse("todo apple task");
        bot.getResponse("deadline apple report /by Sunday");

        String matches = bot.getResponse("find apple");
        assertTrue(matches.contains("2.[T][ ] apple task"));
        assertTrue(matches.contains("3.[D][ ] apple report (by: Sunday)"));
        assertTrue(bot.getResponse("find missing").contains("no matching tasks"));

        String sorted = bot.getResponse("sort");
        List<String> expectedOrder = List.of("1.[D][ ] apple report", "2.[T][ ] apple task", "3.[T][ ] Zebra task");
        int previousPosition = -1;
        for (String expected : expectedOrder) {
            int position = sorted.indexOf(expected);
            assertTrue(position > previousPosition, expected);
            previousPosition = position;
        }
    }

    @Test
    void malformedCommands_returnGuidanceAndNextValidCommandStillWorks() {
        Bot67 bot = createBot();
        assertErrorContains(bot, "find", "Use: find <keyword>.");
        assertErrorContains(bot, "mark", "Use: mark <task number>.");
        assertErrorContains(bot, "unmark", "Use: unmark <task number>.");
        assertErrorContains(bot, "delete", "Use: delete <task number>.");
        assertErrorContains(bot, "todo", "A todo description cannot be empty.");
        assertErrorContains(bot, "deadline", "Use: deadline <description> /by <date or time>.");
        assertErrorContains(bot, "event", "Use: event <description> /from <start> /to <end>.");
        assertErrorContains(bot, "list now", "Use: list (no arguments).");
        assertErrorContains(bot, "sort now", "Use: sort (no arguments).");
        assertErrorContains(bot, "bye now", "Use: bye (no arguments).");
        assertErrorContains(bot, "dance", "I do not recognize that command.");
        assertErrorContains(bot, "delete 1", "Task number is out of range.");

        assertTrue(bot.getResponse("todo recovered").contains("I've added this task"));
        assertFalse(bot.isLastResponseError());
    }

    @Test
    void exitAndPresentationValues_areExposedForBothInterfaces() {
        Bot67 bot = createBot();

        assertTrue(Bot67.getBanner().contains("___"));
        assertFalse(Bot67.getPersonalityArt().isBlank());
        assertEquals(Ui.GOODBYE, bot.getResponse("bye"));
        assertTrue(bot.isExitRequested());
        assertFalse(bot.isLastResponseError());
    }

    /** Creates a bot with an isolated save file for one test. */
    private Bot67 createBot() {
        return new Bot67(new Storage(directory.resolve("tasks.txt")));
    }

    /** Checks both the user-facing error text and the separate GUI error flag. */
    private static void assertErrorContains(Bot67 bot, String command, String expected) {
        assertTrue(bot.getResponse(command).contains(expected), command);
        assertTrue(bot.isLastResponseError(), command);
    }
}
