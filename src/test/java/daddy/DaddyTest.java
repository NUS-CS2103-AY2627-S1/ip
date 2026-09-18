package daddy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import daddy.storage.Storage;
import daddy.task.Task;

/**
 * Tests Daddy's command-response API used by the graphical interface.
 */
class DaddyTest {
    /** Provides an isolated directory for each test's saved task data. */
    @TempDir
    Path temporaryDirectory;

    /**
     * Verifies that graphical responses use the same commands, messages, and exit state as the CLI.
     */
    @Test
    void getResponse_validCommands_returnsFormattedMessagesAndTracksExit() {
        Storage storage = new Storage(temporaryDirectory.resolve("daddy.txt"),
                temporaryDirectory.resolve("duke.txt"));
        Daddy daddy = new Daddy(storage);

        String addResponse = daddy.getResponse("todo read book");
        String listResponse = daddy.getResponse("list");

        assertTrue(addResponse.contains("Good. The plan just got stronger:"));
        assertTrue(listResponse.contains("1.[T][ ] read book"));
        assertFalse(daddy.isExitRequested());

        String exitResponse = daddy.getResponse("bye");

        assertTrue(exitResponse.contains("Go make Daddy proud, my friend. See you soon."));
        assertTrue(daddy.isExitRequested());
    }

    /** Verifies that the graphical greeting excludes console-only art and dividers. */
    @Test
    void getWelcomeMessage_graphicalInterface_returnsCleanGreeting() {
        Storage storage = new Storage(temporaryDirectory.resolve("daddy.txt"),
                temporaryDirectory.resolve("duke.txt"));
        Daddy daddy = new Daddy(storage);

        String welcomeMessage = daddy.getWelcomeMessage();

        assertTrue(welcomeMessage.contains("Hello, my friend."));
        assertTrue(welcomeMessage.contains("Big dreams require big actions."));
        assertFalse(welcomeMessage.contains("____"));
        assertFalse(welcomeMessage.contains("|  _"));
    }

    /** Verifies that the graphical interface can read an immutable task-list snapshot. */
    @Test
    void getTasks_afterAddingTask_returnsCurrentImmutableSnapshot() {
        Storage storage = new Storage(temporaryDirectory.resolve("daddy.txt"),
                temporaryDirectory.resolve("duke.txt"));
        Daddy daddy = new Daddy(storage);
        daddy.getResponse("todo read book");

        List<Task> tasks = daddy.getTasks();

        assertEquals(1, tasks.size());
        assertEquals("read book", tasks.get(0).getDescription());
        assertThrows(UnsupportedOperationException.class, () -> tasks.add(tasks.get(0)));
    }

    /** Verifies that command errors are exposed for GUI styling and cleared by the next successful command. */
    @Test
    void hasLastResponseError_invalidThenValidCommand_tracksLatestResult() {
        Storage storage = new Storage(temporaryDirectory.resolve("daddy.txt"),
                temporaryDirectory.resolve("duke.txt"));
        Daddy daddy = new Daddy(storage);

        daddy.getResponse("not a command");
        assertTrue(daddy.hasLastResponseError());

        daddy.getResponse("todo read book");
        assertFalse(daddy.hasLastResponseError());
    }

    /**
     * Verifies that task data saved by one instance is loaded by the next instance.
     */
    @Test
    void constructor_existingSavedTasks_loadsTasksForNewSession() {
        Path dataFile = temporaryDirectory.resolve("daddy.txt");
        Path legacyFile = temporaryDirectory.resolve("duke.txt");
        Daddy firstSession = new Daddy(new Storage(dataFile, legacyFile));
        firstSession.getResponse("todo read book");
        firstSession.getResponse("deadline return book /by 02-12-2026 1800");

        Daddy secondSession = new Daddy(new Storage(dataFile, legacyFile));

        assertEquals(2, secondSession.getTasks().size());
        assertTrue(secondSession.getResponse("list").contains("return book"));
    }

    /**
     * Verifies that loading problems appear in the welcome response.
     *
     * @throws IOException if the invalid data path cannot be created
     */
    @Test
    void getWelcomeMessage_storageLoadingProblem_includesSpecificError() throws IOException {
        Path dataFile = temporaryDirectory.resolve("daddy.txt");
        Files.createDirectory(dataFile);
        Daddy daddy = new Daddy(new Storage(dataFile, temporaryDirectory.resolve("duke.txt")));

        String welcomeMessage = daddy.getWelcomeMessage();

        assertTrue(welcomeMessage.contains("expected a data file"));
    }

    /**
     * Verifies that a failed save is returned as an error without retaining the unsaved task.
     *
     * @throws IOException if the blocking parent file cannot be created
     */
    @Test
    void getResponse_storageSaveFails_returnsErrorAndPreservesTaskState() throws IOException {
        Path blockingParent = temporaryDirectory.resolve("not-a-folder");
        Files.writeString(blockingParent, "blocking file");
        Daddy daddy = new Daddy(new Storage(blockingParent.resolve("daddy.txt"),
                temporaryDirectory.resolve("duke.txt")));

        String response = daddy.getResponse("todo read book");

        assertTrue(response.contains("couldn't save your tasks"));
        assertTrue(daddy.hasLastResponseError());
        assertTrue(daddy.getTasks().isEmpty());
    }
}
