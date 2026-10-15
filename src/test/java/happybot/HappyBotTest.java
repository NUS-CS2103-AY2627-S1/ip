package happybot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class HappyBotTest {
    private static final Clock FIXED_CLOCK = Clock.fixed(
            Instant.parse("2026-09-16T09:00:00Z"), ZoneOffset.UTC);

    private final InputStream originalInput = System.in;
    private final PrintStream originalOutput = System.out;

    @AfterEach
    void restoreStandardStreams() {
        System.setIn(originalInput);
        System.setOut(originalOutput);
    }

    @Test
    public void getResponse_addAndList_taskShown(@TempDir Path tempDir) {
        HappyBot happyBot = new HappyBot(tempDir.resolve("tasks.txt"));

        String addResponse = happyBot.getResponse("todo read book");
        String listResponse = happyBot.getResponse("list");

        assertTrue(addResponse.contains("[T][ ] read book"));
        assertTrue(listResponse.contains("1.[T][ ] read book"));
    }

    @Test
    public void getResponse_invalidCommand_errorReturned(@TempDir Path tempDir) {
        HappyBot happyBot = new HappyBot(tempDir.resolve("tasks.txt"));

        String response = happyBot.getResponse("dance");

        assertEquals(" Oops! I don't know what that means :-(", response);
    }

    @Test
    public void getResponse_whitespaceAroundCommand_taskAddedWithNormalizedDescription(@TempDir Path tempDir) {
        HappyBot happyBot = new HappyBot(tempDir.resolve("tasks.txt"));

        String response = happyBot.getResponse("  todo\tread   book  ");

        assertTrue(response.contains("[T][ ] read book"));
    }

    @Test
    public void getResponse_blankCommand_errorReturned(@TempDir Path tempDir) {
        HappyBot happyBot = new HappyBot(tempDir.resolve("tasks.txt"));

        assertEquals(" Oops! Please enter a command.", happyBot.getResponse("   \t"));
    }

    @Test
    public void getResponse_emptyTaskListMarkCommand_errorReturned(@TempDir Path tempDir) {
        HappyBot happyBot = new HappyBot(tempDir.resolve("tasks.txt"));

        String response = happyBot.getResponse("mark 1");

        assertEquals(" Oops! There are no tasks to mark.", response);
    }

    @Test
    public void getResponse_eventStartEqualsEnd_errorReturned(@TempDir Path tempDir) {
        HappyBot happyBot = new HappyBot(tempDir.resolve("tasks.txt"));

        String response = happyBot.getResponse("event meeting /from 2019-12-01 /to 2019-12-01");

        assertEquals(" Oops! An event must start before it ends.", response);
    }

    @Test
    public void getResponse_eventStartAfterEnd_errorReturned(@TempDir Path tempDir) {
        HappyBot happyBot = new HappyBot(tempDir.resolve("tasks.txt"));

        String response = happyBot.getResponse("event meeting /from 2019-12-02 /to 2019-12-01");

        assertEquals(" Oops! An event must start before it ends.", response);
    }

    @Test
    public void getResponse_dateOnlyDeadline_pastDateRejectedAndTodayAccepted(@TempDir Path tempDir) {
        HappyBot happyBot = new HappyBot(tempDir.resolve("tasks.txt"), FIXED_CLOCK);

        try {
            assertEquals(" Oops! A deadline cannot be due in the past.",
                    happyBot.getResponse("deadline old task /by 2026-09-15"));
            assertTrue(happyBot.getResponse("deadline today task /by 2026-09-16")
                    .contains("[D][ ] today task"));
            assertFalse(happyBot.getResponse("list").contains("old task"));
        } finally {
            happyBot.close();
        }
    }

    @Test
    public void getResponse_timedDeadline_pastAndCurrentTimesRejectedAndFutureTimeAccepted(
            @TempDir Path tempDir) {
        HappyBot happyBot = new HappyBot(tempDir.resolve("tasks.txt"), FIXED_CLOCK);

        try {
            assertEquals(" Oops! A deadline cannot be due in the past.",
                    happyBot.getResponse("deadline midnight task /by 2026-09-16 0000"));
            assertEquals(" Oops! A deadline cannot be due in the past.",
                    happyBot.getResponse("deadline old task /by 2026-09-16 0800"));
            assertEquals(" Oops! A deadline cannot be due in the past.",
                    happyBot.getResponse("deadline now task /by 2026-09-16 0900"));
            assertTrue(happyBot.getResponse("deadline later task /by 2026-09-16 0901")
                    .contains("[D][ ] later task"));
        } finally {
            happyBot.close();
        }
    }

    @Test
    public void getResponse_dateOnlyEvent_pastEndRejectedAndTodayEndAccepted(@TempDir Path tempDir) {
        HappyBot happyBot = new HappyBot(tempDir.resolve("tasks.txt"), FIXED_CLOCK);

        try {
            assertEquals(" Oops! An event cannot end in the past.",
                    happyBot.getResponse("event old meeting /from 2026-09-14 /to 2026-09-15"));
            assertTrue(happyBot.getResponse("event today meeting /from 2026-09-15 /to 2026-09-16")
                    .contains("[E][ ] today meeting"));
            assertFalse(happyBot.getResponse("list").contains("old meeting"));
        } finally {
            happyBot.close();
        }
    }

    @Test
    public void getResponse_timedEvent_pastAndCurrentEndsRejectedAndFutureEndAccepted(
            @TempDir Path tempDir) {
        HappyBot happyBot = new HappyBot(tempDir.resolve("tasks.txt"), FIXED_CLOCK);

        try {
            assertEquals(" Oops! An event cannot end in the past.",
                    happyBot.getResponse("event midnight meeting /from 2026-09-15 /to 2026-09-16 0000"));
            assertEquals(" Oops! An event cannot end in the past.",
                    happyBot.getResponse("event old meeting /from 2026-09-16 0700 /to 2026-09-16 0800"));
            assertEquals(" Oops! An event cannot end in the past.",
                    happyBot.getResponse("event now meeting /from 2026-09-16 0800 /to 2026-09-16 0900"));
            assertTrue(happyBot.getResponse("event later meeting /from 2026-09-16 0800 /to 2026-09-16 1000")
                    .contains("[E][ ] later meeting"));
        } finally {
            happyBot.close();
        }
    }

    @Test
    public void getResponse_byeCommand_goodbyeReturned(@TempDir Path tempDir) {
        HappyBot happyBot = new HappyBot(tempDir.resolve("tasks.txt"));

        String response = happyBot.getResponse("bye");

        assertEquals("Bye. Hope to see you again soon!", response);
    }

    @Test
    public void getResponse_noArgumentCommandWithArgument_usageErrorReturned(@TempDir Path tempDir) {
        HappyBot happyBot = new HappyBot(tempDir.resolve("tasks.txt"));

        assertEquals(" Oops! Use: list.", happyBot.getResponse("list now"));
        assertEquals(" Oops! Use: bye.", happyBot.getResponse("bye now"));
        assertFalse(happyBot.isExitCommand("bye now"));
    }

    @Test
    public void getResponse_duplicateTask_errorReturnedAndListUnchanged(@TempDir Path tempDir) {
        HappyBot happyBot = new HappyBot(tempDir.resolve("tasks.txt"));
        happyBot.getResponse("todo read book");

        String response = happyBot.getResponse("todo READ book");
        String listResponse = happyBot.getResponse("list");

        assertEquals(" Oops! That task is already in your list.", response);
        assertTrue(listResponse.contains("1.[T][ ] read book"));
        assertFalse(listResponse.contains("2."));
    }

    @Test
    public void constructor_savedTask_taskLoaded(@TempDir Path tempDir) {
        Path dataFile = tempDir.resolve("tasks.txt");
        HappyBot firstSession = new HappyBot(dataFile);
        firstSession.getResponse("todo read book");
        firstSession.close();

        HappyBot secondSession = new HappyBot(dataFile);

        assertTrue(secondSession.getResponse("list").contains("1.[T][ ] read book"));
    }

    @Test
    public void getResponse_stats_currentWeekStatisticsReturned(@TempDir Path tempDir) {
        HappyBot happyBot = new HappyBot(tempDir.resolve("tasks.txt"), FIXED_CLOCK);
        happyBot.getResponse("todo read book");
        happyBot.getResponse("deadline submit report /by 2026-09-18");
        happyBot.getResponse("event team meeting /from 2026-09-16 /to 2026-09-17");
        happyBot.getResponse("deadline pay fees /by 2026-09-19");
        happyBot.getResponse("mark 1");
        happyBot.getResponse("mark 2");
        happyBot.getResponse("mark 3");

        String response = happyBot.getResponse("stats");

        assertEquals(" Here are your statistics for this week:\n"
                        + " Total tasks: 4\n"
                        + " Completed this week: 3\n"
                        + "   To-dos: 1\n"
                        + "   Deadlines: 1\n"
                        + "   Events: 1\n"
                        + " Uncompleted deadlines due this week: 1",
                response);
    }

    @Test
    public void getResponse_statsWithArgument_usageErrorReturned(@TempDir Path tempDir) {
        HappyBot happyBot = new HappyBot(tempDir.resolve("tasks.txt"), FIXED_CLOCK);

        assertEquals(" Oops! Use: stats.", happyBot.getResponse("stats week"));
    }

    @Test
    public void getResponse_statsAfterReload_completedTaskStillCounted(@TempDir Path tempDir) {
        Path dataFile = tempDir.resolve("tasks.txt");
        HappyBot firstSession = new HappyBot(dataFile, FIXED_CLOCK);
        firstSession.getResponse("todo read book");
        firstSession.getResponse("mark 1");
        firstSession.close();
        HappyBot secondSession = new HappyBot(dataFile, FIXED_CLOCK);

        String response = secondSession.getResponse("stats");

        assertTrue(response.contains("Completed this week: 1"));
    }

    @Test
    public void constructor_dataFileInUse_noticeAndCommandErrorReturned(@TempDir Path tempDir) {
        Path dataFile = tempDir.resolve("tasks.txt");
        HappyBot firstSession = new HappyBot(dataFile);
        HappyBot secondSession = new HappyBot(dataFile);

        try {
            assertTrue(secondSession.getWelcomeMessage().contains("already open in another HappyBot session"));
            assertEquals(" Oops! Your task list at " + dataFile
                            + " is already open in another HappyBot session. Close the other session, then"
                            + " restart this HappyBot session.",
                    secondSession.getResponse("todo read book"));
        } finally {
            secondSession.close();
            firstSession.close();
        }
    }

    @Test
    public void getResponse_dueAndFindCommands_matchingTasksReturned(@TempDir Path tempDir) {
        HappyBot happyBot = new HappyBot(tempDir.resolve("tasks.txt"), FIXED_CLOCK);

        try {
            happyBot.getResponse("todo read book");
            happyBot.getResponse("deadline return book /by 2026-09-16 1800");

            assertEquals(" Here are the deadlines due on Sep 16 2026:\n"
                            + " 2.[D][ ] return book (by: Sep 16 2026 6:00PM)",
                    happyBot.getResponse("due 2026-09-16"));
            assertEquals(" Here are the matching tasks in your list:\n"
                            + " 1.[T][ ] read book\n"
                            + " 2.[D][ ] return book (by: Sep 16 2026 6:00PM)",
                    happyBot.getResponse("find BOOK"));
        } finally {
            happyBot.close();
        }
    }

    @Test
    public void getResponse_noMatchingDueOrFindCommand_noticeReturned(@TempDir Path tempDir) {
        HappyBot happyBot = new HappyBot(tempDir.resolve("tasks.txt"));

        try {
            happyBot.getResponse("todo read book");

            assertEquals(" There are no deadlines due on Sep 16 2026.",
                    happyBot.getResponse("due 2026-09-16"));
            assertEquals(" There are no tasks matching \"pizza\".", happyBot.getResponse("find pizza"));
        } finally {
            happyBot.close();
        }
    }

    @Test
    public void getResponse_markUnmarkAndDeleteTask_eachChangeConfirmed(@TempDir Path tempDir) {
        HappyBot happyBot = new HappyBot(tempDir.resolve("tasks.txt"), FIXED_CLOCK);

        try {
            happyBot.getResponse("todo read book");

            assertEquals(" Nice! I've marked this task as done:\n   [T][X] read book",
                    happyBot.getResponse("mark 1"));
            assertEquals(" OK, I've marked this task as not done yet:\n   [T][ ] read book",
                    happyBot.getResponse("unmark 1"));
            assertEquals(" Alrighties I've removed this task:\n"
                            + "   [T][ ] read book\n"
                            + " Now you have 0 tasks in the list.",
                    happyBot.getResponse("delete 1"));
        } finally {
            happyBot.close();
        }
    }

    @Test
    public void getResponse_emptyTaskListUnmarkAndDelete_errorsReturned(@TempDir Path tempDir) {
        HappyBot happyBot = new HappyBot(tempDir.resolve("tasks.txt"));

        try {
            assertEquals(" Oops! There are no tasks to unmark.", happyBot.getResponse("unmark 1"));
            assertEquals(" Oops! There are no tasks to delete.", happyBot.getResponse("delete 1"));
        } finally {
            happyBot.close();
        }
    }

    @Test
    public void getResponse_unusableOrOutOfRangeTaskNumber_errorReturned(@TempDir Path tempDir) {
        HappyBot happyBot = new HappyBot(tempDir.resolve("tasks.txt"));

        try {
            happyBot.getResponse("todo read book");

            assertEquals(" Oops! Please choose a valid task number.", happyBot.getResponse("mark 2"));
            assertEquals(" Oops! Please provide one positive whole task number.", happyBot.getResponse("unmark 0"));
            assertEquals(" Oops! Please choose a valid task number.", happyBot.getResponse("delete 2"));
        } finally {
            happyBot.close();
        }
    }

    @Test
    public void getWelcomeMessage_loadingSkippedLines_noticeIncluded(@TempDir Path tempDir) throws Exception {
        Path dataFile = tempDir.resolve("tasks.txt");
        Files.writeString(dataFile, "X | 0 | unreadable\n", StandardCharsets.UTF_8);
        HappyBot happyBot = new HappyBot(dataFile);

        try {
            assertEquals("Hello! I'm HappyBot.\nHow can I cheer you up today?\n\n"
                            + "Heads up! I skipped 1 unreadable line(s) in your saved data.",
                    happyBot.getWelcomeMessage());
        } finally {
            happyBot.close();
        }
    }

    @Test
    public void getWelcomeMessage_loadingDataDirectory_noticeIncluded(@TempDir Path tempDir) throws Exception {
        Path dataFile = tempDir.resolve("tasks.txt");
        Files.createDirectory(dataFile);
        HappyBot happyBot = new HappyBot(dataFile);

        try {
            assertEquals("Hello! I'm HappyBot.\nHow can I cheer you up today?\n\n"
                            + "Heads up! I could not read " + dataFile
                            + ", so I am starting with an empty task list.",
                    happyBot.getWelcomeMessage());
        } finally {
            happyBot.close();
        }
    }

    @Test
    public void getResponse_dataFileDirectoryCannotBeSaved_warningPrecedesConfirmation(@TempDir Path tempDir)
            throws Exception {
        Path dataFile = tempDir.resolve("tasks.txt");
        Files.createDirectory(dataFile);
        HappyBot happyBot = new HappyBot(dataFile);

        try {
            assertEquals(" Heads up! I could not save your tasks to " + dataFile + ".\n"
                            + " Got it. I've added this task:\n"
                            + "   [T][ ] read book\n"
                            + " Now you have 1 tasks in the list.",
                    happyBot.getResponse("todo read book"));
        } finally {
            happyBot.close();
        }
    }

    @Test
    public void getResponse_byeCommands_farewellOrUsageErrorReturned(@TempDir Path tempDir) {
        HappyBot happyBot = new HappyBot(tempDir.resolve("tasks.txt"));

        try {
            assertEquals("Bye. Hope to see you again soon!", happyBot.getResponse("bye"));
            assertEquals("Bye. Hope to see you again soon!", happyBot.getResponse(" \tbye  "));
            assertEquals(" Oops! Use: bye.", happyBot.getResponse("bye now"));
        } finally {
            happyBot.close();
        }
    }

    @Test
    public void isExitCommand_validAndInvalidInputs_correctlyIdentified(@TempDir Path tempDir) {
        HappyBot happyBot = new HappyBot(tempDir.resolve("tasks.txt"));

        try {
            assertTrue(happyBot.isExitCommand(" \tbye  "));
            assertFalse(happyBot.isExitCommand("list"));
            assertFalse(happyBot.isExitCommand("bye now"));
            assertFalse(happyBot.isExitCommand(null));
        } finally {
            happyBot.close();
        }
    }

    @Test
    public void run_endOfInputAfterCommand_welcomeResponseAndFarewellPrinted(@TempDir Path tempDir) {
        System.setIn(new ByteArrayInputStream("list\n".getBytes(StandardCharsets.UTF_8)));
        ByteArrayOutputStream capturedOutput = new ByteArrayOutputStream();
        System.setOut(new PrintStream(capturedOutput, true, StandardCharsets.UTF_8));
        HappyBot happyBot = new HappyBot(tempDir.resolve("tasks.txt"));

        try {
            happyBot.run();

            String output = capturedOutput.toString(StandardCharsets.UTF_8);
            assertTrue(output.contains("Hello! I'm HappyBot.\nHow can I cheer you up today?"));
            assertTrue(output.contains(" Here are the tasks in your list:"));
            assertTrue(output.endsWith("Bye. Hope to see you again soon!\n"
                    + "____________________________________________________________\n"));
        } finally {
            happyBot.close();
        }
    }
}
