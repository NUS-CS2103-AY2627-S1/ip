package clearblue.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import clearblue.ClearblueException;
import clearblue.task.Task;
import clearblue.task.Todo;

/**
 * Tests for {@link Storage}: round-tripping tasks through save/load, and
 * how load() reports lines it could not parse.
 */
public class StorageTest {
    @TempDir
    private Path tempDir;

    private Storage newStorage() {
        return new Storage(tempDir.resolve("test-data.txt").toString());
    }

    @Test
    public void load_missingFile_returnsEmptyResultWithNoSkippedLines() throws ClearblueException {
        Storage.LoadResult result = newStorage().load();

        assertTrue(result.tasks().isEmpty());
        assertEquals(0, result.skippedLineCount());
    }

    @Test
    public void saveThenLoad_roundTripsTasksWithNoSkippedLines() throws ClearblueException {
        Storage storage = newStorage();
        storage.save(List.of(new Todo("read book")));

        Storage.LoadResult result = storage.load();

        assertEquals(1, result.tasks().size());
        assertEquals("read book", result.tasks().get(0).getDescription());
        assertEquals(0, result.skippedLineCount());
    }

    @Test
    public void load_corruptedLine_isSkippedAndCounted() throws ClearblueException, IOException {
        Path dataFile = tempDir.resolve("test-data.txt");
        Files.write(dataFile, List.of("T | 1 | read book", "this line is not valid"));

        Storage.LoadResult result = new Storage(dataFile.toString()).load();

        assertEquals(1, result.tasks().size());
        Task task = result.tasks().get(0);
        assertEquals("read book", task.getDescription());
        assertTrue(task.isDone());
        assertEquals(1, result.skippedLineCount());
    }

    @Test
    public void containsFieldSeparator_textWithoutSeparator_returnsFalse() {
        assertFalse(Storage.containsFieldSeparator("read book"));
    }

    @Test
    public void containsFieldSeparator_textWithSeparator_returnsTrue() {
        assertTrue(Storage.containsFieldSeparator("read book | urgent"));
    }
}
