package slotbot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class SlotBotTest {
    @TempDir
    private Path directory;

    @Test
    public void getResponseDetails_validAndInvalidCommands_reportsErrorStatus() {
        SlotBot bot = new SlotBot(directory.resolve("tasks.txt"));

        assertFalse(bot.getResponseDetails("todo read book").isError());
        assertTrue(bot.getResponseDetails("todo").isError());
    }

    @Test
    public void getResponse_commandsAcrossRequests_preservesStateAndSaves() {
        Path save = directory.resolve("tasks.txt");
        SlotBot bot = new SlotBot(save);

        assertTrue(bot.getResponse("todo read book").contains("Now you have 1 tasks"));
        assertTrue(bot.getResponse("deadline return book /by 2026-09-10").contains("Sep 10 2026"));
        assertTrue(bot.getResponse("event study /from 2026-09-10 14:00 /to 2026-09-10 15:00")
                .contains("[E][ ] study"));

        assertTrue(bot.getResponse("mark 1").contains("[T][X] read book"));
        assertTrue(new SlotBot(save).getResponse("list").contains("[T][X] read book"));
        assertTrue(bot.getResponse("unmark 1").contains("[T][ ] read book"));

        assertFalse(bot.getResponse("find book").contains("[E]"));
        assertTrue(bot.getResponse("find missing").contains("No tasks match \"missing\"."));

        bot.getResponse("delete 3");
        assertFalse(new SlotBot(save).getResponse("list").contains("[E]"));
    }

    @Test
    public void getResponse_findMatchingTask_preservesListNumber() {
        SlotBot bot = new SlotBot(directory.resolve("tasks.txt"));
        bot.getResponse("todo read book");
        bot.getResponse("todo project meeting");

        String response = bot.getResponse("find meeting");

        assertTrue(response.contains("2. [T][ ] project meeting"));
        assertFalse(response.contains("1. [T][ ] project meeting"));
    }

    @Test
    public void getResponse_invalidCommands_doesNotChangeTasksOrRepeatReplies() {
        SlotBot bot = new SlotBot(directory.resolve("tasks.txt"));
        bot.getResponse("todo read book");
        String before = bot.getResponse("list");
        String[] commands = {"", "todo", "blah", "mark 0", "mark 2", "delete abc",
            "deadline bad /by 2026-02-30", "deadline repeated /by 2026-09-18 /by 2026-09-19",
            "todo unsafe | description",
            "event same /from 2026-09-18 10:00 /to 2026-09-18 10:00",
            "event reversed /from 2026-09-18 11:00 /to 2026-09-18 10:00",
            "find", "bye extra"};

        for (String command : commands) {
            String reply = bot.getResponse(command);

            assertFalse(reply.isBlank());
            assertFalse(reply.contains("Got it. I've added"));
            assertEquals(before, bot.getResponse("list"));
            assertFalse(bot.isExiting());
        }
    }

    @Test
    public void getResponse_validBye_stopsFurtherChanges() {
        Path save = directory.resolve("tasks.txt");
        SlotBot bot = new SlotBot(save);

        assertTrue(bot.getResponse(" bye ").contains("See you next time"));
        assertTrue(bot.isExiting());

        bot.getResponse("todo ignored");
        assertFalse(Files.exists(save));
    }

    @Test
    public void getWelcome_corruptedRecord_includesWarningAndLoadsValidTask() throws IOException {
        Path save = directory.resolve("tasks.txt");
        Files.writeString(save, "broken\nT | 0 | kept\n");
        SlotBot bot = new SlotBot(save);

        assertTrue(bot.getWelcome().contains("Hello! I'm SlotBot."));
        assertTrue(bot.getWelcome().contains("Ignoring invalid task data on line 1"));
        assertTrue(bot.getResponse("list").contains("kept"));
        assertFalse(bot.getResponse("list").contains("Warning"));
    }

    @Test
    public void getResponse_unwritableSave_includesWarning() throws IOException {
        Path parentFile = directory.resolve("file");
        Files.writeString(parentFile, "not a directory");
        SlotBot bot = new SlotBot(parentFile.resolve("tasks.txt"));

        assertTrue(bot.getResponse("todo unsaved").contains("Warning: Unable to save tasks to disk."));
    }

    @Test
    public void getResponse_reminders_returnsUpcomingUnfinishedDeadlines() {
        Clock clock = Clock.fixed(Instant.parse("2026-09-11T00:00:00Z"), ZoneOffset.UTC);
        SlotBot bot = new SlotBot(directory.resolve("tasks.txt"), clock);

        bot.getResponse("deadline later /by 2026-09-18");
        bot.getResponse("deadline today /by 2026-09-11");
        bot.getResponse("deadline completed /by 2026-09-12");
        bot.getResponse("deadline outside /by 2026-09-19");
        bot.getResponse("mark 3");

        String response = bot.getResponse("reminders");

        assertTrue(response.contains("today"));
        assertTrue(response.contains("later"));
        assertFalse(response.contains("completed"));
        assertFalse(response.contains("outside"));
        assertTrue(response.indexOf("today") < response.indexOf("later"));
    }

    @Test
    public void getResponse_reminders_emptyMessage() {
        Clock clock = Clock.fixed(Instant.parse("2026-09-11T00:00:00Z"), ZoneOffset.UTC);
        SlotBot bot = new SlotBot(directory.resolve("tasks.txt"), clock);

        assertTrue(bot.getResponse("reminders").contains("No upcoming deadlines."));
    }

    @Test
    public void getResponse_commandsRequiringExactInput_rejectsExtraArguments() {
        SlotBot bot = new SlotBot(directory.resolve("tasks.txt"));

        assertTrue(bot.getResponse("list extra").contains("don't recognise that command"));
        assertTrue(bot.getResponse("reminders extra").contains("don't recognise that command"));
        assertFalse(bot.isExiting());
    }

    @Test
    public void getResponse_unknownCommand_guidanceListsAllCommands() {
        SlotBot bot = new SlotBot(directory.resolve("tasks.txt"));

        String response = bot.getResponse("unknown");

        assertTrue(response.contains("todo DESCRIPTION"));
        assertTrue(response.contains("deadline DESCRIPTION /by DATE"));
        assertTrue(response.contains("event DESCRIPTION /from START /to END"));
        assertTrue(response.contains("list"));
        assertTrue(response.contains("find KEYWORD"));
        assertTrue(response.contains("reminders"));
        assertTrue(response.contains("mark [NUMBER]"));
        assertTrue(response.contains("unmark [NUMBER]"));
        assertTrue(response.contains("delete [NUMBER]"));
        assertTrue(response.contains("bye"));
    }

    @Test
    public void getResponse_duplicateTasks_addsBothTasks() {
        SlotBot bot = new SlotBot(directory.resolve("tasks.txt"));

        bot.getResponse("todo read book");
        bot.getResponse("todo read book");
        String response = bot.getResponse("list");

        assertTrue(response.contains("1. [T][ ] read book"));
        assertTrue(response.contains("2. [T][ ] read book"));
    }

    @Test
    public void getResponse_afterExit_returnsGoodbyeWithoutChangingTasks() {
        Path save = directory.resolve("tasks.txt");
        SlotBot bot = new SlotBot(save);
        bot.getResponse("todo saved");
        bot.getResponse("bye");

        String response = bot.getResponse("delete 1");

        assertTrue(response.contains("See you next time"));
        assertTrue(new SlotBot(save).getResponse("list").contains("saved"));
    }
}
