package adrian;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests Adrian's command processing independently of the console and JavaFX interfaces.
 */
public class AdrianTest {
    @TempDir
    private Path temporaryDirectory;

    /**
     * Verifies that task commands update, find, and remove tasks through the response API.
     */
    @Test
    public void getResponse_taskCommands_returnsExpectedResponses() {
        Adrian adrian = createAdrian();

        assertTrue(adrian.getResponse("todo read book").contains("[T][ ] read book"));
        assertTrue(adrian.getResponse("deadline submit report /by 2026-09-10 1800")
                .contains("[D][ ] submit report"));
        assertTrue(adrian.getResponse("event meeting /from 2026-09-11 1000 /to 2026-09-11 1100")
                .contains("[E][ ] meeting"));

        String taskList = adrian.getResponse("list");
        assertTrue(taskList.contains("1.[T][ ] read book"));
        assertTrue(taskList.contains("2.[D][ ] submit report"));
        assertTrue(taskList.contains("3.[E][ ] meeting"));

        assertTrue(adrian.getResponse("mark 1").contains("[T][X] read book"));
        assertTrue(adrian.getResponse("unmark 1").contains("[T][ ] read book"));

        String searchResults = adrian.getResponse("find report");
        assertTrue(searchResults.contains("[D][ ] submit report"));
        assertFalse(searchResults.contains("read book"));

        assertTrue(adrian.getResponse("delete 2").contains("[D][ ] submit report"));
        assertFalse(adrian.getResponse("list").contains("submit report"));
    }

    /**
     * Verifies that invalid commands return an explanatory error response.
     */
    @Test
    public void getResponse_invalidCommand_returnsErrorResponse() {
        Adrian adrian = createAdrian();

        assertTrue(adrian.getResponse("unknown").startsWith("OOPS!!!"));
        assertTrue(adrian.getResponse("mark 1").contains("That task number does not exist."));
        assertTrue(adrian.getResponse("deadline task /by tomorrow")
                .contains("Please use date format yyyy-MM-dd HHmm."));
        assertTrue(adrian.getResponse(
                "event meeting /from 2026-09-11 1100 /to 2026-09-11 1000")
                .contains("The event end time must be after its start time."));
        assertTrue(adrian.getResponse(
                "event meeting /from 2026-09-11 1100 /to 2026-09-11 1100")
                .contains("The event end time must be after its start time."));
    }

    /**
     * Verifies that a duration command adds a fixed-duration task.
     */
    @Test
    public void getResponse_validDurationCommand_addsFixedDurationTask() {
        Adrian adrian = createAdrian();

        String response = adrian.getResponse("duration read sales report /for 120");

        assertTrue(response.contains("Amaze. Amaze. Amaze."));
        assertTrue(response.contains("[F][ ] read sales report (duration: 120 minutes)"));
        assertTrue(adrian.getResponse("list")
                .contains("1.[F][ ] read sales report (duration: 120 minutes)"));
    }

    /**
     * Verifies that malformed duration commands return explanatory error responses.
     */
    @Test
    public void getResponse_invalidDurationCommands_returnErrorResponses() {
        Adrian adrian = createAdrian();

        assertTrue(adrian.getResponse("duration read report")
                .contains("Please specify the task duration using /for."));
        assertTrue(adrian.getResponse("duration /for 60")
                .contains("The description of a fixed-duration task cannot be empty."));
        assertTrue(adrian.getResponse("duration read report /for")
                .contains("The task duration cannot be empty."));
        assertTrue(adrian.getResponse("duration read report /for two")
                .contains("Please specify the duration as a whole number of minutes."));
        assertTrue(adrian.getResponse("duration read report /for 0")
                .contains("The task duration must be positive."));
    }

    /**
     * Verifies that saved tasks are available in a new Adrian session.
     */
    @Test
    public void constructor_savedTaskExists_loadsTask() {
        Storage storage = new Storage(temporaryDirectory.resolve("data/adrian.txt"));
        Adrian firstSession = new Adrian(storage);
        firstSession.getResponse("todo persisted task");

        Adrian secondSession = new Adrian(storage);

        assertTrue(secondSession.getResponse("list").contains("[T][ ] persisted task"));
    }

    /**
     * Verifies that a missing data file starts Adrian with an empty task list.
     */
    @Test
    public void constructor_dataFileMissing_startsWithEmptyTaskList() {
        Adrian adrian = createAdrian();

        assertFalse(adrian.getWelcomeMessage().startsWith("OOPS!!!"));
        assertEquals("Rocky, here is our mission task list:", adrian.getResponse("list"));
    }

    /**
     * Verifies that a completed fixed-duration task is restored in a new session.
     */
    @Test
    public void constructor_savedFixedDurationTaskExists_loadsTask() {
        Storage storage = new Storage(temporaryDirectory.resolve("data/adrian.txt"));
        Adrian firstSession = new Adrian(storage);
        firstSession.getResponse("duration read sales report /for 120");
        firstSession.getResponse("mark 1");

        Adrian secondSession = new Adrian(storage);

        assertTrue(secondSession.getResponse("list")
                .contains("[F][X] read sales report (duration: 120 minutes)"));
    }

    /**
     * Verifies that malformed saved data produces a loading error instead of crashing startup.
     *
     * @throws IOException if the test data file cannot be created.
     */
    @Test
    public void constructor_malformedDataFile_showsLoadingErrorAndStartsEmpty() throws IOException {
        Path dataFile = temporaryDirectory.resolve("data/adrian.txt");
        Files.createDirectories(dataFile.getParent());
        Files.writeString(dataFile, "E | 0 | broken event | not-a-date | also-not-a-date");

        Adrian adrian = new Adrian(new Storage(dataFile));

        assertTrue(adrian.getWelcomeMessage().contains("mission log could not be loaded"));
        assertFalse(adrian.getResponse("list").contains("broken event"));
    }

    /**
     * Verifies that failed saves restore the task list and completion statuses.
     */
    @Test
    public void getResponse_saveFails_rollsBackTaskChanges() {
        ControlledStorage storage = new ControlledStorage(
                temporaryDirectory.resolve("data/adrian.txt"));
        Adrian adrian = new Adrian(storage);

        storage.setShouldFailSaves(true);
        assertTrue(adrian.getResponse("todo unsaved task").contains("could not save"));
        assertFalse(adrian.getResponse("list").contains("unsaved task"));

        storage.setShouldFailSaves(false);
        adrian.getResponse("todo stable task");

        storage.setShouldFailSaves(true);
        assertTrue(adrian.getResponse("mark 1").contains("could not save"));
        assertTrue(adrian.getResponse("list").contains("[T][ ] stable task"));

        storage.setShouldFailSaves(false);
        adrian.getResponse("mark 1");

        storage.setShouldFailSaves(true);
        assertTrue(adrian.getResponse("unmark 1").contains("could not save"));
        assertTrue(adrian.getResponse("list").contains("[T][X] stable task"));
        assertTrue(adrian.getResponse("delete 1").contains("could not save"));
        assertTrue(adrian.getResponse("list").contains("[T][X] stable task"));
    }

    /**
     * Verifies that the exit command returns a farewell and records the exit request.
     */
    @Test
    public void getResponse_bye_requestsExit() {
        Adrian adrian = createAdrian();

        assertTrue(adrian.getResponse("bye").contains("Mission log secured, Rocky."));
        assertTrue(adrian.isExitRequested());
    }

    /**
     * Verifies that the welcome message establishes Adrian and Rocky's mission partnership.
     */
    @Test
    public void getWelcomeMessage_newSession_usesMissionPersonality() {
        Adrian adrian = createAdrian();

        String welcomeMessage = adrian.getWelcomeMessage();

        assertTrue(welcomeMessage.contains("Adrian online. Hello, Rocky."));
        assertTrue(welcomeMessage.contains("Amaze. Amaze. Amaze."));
    }

    /**
     * Creates an Adrian instance backed by an isolated task file.
     *
     * @return Adrian instance for the current test.
     */
    private Adrian createAdrian() {
        Path dataFile = temporaryDirectory.resolve("data/adrian.txt");
        return new Adrian(new Storage(dataFile));
    }

    /**
     * Provides controllable save failures for testing Adrian's rollback behavior.
     */
    private static class ControlledStorage extends Storage {
        private boolean shouldFailSaves;

        ControlledStorage(Path filePath) {
            super(filePath);
        }

        void setShouldFailSaves(boolean shouldFailSaves) {
            this.shouldFailSaves = shouldFailSaves;
        }

        @Override
        public void saveTasks(List<Task> tasks) throws IOException {
            if (shouldFailSaves) {
                throw new IOException("Simulated save failure");
            }

            super.saveTasks(tasks);
        }
    }
}
