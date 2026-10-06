package moon;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Tests validation of task data loaded from disk. */
public class StorageTest {
    private static final Path DATA_FILE = Path.of("data", "moon.txt");

    private byte[] originalData;
    private boolean dataFileExisted;

    /** Replaces the real task file with an empty isolated file for each persistence test. */
    @BeforeEach
    public void isolateTaskFile() throws IOException {
        dataFileExisted = Files.exists(DATA_FILE);
        originalData = dataFileExisted ? Files.readAllBytes(DATA_FILE) : null;
        Files.createDirectories(DATA_FILE.getParent());
        Files.writeString(DATA_FILE, "", StandardCharsets.UTF_8);
    }

    /** Restores the task file after each persistence test. */
    @AfterEach
    public void restoreTaskFile() throws IOException {
        if (dataFileExisted) {
            Files.write(DATA_FILE, originalData);
        } else {
            Files.deleteIfExists(DATA_FILE);
        }
    }

    /** Verifies all supported task types can be saved and loaded again. */
    @Test
    public void saveAndLoad_roundTripsAllTaskTypes() throws IOException {
        Storage storage = new Storage();
        storage.save(List.of(
                new ToDo("buy milk"),
                new Deadline("submit report", LocalDate.of(2026, 9, 25)),
                new Event("team meeting", LocalDate.of(2026, 9, 20), LocalDate.of(2026, 9, 21))));

        List<Task> loadedTasks = storage.load();

        assertEquals(3, loadedTasks.size());
        assertTrue(loadedTasks.get(0) instanceof ToDo);
        assertTrue(loadedTasks.get(1) instanceof Deadline);
        assertTrue(loadedTasks.get(2) instanceof Event);
        assertEquals("buy milk", loadedTasks.get(0).getDescription());
    }

    /** Verifies completed persisted tasks are restored with their status. */
    @Test
    public void loadFromSaveFormats_completedTask_restoresStatus() throws IOException {
        Storage storage = new Storage();

        List<Task> tasks = storage.loadFromSaveFormats(List.of("T | 1 | read book"));

        assertEquals("X", tasks.get(0).getStatusIcon());
    }

    /** Verifies malformed task records of every supported type are rejected. */
    @Test
    public void loadFromSaveFormats_malformedRecords_throwsIOException() {
        Storage storage = new Storage();

        assertThrows(IOException.class, () -> storage.loadFromSaveFormats(List.of("T | 2 | task")));
        assertThrows(IOException.class, () -> storage.loadFromSaveFormats(List.of("X | 0 | task")));
        assertThrows(IOException.class, () -> storage.loadFromSaveFormats(List.of("T | 0")));
        assertThrows(IOException.class, () -> storage.loadFromSaveFormats(List.of("T | 0 | ")));
        assertThrows(IOException.class, () -> storage.loadFromSaveFormats(List.of("D | 0 | task | ")));
        assertThrows(IOException.class, () -> storage.loadFromSaveFormats(List.of("D | 0 | task | invalid-date")));
        assertThrows(IOException.class, () -> storage.loadFromSaveFormats(List.of(
                "E | 0 | event | 2026-09-20 | ")));
    }

    /** Verifies that blank persisted entries are reported as invalid data. */
    @Test
    public void loadFromSaveFormats_blankEntry_throwsIOException() {
        Storage storage = new Storage();

        assertThrows(IOException.class, () -> storage.loadFromSaveFormats(List.of("   ")));
    }

    /** Verifies that missing persisted data is reported as invalid input. */
    @Test
    public void loadFromSaveFormats_missingContents_throwsIOException() {
        Storage storage = new Storage();

        assertThrows(IOException.class, () -> storage.loadFromSaveFormats(null));
    }

    /** Verifies that persisted events must have a forward date range. */
    @Test
    public void loadFromSaveFormats_invalidEventRange_throwsIOException() {
        Storage storage = new Storage();

        assertThrows(IOException.class, () -> storage.loadFromSaveFormats(List.of(
                "E | 0 | concert | 2026-09-20 | 2026-09-20")));
    }

    /** Verifies that persisted duplicate tasks are rejected. */
    @Test
    public void loadFromSaveFormats_duplicateTask_throwsIOException() {
        Storage storage = new Storage();

        assertThrows(IOException.class, () -> storage.loadFromSaveFormats(List.of(
                "T | 0 | read book", "T | 0 | read book")));
        assertThrows(IOException.class, () -> storage.loadFromSaveFormats(List.of(
                "T | 0 | read book", "T | 1 | read book")));
    }
}
