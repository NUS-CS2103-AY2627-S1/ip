package daddy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
}
