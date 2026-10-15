package jassabot;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jassabot.parser.CommandType;

/**
 * Tests the command entry point shared by the GUI and console, including failed saves.
 */
public class JassaBotTest {
    private static final String HELP_RESPONSE = """
            JassaBot commands:
            todo DESCRIPTION - Add a task.
            deadline DESCRIPTION /by DATE [TIME] - Add a deadline.
            event DESCRIPTION /from DATE [TIME] /to DATE [TIME] - Add an event.
            list - Show all tasks and their numbers.
            find KEYWORD - Find descriptions containing KEYWORD, ignoring case.
            mark NUMBER - Mark a task as done.
            unmark NUMBER - Mark a task as not done.
            delete NUMBER - Delete a task.
            help - Show this help.
            bye - Exit JassaBot.

            Replace uppercase placeholders with your values. [TIME] is optional.
            Commands are lowercase. Dates: yyyy-MM-dd or d/M/yyyy. Time: 24-hour HHmm.
            Use numbers from list for mark, unmark, and delete; find renumbers its results.

            Examples:
            deadline return book /by 2019-12-02 1800
            event meeting /from 2019-12-02 1400 /to 2019-12-02 1600
            """.stripTrailing();

    private final Path directory = Path.of("build", "test-data", "bot-" + UUID.randomUUID());
    private final Path dataFile = directory.resolve("tasks.txt");
    private JassaBot bot;

    @BeforeEach
    public void setUp() throws IOException {
        Files.createDirectories(directory);
        bot = new JassaBot(dataFile);
    }

    @AfterEach
    public void tearDown() throws IOException {
        List<Path> paths;
        try (Stream<Path> stream = Files.walk(directory)) {
            paths = stream.sorted(Comparator.reverseOrder()).toList();
        }
        for (Path path : paths) {
            Files.deleteIfExists(path);
        }
    }

    @Test
    public void getResponse_help_displaysReferenceWithoutCreatingStorage() {
        bot = new JassaBot(directory.resolve("missing").resolve("tasks.txt"));
        for (String command : List.of("help", "  help  ", "\thelp\t")) {
            assertEquals(HELP_RESPONSE, bot.getResponse(command));
            assertEquals(CommandType.HELP, bot.getCommandType());
            assertFalse(bot.isExitRequested());
            assertFalse(Files.exists(directory.resolve("missing")));
        }
        assertEquals("Your garden is clear. Enjoy the breathing room.", bot.getResponse("list"));
        assertTrue(bot.getResponse("todo after help").contains("[T][ ] after help"));
    }

    @Test
    public void getResponse_helpAndInvalidVariants_preserveTasksAndSavedBytes() throws IOException {
        bot.getResponse("todo keep first");
        bot.getResponse("todo keep second");
        bot.getResponse("mark 2");
        String listing = bot.getResponse("list");
        byte[] saved = Files.readAllBytes(dataFile);
        assertEquals(HELP_RESPONSE, bot.getResponse("help"));
        assertEquals(listing, bot.getResponse("list"));
        assertArrayEquals(saved, Files.readAllBytes(dataFile));
        for (String command : List.of("HELP", "Help", "?", "/help", "--help", "helper",
                "helpful", "help123", "help todo", "help unknown", "help\ttodo")) {
            assertEquals("I couldn't understand that command. Try help to see what you can do.",
                    bot.getResponse(command), command);
            assertEquals(CommandType.UNKNOWN, bot.getCommandType());
            assertFalse(bot.isExitRequested());
            assertEquals(listing, bot.getResponse("list"));
            assertArrayEquals(saved, Files.readAllBytes(dataFile));
        }
        assertEquals(HELP_RESPONSE, bot.getResponse("help"));
    }

    @Test
    public void getResponse_helpWithUnavailableStorage_remainsAvailable() throws IOException {
        Files.createDirectory(dataFile);
        bot = new JassaBot(dataFile);
        assertTrue(bot.getWelcome().contains("WARNING: The task data path is not a regular file."));
        assertEquals(HELP_RESPONSE, bot.getResponse("help"));
        assertEquals(CommandType.HELP, bot.getCommandType());
        assertEquals("Your garden is clear. Enjoy the breathing room.", bot.getResponse("list"));
        assertTrue(Files.isDirectory(dataFile));
    }

    @Test
    public void getResponse_taskLifecycle_preservesStateAndSavesChanges() {
        assertEquals("Planted a new task:\n  [T][ ] read book\n"
                + "Your garden now holds 1 task.", bot.getResponse("  todo read book  "));
        assertEquals(CommandType.TODO, bot.getCommandType());
        assertEquals("Planted a new task:\n  [D][ ] return book (by: Dec 2 2019, 6:00 PM)\n"
                + "Your garden now holds 2 tasks.",
                bot.getResponse("deadline return book /by 2019-12-02 1800"));
        assertEquals("Planted a new task:\n"
                + "  [E][ ] meeting (from: Dec 2 2019, 2:00 PM to: Dec 2 2019, 4:00 PM)\n"
                + "Your garden now holds 3 tasks.",
                bot.getResponse("event meeting /from 2019-12-02 1400 /to 2019-12-02 1600"));
        assertEquals("A little progress, a little growth. Task completed:\n  [T][X] read book",
                bot.getResponse("mark 1"));
        assertEquals("Room to grow. This task is marked as not done:\n  [T][ ] read book",
                bot.getResponse("unmark 1"));
        assertEquals("Here's what I found in your task garden:\n1.[T][ ] read book\n"
                + "2.[D][ ] return book (by: Dec 2 2019, 6:00 PM)", bot.getResponse("find BOOK"));
        assertTrue(bot.getResponse("delete 2").contains("Your garden now holds 2 tasks."));
        String listing = bot.getResponse("list");
        assertTrue(listing.contains("2.[E][ ] meeting"));
        assertEquals(listing, new JassaBot(dataFile).getResponse("list"));
    }

    @Test
    public void getResponse_emptyGardenAndSearch_preserveTasksAndReflectLastDeletion() throws IOException {
        String emptyGarden = "Your garden is clear. Enjoy the breathing room.";
        String noMatches = "No matching tasks this time. Try another keyword.";
        assertEquals(emptyGarden, bot.getResponse("list"));
        assertEquals(noMatches, bot.getResponse("find seedlings"));
        assertFalse(Files.exists(dataFile));
        bot.getResponse("todo water seedlings");
        byte[] saved = Files.readAllBytes(dataFile);
        assertEquals(noMatches, bot.getResponse("find book"));
        assertArrayEquals(saved, Files.readAllBytes(dataFile));
        assertEquals("Here's what's growing in your task list:\n1.[T][ ] water seedlings",
                bot.getResponse("list"));
        assertEquals("Removed this task. More room for what matters:\n  [T][ ] water seedlings\n"
                + "Your garden now holds 0 tasks.", bot.getResponse("delete 1"));
        assertEquals(emptyGarden, bot.getResponse("list"));
        assertEquals(emptyGarden, new JassaBot(dataFile).getResponse("list"));
    }

    @Test
    public void getResponse_invalidCommands_returnsErrorsWithoutChangingTasks() {
        bot.getResponse("todo keep me");
        String listing = bot.getResponse("list");
        for (String command : List.of(" ", "todo", "deadline bad /by 2019-02-29", "event",
                "find", "unknown", "mark 0", "unmark 99999999999999999", "delete")) {
            String response = bot.getResponse(command);
            assertFalse(response.isBlank());
            assertEquals(CommandType.UNKNOWN, bot.getCommandType(), command);
            assertEquals(listing, bot.getResponse("list"), command);
        }
        assertEquals("Please enter a command.", bot.getResponse(" "));
    }

    @Test
    public void getResponse_markerPrefixes_skipsPartialMatchesAndFindsCompleteMarkers() {
        assertTrue(bot.getResponse("deadline compare /bypass /by 2019-12-02")
                .contains("[D][ ] compare /bypass (by: Dec 2 2019)"));
        assertTrue(bot.getResponse("event travel /fromage /together /from 2019-12-02 /to 2019-12-03")
                .contains("[E][ ] travel /fromage /together"));
        assertEquals("A deadline needs '/by' followed by its due time.",
                bot.getResponse("deadline compare /bypass"));
    }

    @Test
    public void getWelcome_malformedStorage_reportsWarningsAndLoadsValidTasks() throws IOException {
        Files.writeString(dataFile, "T | 1 | saved task\nX | 0 | invalid\n");
        bot = new JassaBot(dataFile);
        assertEquals("Hello, I'm JassaBot.\nLet's make room for a little progress today.\n"
                + "Type help to see available commands.\n"
                + "WARNING: Skipped data line 2: unknown task type 'X'.", bot.getWelcome());
        assertEquals(HELP_RESPONSE, bot.getResponse("help"));
        assertEquals("Here's what's growing in your task list:\n1.[T][X] saved task", bot.getResponse("list"));
    }

    @Test
    public void getResponse_failedSaves_rollBackEveryMutation() throws IOException {
        bot.getResponse("todo keep first");
        bot.getResponse("todo keep second");
        bot.getResponse("mark 2");
        String listing = bot.getResponse("list");
        Files.delete(dataFile);
        Files.createDirectory(dataFile);
        Files.writeString(dataFile.resolve("blocker"), "Force a failed replacement on all platforms.");
        for (String command : List.of("todo rejected", "deadline rejected /by 2019-12-02",
                "event rejected /from 2019-12-02 /to 2019-12-03", "mark 1", "mark 2",
                "unmark 1", "unmark 2", "delete 1")) {
            assertEquals("I couldn't save your tasks, so no changes were made.",
                    bot.getResponse(command), command);
            assertEquals(CommandType.UNKNOWN, bot.getCommandType());
            assertEquals(listing, bot.getResponse("list"), command);
        }
    }

    @Test
    public void getResponse_bye_requestsExitAndPreventsFurtherChanges() throws IOException {
        bot.getResponse("todo saved before exit");
        String saved = Files.readString(dataFile);
        assertFalse(bot.isExitRequested());
        assertEquals("Bye for now. Take your time, and keep growing.", bot.getResponse("  bye  "));
        assertEquals(CommandType.BYE, bot.getCommandType());
        assertTrue(bot.isExitRequested());
        assertEquals("Bye for now. Take your time, and keep growing.", bot.getResponse("todo too late"));
        assertEquals("Bye for now. Take your time, and keep growing.", bot.getResponse("help"));
        assertEquals(CommandType.UNKNOWN, bot.getCommandType());
        assertTrue(bot.isExitRequested());
        assertEquals(saved, Files.readString(dataFile));
    }

    @Test
    public void getResponse_invalidTaskNumbers_preserveMemoryAndSavedFile() throws IOException {
        bot.getResponse("todo first");
        bot.getResponse("todo last");
        String listing = bot.getResponse("list");
        byte[] saved = Files.readAllBytes(dataFile);
        for (String operation : List.of("mark", "unmark", "delete")) {
            for (String number : List.of("", "0", "-1", "3", "2147483647", "2147483648",
                    "-2147483648", "1.0", "one", "1 2")) {
                String command = operation + " " + number;
                assertEquals("Please enter a valid task number.", bot.getResponse(command), command);
                assertEquals(CommandType.UNKNOWN, bot.getCommandType(), command);
                assertEquals(listing, bot.getResponse("list"), command);
                assertArrayEquals(saved, Files.readAllBytes(dataFile), command);
            }
        }
    }

    @Test
    public void getResponse_missingDateFields_returnsSpecificErrorsWithoutSaving() {
        String[][] cases = {
            {"deadline /by 2024-02-29", "The description of a deadline cannot be empty."},
            {"deadline book", "A deadline needs '/by' followed by its due time."},
            {"deadline book /by", "A deadline needs '/by' followed by its due time."},
            {"event /from 2024-02-29 /to 2024-03-01", "The description of an event cannot be empty."},
            {"event meeting /to 2024-03-01", "An event needs both '/from' and '/to' time markers."},
            {"event meeting /from 2024-02-29", "An event needs both '/from' and '/to' time markers."},
            {"event meeting /to 2024-03-01 /from 2024-02-29",
                "An event needs both '/from' and '/to' time markers."},
            {"event meeting /from /to 2024-03-01",
                "An event needs non-empty times after both '/from' and '/to'."},
            {"event meeting /from 2024-02-29 /to",
                "An event needs non-empty times after both '/from' and '/to'."}
        };
        for (String[] testCase : cases) {
            assertEquals(testCase[1], bot.getResponse(testCase[0]), testCase[0]);
            assertEquals(CommandType.UNKNOWN, bot.getCommandType());
            assertEquals("Your garden is clear. Enjoy the breathing room.", bot.getResponse("list"));
            assertFalse(Files.exists(dataFile));
        }
    }

    @Test
    public void getResponse_invalidEventDates_preservesExistingTasks() throws IOException {
        bot.getResponse("todo keep me");
        String listing = bot.getResponse("list");
        byte[] saved = Files.readAllBytes(dataFile);
        for (String command : List.of("event meeting /from 2023-02-29 /to 2024-03-01",
                "event meeting /from 2024-02-29 /to 2024-03-01 2400")) {
            assertEquals("Please enter a valid date as yyyy-MM-dd or d/M/yyyy, "
                    + "optionally followed by a time in HHmm format.", bot.getResponse(command));
            assertEquals(CommandType.UNKNOWN, bot.getCommandType());
            assertEquals(listing, bot.getResponse("list"));
            assertArrayEquals(saved, Files.readAllBytes(dataFile));
        }
    }

    @Test
    public void getResponse_whitespaceAfterMarkers_acceptsDatesAndReportsCommandTypes() {
        assertTrue(bot.getResponse("deadline book /by\t29/2/2024").contains("(by: Feb 29 2024)"));
        assertEquals(CommandType.DEADLINE, bot.getCommandType());
        assertTrue(bot.getResponse("event trip /from\t2024-02-29 /to\t2024-03-01")
                .contains("(from: Feb 29 2024 to: Mar 1 2024)"));
        assertEquals(CommandType.EVENT, bot.getCommandType());
        bot.getResponse("mark   2");
        assertEquals(CommandType.MARK, bot.getCommandType());
        bot.getResponse("unmark   2");
        assertEquals(CommandType.UNMARK, bot.getCommandType());
        bot.getResponse("find trip");
        assertEquals(CommandType.FIND, bot.getCommandType());
        bot.getResponse("delete 2");
        assertEquals(CommandType.DELETE, bot.getCommandType());
        bot.getResponse("list");
        assertEquals(CommandType.LIST, bot.getCommandType());
    }

    @Test
    public void getResponse_repeatedMarkAndUnmark_persistsIdempotentState() {
        bot.getResponse("todo first");
        bot.getResponse("todo last");
        for (String operation : List.of("mark", "unmark")) {
            String firstResponse = bot.getResponse(operation + " 2");
            assertEquals(firstResponse, bot.getResponse(operation + " 2"));
            String listing = bot.getResponse("list");
            assertTrue(listing.contains("1.[T][ ] first"));
            assertTrue(listing.contains(operation.equals("mark") ? "2.[T][X] last" : "2.[T][ ] last"));
            assertEquals(listing, new JassaBot(dataFile).getResponse("list"));
        }
    }

    @Test
    public void getResponse_deleteFirstAndLast_renumbersAndPersists() {
        bot.getResponse("todo first");
        bot.getResponse("todo middle");
        bot.getResponse("todo last");
        assertTrue(bot.getResponse("delete 1").contains("[T][ ] first"));
        assertEquals("Removed this task. More room for what matters:\n  [T][ ] last\n"
                + "Your garden now holds 1 task.", bot.getResponse("delete 2"));
        String expected = "Here's what's growing in your task list:\n1.[T][ ] middle";
        assertEquals(expected, bot.getResponse("list"));
        assertEquals(expected, new JassaBot(dataFile).getResponse("list"));
    }

    @Test
    public void getResponse_failedDeletionAtEveryPosition_restoresOrderAndAllowsRetry() throws IOException {
        bot.getResponse("todo first");
        bot.getResponse("todo middle");
        bot.getResponse("todo last");
        bot.getResponse("mark 2");
        String listing = bot.getResponse("list");
        Files.delete(dataFile);
        Files.createDirectory(dataFile);
        Path blocker = dataFile.resolve("blocker");
        Files.writeString(blocker, "Keep this directory nonempty.");
        for (int number = 1; number <= 3; number++) {
            assertEquals("I couldn't save your tasks, so no changes were made.",
                    bot.getResponse("delete " + number));
            assertEquals(listing, bot.getResponse("list"));
            assertEquals("Keep this directory nonempty.", Files.readString(blocker));
        }
        Files.delete(blocker);
        Files.delete(dataFile);
        assertTrue(bot.getResponse("delete 2").contains("[T][X] middle"));
        assertEquals(bot.getResponse("list"), new JassaBot(dataFile).getResponse("list"));
    }

    @Test
    public void getWelcome_repeatedCalls_doNotAccumulateResponsesOrChangeState() {
        String welcome = "Hello, I'm JassaBot.\nLet's make room for a little progress today.\n"
                + "Type help to see available commands.";
        assertEquals(welcome, bot.getWelcome());
        assertEquals(welcome, bot.getWelcome());
        bot.getResponse("todo retained");
        assertEquals(welcome, bot.getWelcome());
        assertEquals("Here's what's growing in your task list:\n1.[T][ ] retained", bot.getResponse("list"));
    }
}
