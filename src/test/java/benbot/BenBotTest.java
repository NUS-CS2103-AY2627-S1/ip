package benbot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests the application coordinator's loading and interaction delegation. */
class BenBotTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void constructor_defaultComponents_processesCommand() {
        BenBot benBot = new BenBot();

        String response = benBot.getResponse("todo read book");

        assertTrue(response.contains("I've added this task"));
        assertFalse(benBot.isExitRequested());
    }

    @Test
    void load_delegatesToStoredTaskLoader() throws Exception {
        Path storedTaskPath = temporaryDirectory.resolve("stored-task");
        Files.writeString(storedTaskPath, "todo read book\n");
        Task[] tasks = new Task[2];
        int[] taskCount = {0};
        BenBot benBot = new BenBot(tasks, taskCount, new TaskLoader(2),
                new StoredTaskLoader(storedTaskPath),
                new Ui(new Scanner(""), new TaskDataStore(temporaryDirectory.resolve("output"))));

        String output = OutputCapture.capture(benBot::load);

        assertEquals(1, taskCount[0]);
        assertEquals("todo read book", tasks[0].toStorageString());
        assertTrue(output.contains("1 item already in storage."));
    }

    @Test
    void run_delegatesToUiCommandLoop() throws Exception {
        Path storedTaskPath = temporaryDirectory.resolve("stored-task");
        Task[] tasks = new Task[2];
        int[] taskCount = {0};
        BenBot benBot = new BenBot(tasks, taskCount, new TaskLoader(2),
                new StoredTaskLoader(temporaryDirectory.resolve("input")),
                new Ui(new Scanner("todo read book\nbye\n"), new TaskDataStore(storedTaskPath)));

        BenBot finalBenBot = benBot;
        OutputCapture.capture(finalBenBot::run);

        assertEquals(1, taskCount[0]);
        assertEquals("todo read book" + System.lineSeparator(), Files.readString(storedTaskPath));
    }

    @Test
    void run_afterFailedLoad_preservesOriginalFileWithoutStartingCommandLoop() throws Exception {
        Path storedTaskPath = temporaryDirectory.resolve("stored-task");
        String corruptedData = "todo read book\ninvalid command\n";
        Files.writeString(storedTaskPath, corruptedData);
        Task[] tasks = new Task[2];
        int[] taskCount = {0};
        BenBot benBot = new BenBot(tasks, taskCount, new TaskLoader(2),
                new StoredTaskLoader(storedTaskPath),
                new Ui(new Scanner("bye\n"), new TaskDataStore(storedTaskPath)));

        OutputCapture.capture(benBot::load);
        benBot.run();

        assertEquals(0, taskCount[0]);
        assertEquals(corruptedData, Files.readString(storedTaskPath));
    }

    @Test
    void getResponse_processesGuiCommandsAndStoresOnExit() throws Exception {
        Path storedTaskPath = temporaryDirectory.resolve("stored-task");
        Task[] tasks = new Task[2];
        int[] taskCount = {0};
        BenBot benBot = new BenBot(tasks, taskCount, new TaskLoader(2),
                new StoredTaskLoader(temporaryDirectory.resolve("input")),
                new Ui(new Scanner(""), new TaskDataStore(storedTaskPath)));

        String addResponse = benBot.getResponse("todo read book");
        String exitResponse = benBot.getResponse("bye");

        assertTrue(addResponse.contains("I've added this task"));
        assertTrue(exitResponse.contains("Hope to see you again soon"));
        assertTrue(benBot.isExitRequested());
        assertEquals("todo read book" + System.lineSeparator(), Files.readString(storedTaskPath));
    }

    @Test
    void getResponse_surroundingWhitespace_trimsCommand() {
        Task[] tasks = new Task[1];
        int[] taskCount = {0};
        BenBot benBot = new BenBot(tasks, taskCount, new TaskLoader(1),
                new StoredTaskLoader(temporaryDirectory.resolve("input")),
                new Ui(new Scanner(""),
                        new TaskDataStore(temporaryDirectory.resolve("stored-task"))));

        String response = benBot.getResponse("  todo read book  ");

        assertTrue(response.contains("I've added this task"));
        assertEquals(1, taskCount[0]);
        assertEquals("todo read book", tasks[0].toStorageString());
        assertFalse(benBot.isExitRequested());
    }

    @Test
    void save_storageFailure_returnsHelpfulError() throws Exception {
        Path parentFile = temporaryDirectory.resolve("parent-file");
        Files.writeString(parentFile, "not a directory");
        BenBot benBot = new BenBot(new Task[1], new int[] {0}, new TaskLoader(1),
                new StoredTaskLoader(temporaryDirectory.resolve("input")),
                new Ui(new Scanner(""), new TaskDataStore(parentFile.resolve("stored-task"))));

        String response = benBot.save();

        assertEquals(Ui.STORAGE_ERROR_MESSAGE, response);
    }

    @Test
    void getResponse_byeWithStorageFailure_replacesGoodbyeAndKeepsRunning() throws Exception {
        Path parentFile = temporaryDirectory.resolve("parent-file");
        Files.writeString(parentFile, "not a directory");
        BenBot benBot = new BenBot(new Task[1], new int[] {0}, new TaskLoader(1),
                new StoredTaskLoader(temporaryDirectory.resolve("input")),
                new Ui(new Scanner(""), new TaskDataStore(parentFile.resolve("stored-task"))));

        String response = benBot.getResponse("bye");

        assertEquals(Ui.STORAGE_ERROR_MESSAGE + System.lineSeparator()
                + BenBot.SAVE_RETRY_MESSAGE, response);
        assertFalse(benBot.isExitRequested());
    }

    @Test
    void getResponse_byeAfterFailedLoad_preservesOriginalFileAndKeepsRunning() throws Exception {
        Path storedTaskPath = temporaryDirectory.resolve("stored-task");
        String corruptedData = "todo read book\ninvalid command\n";
        Files.writeString(storedTaskPath, corruptedData);
        BenBot benBot = new BenBot(new Task[2], new int[] {0}, new TaskLoader(2),
                new StoredTaskLoader(storedTaskPath),
                new Ui(new Scanner(""), new TaskDataStore(storedTaskPath)));

        OutputCapture.capture(benBot::load);
        String response = benBot.getResponse("bye");

        assertEquals(BenBot.LOAD_FAILURE_SAVE_ERROR, response);
        assertEquals(BenBot.LOAD_FAILURE_SAVE_ERROR, benBot.getLoadFailureMessage());
        assertFalse(response.contains("Hope to see you again soon"));
        assertFalse(benBot.isExitRequested());
        assertEquals(corruptedData, Files.readString(storedTaskPath));
    }

    @Test
    void getResponse_afterFailedBye_processesCommandsAndRetriesSave() throws Exception {
        Path parentFile = temporaryDirectory.resolve("parent-file");
        Path storedTaskPath = parentFile.resolve("stored-task");
        Files.writeString(parentFile, "not a directory");
        Task[] tasks = new Task[2];
        int[] taskCount = {0};
        BenBot benBot = new BenBot(tasks, taskCount, new TaskLoader(2),
                new StoredTaskLoader(temporaryDirectory.resolve("input")),
                new Ui(new Scanner(""), new TaskDataStore(storedTaskPath)));

        String failedExitResponse = benBot.getResponse("bye");
        String addResponse = benBot.getResponse("todo read book");
        Files.delete(parentFile);
        Files.createDirectory(parentFile);
        String successfulExitResponse = benBot.getResponse("bye");

        assertTrue(failedExitResponse.contains(Ui.STORAGE_ERROR_MESSAGE));
        assertTrue(addResponse.contains("I've added this task"));
        assertEquals("Bye. Hope to see you again soon!", successfulExitResponse);
        assertTrue(benBot.isExitRequested());
        assertEquals("todo read book" + System.lineSeparator(), Files.readString(storedTaskPath));
    }
}
