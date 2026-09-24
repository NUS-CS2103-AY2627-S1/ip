package dobby;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Tests command handling through the UI-independent Dobby facade. */
class DobbyTest {
    private static final Path DATA_FILE = Path.of("data", "dobby.txt");
    private static final Path LEGACY_DATA_FILE = Path.of("data", "duke.txt");

    private boolean hasOriginalDataFile;
    private byte[] originalData;
    private boolean hasOriginalLegacyDataFile;
    private byte[] originalLegacyData;
    private Dobby dobby;

    /** Saves existing task data and starts each test with no saved tasks. */
    @BeforeEach
    void setUp() throws IOException {
        hasOriginalDataFile = Files.exists(DATA_FILE);
        originalData = hasOriginalDataFile ? Files.readAllBytes(DATA_FILE) : null;
        hasOriginalLegacyDataFile = Files.exists(LEGACY_DATA_FILE);
        originalLegacyData = hasOriginalLegacyDataFile ? Files.readAllBytes(LEGACY_DATA_FILE) : null;
        Files.deleteIfExists(DATA_FILE);
        Files.deleteIfExists(LEGACY_DATA_FILE);
        dobby = new Dobby();
    }

    /** Restores the task data that was present before the test. */
    @AfterEach
    void restoreDataFile() throws IOException {
        if (hasOriginalDataFile) {
            Files.createDirectories(DATA_FILE.getParent());
            Files.write(DATA_FILE, originalData);
        } else {
            Files.deleteIfExists(DATA_FILE);
        }
        if (hasOriginalLegacyDataFile) {
            Files.createDirectories(LEGACY_DATA_FILE.getParent());
            Files.write(LEGACY_DATA_FILE, originalLegacyData);
        } else {
            Files.deleteIfExists(LEGACY_DATA_FILE);
        }
    }

    @Test
    void getResponse_taskCommands_shareTaskState() {
        assertEquals("> Dobby noted a new Todo: read book", dobby.getResponse("todo read book"));
        assertEquals("> Dobby show 1 tasks:\n1. [T][ ] read book\n", dobby.getResponse("list"));
    }

    @Test
    void getResponse_exitCommand_returnsGoodbyeMessage() {
        assertEquals("> Dobby says goodbye to master!", dobby.getResponse("bye"));
    }

    @Test
    void getResponse_helpCommand_returnsGuideWithoutChangingTasks() {
        dobby.getResponse("todo read book");

        assertEquals("> Dobby can help with these commands:\n"
                + "Create tasks:\n"
                + "  todo <description> - Add a todo task.\n"
                + "  deadline <description> /by <date/time> - Add a task with a deadline.\n"
                + "  event <description> /from <date/time> /to <date/time> - Add an event.\n"
                + "View tasks:\n"
                + "  list - Show all tasks.\n"
                + "  find <search text> - Show tasks matching text.\n"
                + "Update tasks:\n"
                + "  mark <task number> - Mark a task as done.\n"
                + "  unmark <task number> - Mark a task as not done.\n"
                + "  delete <task number> - Delete a task.\n"
                + "Other:\n"
                + "  help - Show this help page.\n"
                + "  bye - Exit Dobby.\n"
                + "Dates: use yyyy-MM-dd or d/M/yyyy. Times are optional; use HHmm or HH:mm.\n"
                + "Example: deadline return book /by 2019-12-02 1800", dobby.getResponse("help"));
        assertEquals("> Dobby show 1 tasks:\n1. [T][ ] read book\n", dobby.getResponse("list"));
    }

    @Test
    void isExitCommand_exitAndNonExitCommands_returnsMatchingStatus() {
        assertTrue(dobby.isExitCommand("bye"));
        assertFalse(dobby.isExitCommand("list"));
    }
}
