package goat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests task persistence using isolated temporary directories.
 */
public class StorageTest {
    @TempDir
    private Path tempDirectory;

    @Test
    public void load_missingFile_returnsEmptyList() throws Exception {
        Storage storage = new Storage(tempDirectory.resolve("missing.txt"));

        assertTrue(storage.load().isEmpty());
    }

    @Test
    public void saveAndLoad_mixedTasks_preservesTaskData() throws Exception {
        Path dataFile = tempDirectory.resolve("nested").resolve("goat.txt");
        Storage storage = new Storage(dataFile);
        Task completedTodo = new Todo("read A|B \\ notes");
        completedTodo.markAsDone();
        List<Task> originalTasks = List.of(
                completedTodo,
                new Deadline("submit report", LocalDate.of(2026, 10, 15)),
                new Event("camp", LocalDate.of(2026, 11, 1),
                        LocalDate.of(2026, 11, 3)));

        storage.save(originalTasks);
        List<Task> loadedTasks = storage.load();

        assertEquals(3, loadedTasks.size());
        for (int i = 0; i < originalTasks.size(); i++) {
            assertEquals(originalTasks.get(i).toFileString(), loadedTasks.get(i).toFileString());
        }
    }

    @Test
    public void load_malformedStatus_reportsLineNumber() throws Exception {
        Path dataFile = tempDirectory.resolve("goat.txt");
        Files.writeString(dataFile, "T | 0 | valid task\nT | maybe | broken task\n");
        Storage storage = new Storage(dataFile);

        GoatException exception = org.junit.jupiter.api.Assertions.assertThrows(
                GoatException.class, storage::load);

        assertEquals("Invalid data on line 2: completion status must be 0 or 1.",
                exception.getMessage());
    }

    @Test
    public void load_eventWithReverseRange_reportsLineNumber() throws Exception {
        Path dataFile = tempDirectory.resolve("goat.txt");
        Files.writeString(dataFile, "E | 0 | camp | 2026-11-03 | 2026-11-01\n");
        Storage storage = new Storage(dataFile);

        GoatException exception = org.junit.jupiter.api.Assertions.assertThrows(
                GoatException.class, storage::load);

        assertEquals("Invalid data on line 1: The saved event must start before it ends.",
                exception.getMessage());
    }
}
