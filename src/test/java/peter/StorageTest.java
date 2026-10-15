package peter;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests for {@link Storage}, focused on the environment and malformed-data
 * cases it needs to survive without crashing or losing valid tasks: a
 * missing file or parent directory, and a data file whose content is not
 * as expected.
 */
public class StorageTest {
    @TempDir
    private Path tempDir;

    @Test
    public void load_missingFile_createsFileAndReturnsEmptyList() throws Exception {
        Path dataFile = tempDir.resolve("peter.txt");
        Storage storage = new Storage(dataFile.toString());

        List<Task> loaded = storage.load();

        assertTrue(loaded.isEmpty());
        assertTrue(Files.exists(dataFile));
    }

    @Test
    public void load_missingParentDirectory_createsDirectoryAndFile() throws Exception {
        Path dataFile = tempDir.resolve("nested/dir/peter.txt");
        Storage storage = new Storage(dataFile.toString());

        List<Task> loaded = storage.load();

        assertTrue(loaded.isEmpty());
        assertTrue(Files.exists(dataFile));
    }

    @Test
    public void load_dataFileIsActuallyADirectory_throwsPeterException() throws Exception {
        Path dataAsDirectory = tempDir.resolve("peter.txt");
        Files.createDirectory(dataAsDirectory);
        Storage storage = new Storage(dataAsDirectory.toString());

        assertThrows(PeterException.class, storage::load);
    }

    @Test
    public void load_parentPathIsARegularFileNotADirectory_throwsPeterException() throws Exception {
        // Simulates a hand-broken data directory: something exists at the path where
        // Storage expects to find (or create) the data file's parent directory, but it's
        // not a directory, so Files.createDirectories cannot proceed.
        Path blockingFile = tempDir.resolve("data");
        Files.createFile(blockingFile);
        Storage storage = new Storage(blockingFile.resolve("peter.txt").toString());

        assertThrows(PeterException.class, storage::load);
    }

    @Test
    public void save_parentPathIsARegularFileNotADirectory_doesNotThrow() throws Exception {
        // save() only prints a warning on failure rather than propagating an exception,
        // since losing the in-memory list over a save failure would be worse for the user.
        Path blockingFile = tempDir.resolve("data");
        Files.createFile(blockingFile);
        Storage storage = new Storage(blockingFile.resolve("peter.txt").toString());
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new Todo("read book"));

        assertDoesNotThrow(() -> storage.save(tasks));
    }

    @Test
    public void load_lineWithTooFewFields_skipsLineWithoutCrashing() throws Exception {
        List<Task> loaded = loadFromLines("T | 0");

        assertTrue(loaded.isEmpty());
    }

    @Test
    public void load_lineWithInvalidStatusMarker_skipsLine() throws Exception {
        List<Task> loaded = loadFromLines("T | 2 | read book");

        assertTrue(loaded.isEmpty());
    }

    @Test
    public void load_unknownTaskTypeCode_skipsLine() throws Exception {
        List<Task> loaded = loadFromLines("X | 0 | read book");

        assertTrue(loaded.isEmpty());
    }

    @Test
    public void load_nonExistentCalendarDate_skipsLine() throws Exception {
        // A hand-edited file with an impossible date (Feb 30) should be treated as
        // malformed, the same as any other structurally-invalid line.
        List<Task> loaded = loadFromLines("D | 0 | return book | 2019-02-30T1800");

        assertTrue(loaded.isEmpty());
    }

    @Test
    public void load_eventEndNotAfterStart_skipsLine() throws Exception {
        List<Task> loaded = loadFromLines("E | 0 | meeting | 2019-12-02T1600 | 2019-12-02T1400");

        assertTrue(loaded.isEmpty());
    }

    @Test
    public void load_mixOfValidAndMalformedLines_keepsOnlyValidOnes() throws Exception {
        List<Task> loaded = loadFromLines(
                "T | 0 | read book",
                "not a valid line at all",
                "D | 1 | return book | 2019-12-02T1800");

        assertEquals(2, loaded.size());
        assertEquals("read book", loaded.get(0).getDescription());
        assertEquals("return book", loaded.get(1).getDescription());
        assertTrue(loaded.get(1).toString().startsWith("[D][X]"));
    }

    @Test
    public void saveAndLoad_roundTrip_preservesValidTasks() throws Exception {
        Path dataFile = tempDir.resolve("peter.txt");
        Storage storage = new Storage(dataFile.toString());
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new Todo("read book"));

        storage.save(tasks);
        List<Task> reloaded = storage.load();

        assertEquals(1, reloaded.size());
        assertEquals("read book", reloaded.get(0).getDescription());
    }

    private List<Task> loadFromLines(String... lines) throws IOException, PeterException {
        Path dataFile = tempDir.resolve("peter.txt");
        Files.write(dataFile, List.of(lines), StandardCharsets.UTF_8);
        return new Storage(dataFile.toString()).load();
    }
}
