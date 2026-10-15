package bot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import bot.storage.Storage;

/**
 * Tests command execution shared by the console and JavaFX interfaces.
 */
class BotTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    void getResponse_addAndListTasks_returnsFormattedTaskList() {
        Bot bot = createBot();

        assertEquals("""
                Got it. I've added this task:
                    [T][ ] borrow book
                Now you have 1 tasks in the list.""", bot.getResponse("todo borrow book"));
        assertEquals("""
                Got it. I've added this task:
                    [D][ ] return book (by: Dec 02 2019)
                Now you have 2 tasks in the list.""",
                bot.getResponse("deadline return book /by 2019-12-02"));
        assertEquals("""
                Got it. I've added this task:
                    [E][ ] project meeting (from: Dec 02 2019, 2:00PM to: Dec 02 2019, 4:00PM)
                Now you have 3 tasks in the list.""",
                bot.getResponse("event project meeting /from 2019-12-02 1400 /to 2019-12-02 1600"));
        assertEquals("""
                Here are the tasks in your list:
                1.[T][ ] borrow book
                2.[D][ ] return book (by: Dec 02 2019)
                3.[E][ ] project meeting (from: Dec 02 2019, 2:00PM to: Dec 02 2019, 4:00PM)""",
                bot.getResponse("list"));
    }

    @Test
    void getResponse_multilineResponse_usesPlatformIndependentLineFeeds() {
        Bot bot = createBot();

        String response = bot.getResponse("todo borrow book");

        assertFalse(response.contains("\r"));
        assertEquals(2, response.chars().filter(character -> character == '\n').count());
    }

    @Test
    void getResponse_markUnmarkAndDelete_updatesTaskList() {
        Bot bot = createBot();
        bot.getResponse("todo read book");
        bot.getResponse("deadline return book /by 2019-12-02");

        assertEquals("""
                Nice! I've marked this task as done:
                    [T][X] read book""", bot.getResponse("mark 1"));
        assertEquals("""
                OK, I've marked this task as not done yet:
                    [T][ ] read book""", bot.getResponse("unmark 1"));
        assertEquals("""
                Noted. I've removed this task:
                    [T][ ] read book
                Now you have 1 tasks in the list.""", bot.getResponse("delete 1"));
        assertEquals("""
                Here are the tasks in your list:
                1.[D][ ] return book (by: Dec 02 2019)""", bot.getResponse("list"));
    }

    @Test
    void getResponse_findAndDateQuery_returnsMatchingTasks() {
        Bot bot = createBot();
        bot.getResponse("todo read book");
        bot.getResponse("deadline return book /by 2019-12-02");
        bot.getResponse("event meeting /from 2019-12-01 1400 /to 2019-12-03 1600");

        assertEquals("""
                Here are the matching tasks in your list:
                1.[T][ ] read book
                2.[D][ ] return book (by: Dec 02 2019)""", bot.getResponse("find book"));
        assertEquals("""
                Here are the deadlines and events on Dec 02 2019:
                2.[D][ ] return book (by: Dec 02 2019)
                3.[E][ ] meeting (from: Dec 01 2019, 2:00PM to: Dec 03 2019, 4:00PM)""",
                bot.getResponse("on 2019-12-02"));
    }

    @Test
    void getResponse_sortTasks_ordersTasksAndSavesNewOrder() throws IOException {
        Path dataFile = temporaryDirectory.resolve("data").resolve("bot.txt");
        Bot bot = new Bot(new Storage(dataFile.toFile()));
        bot.getResponse("todo buy milk");
        bot.getResponse("event later /from 2019-12-10 1400 /to 2019-12-10 1600");
        bot.getResponse("deadline submit report /by 2019-12-05");
        bot.getResponse("event earlier /from 2019-12-01 0900 /to 2019-12-01 1000");
        bot.getResponse("todo read book");

        assertEquals("""
                Here are your tasks sorted chronologically:
                1.[E][ ] earlier (from: Dec 01 2019, 9:00AM to: Dec 01 2019, 10:00AM)
                2.[D][ ] submit report (by: Dec 05 2019)
                3.[E][ ] later (from: Dec 10 2019, 2:00PM to: Dec 10 2019, 4:00PM)
                4.[T][ ] buy milk
                5.[T][ ] read book""", bot.getResponse("sort"));
        String expectedData = String.join(System.lineSeparator(),
                "E | 0 | earlier | 2019-12-01T09:00 | 2019-12-01T10:00",
                "D | 0 | submit report | 2019-12-05",
                "E | 0 | later | 2019-12-10T14:00 | 2019-12-10T16:00",
                "T | 0 | buy milk",
                "T | 0 | read book") + System.lineSeparator();
        assertEquals(expectedData, Files.readString(dataFile));
    }

    @Test
    void getResponse_invalidCommands_returnsErrorsAndContinues() {
        Bot bot = createBot();

        assertEquals("OOPS!!! Please enter a command.", bot.getResponse(""));
        assertEquals("OOPS!!! I don't know what \"blah\" means.", bot.getResponse("blah"));
        assertEquals("OOPS!!! The description of a todo cannot be empty.", bot.getResponse("todo"));
        assertEquals("OOPS!!! The task list is empty.", bot.getResponse("mark 1"));
        assertEquals("OOPS!!! The sort command does not take extra information.",
                bot.getResponse("sort later"));
        assertEquals("Here are the tasks in your list:", bot.getResponse("list"));
    }

    @Test
    void getResponse_duplicateTasks_rejectsDuplicatesAndContinues() {
        Bot bot = createBot();

        bot.getResponse("todo Read Book");
        bot.getResponse("mark 1");
        assertEquals("OOPS!!! That task is already in your list.",
                bot.getResponse("todo read book"));

        bot.getResponse("deadline submit report /by 2019-12-02");
        assertEquals("OOPS!!! That task is already in your list.",
                bot.getResponse("deadline SUBMIT REPORT /by 2019-12-02"));
        assertEquals("""
                Got it. I've added this task:
                    [D][ ] submit report (by: Dec 03 2019)
                Now you have 3 tasks in the list.""",
                bot.getResponse("deadline submit report /by 2019-12-03"));
    }

    @Test
    void getResponse_nullInput_returnsEmptyCommandError() {
        Bot bot = createBot();

        assertEquals("OOPS!!! Please enter a command.", bot.getResponse(null));
    }

    @Test
    void getResponse_storagePathIsDirectory_returnsDetailedSavingError() throws IOException {
        Path dataPath = temporaryDirectory.resolve("bot.txt");
        Files.createDirectory(dataPath);
        Bot bot = new Bot(new Storage(dataPath.toFile()));

        assertEquals("OOPS!!! I couldn't save your tasks: Data file path is not a file: "
                        + dataPath + ". Your latest changes may not be available next time.",
                bot.getResponse("todo read book"));
    }

    @Test
    void getResponse_taskChange_savesAndReloadsTask() throws IOException {
        Path dataFile = temporaryDirectory.resolve("data").resolve("bot.txt");
        Storage storage = new Storage(dataFile.toFile());
        Bot bot = new Bot(storage);

        bot.getResponse("todo read book");
        bot.getResponse("mark 1");

        assertEquals("T | 1 | read book" + System.lineSeparator(), Files.readString(dataFile));
        Bot reloadedBot = new Bot(storage);
        assertEquals("""
                Here are the tasks in your list:
                1.[T][X] read book""", reloadedBot.getResponse("list"));
        assertEquals("", reloadedBot.getStartupMessage());
    }

    @Test
    void constructor_malformedData_exposesLoadingErrorAndUsesEmptyList() throws IOException {
        Path dataFile = temporaryDirectory.resolve("bot.txt");
        Files.writeString(dataFile, "X | 0 | invalid task");

        Bot bot = new Bot(new Storage(dataFile.toFile()));

        assertEquals("""
                OOPS!!! I couldn't load your data file: invalid data on line 1 (unknown task type 'X'). \
                I started with an empty list.""", bot.getStartupMessage());
        assertEquals("Here are the tasks in your list:", bot.getResponse("list"));
    }

    @Test
    void getResponse_byeCommand_setsExitFlag() {
        Bot bot = createBot();

        assertFalse(bot.shouldExit());
        assertEquals("Bye. Hope to see you again soon!", bot.getResponse("bye"));
        assertTrue(bot.shouldExit());
    }

    @Test
    void getResponse_byeWithArguments_returnsErrorWithoutSettingExitFlag() {
        Bot bot = createBot();

        assertEquals("OOPS!!! Use bye without any extra words.", bot.getResponse("bye later"));
        assertFalse(bot.shouldExit());
    }

    /**
     * Creates a bot using an isolated data file for each test.
     *
     * @return bot with temporary storage
     */
    private Bot createBot() {
        Path dataFile = temporaryDirectory.resolve("data").resolve("bot.txt");
        return new Bot(new Storage(dataFile.toFile()));
    }
}
