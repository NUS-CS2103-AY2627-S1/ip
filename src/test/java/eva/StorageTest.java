package eva;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import eva.task.Deadline;
import eva.task.Event;
import eva.task.Todo;

public class StorageTest {
    @TempDir
    private Path tempDirectory;

    @Test
    void load_missingFile_returnsEmptyList() throws EvaException {
        Storage storage = new Storage(
                tempDirectory.resolve("missing/eva.txt").toString());

        assertEquals(0, storage.load().size());
    }

    @Test
    void saveAndLoad_preservesTaskTypesAndStatus() throws EvaException {
        Path dataFile = tempDirectory.resolve("data/eva.txt");
        Storage storage = new Storage(dataFile.toString());
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Deadline("submit", "2026-09-30"));
        tasks.add(new Event("meeting", "09:00", "10:00"));
        tasks.mark(2);

        storage.save(tasks);

        assertTrue(Files.exists(dataFile));
        assertEquals(3, storage.load().size());
        assertEquals("T | 0 | read book",
                storage.load().get(0).toFileString());
        assertEquals("D | 1 | submit | 2026-09-30",
                storage.load().get(1).toFileString());
        assertEquals("E | 0 | meeting | 09:00 | 10:00",
                storage.load().get(2).toFileString());
    }

    @Test
    void load_blankLines_ignoresBlankLines() throws IOException, EvaException {
        Path dataFile = tempDirectory.resolve("eva.txt");
        Files.writeString(dataFile, "\nT | 0 | read book\n\n");
        Storage storage = new Storage(dataFile.toString());

        assertEquals(1, storage.load().size());
    }

    @Test
    void load_invalidTaskType_throwsEvaException() throws IOException {
        Path dataFile = tempDirectory.resolve("eva.txt");
        Files.writeString(dataFile, "X | 0 | invalid");
        Storage storage = new Storage(dataFile.toString());

        EvaException error = assertThrows(
                EvaException.class, () -> storage.load());

        assertTrue(error.getMessage().contains("Unknown task type"));
    }

    @Test
    void load_invalidStatus_throwsEvaException() throws IOException {
        Path dataFile = tempDirectory.resolve("eva.txt");
        Files.writeString(dataFile, "T | 2 | read book");
        Storage storage = new Storage(dataFile.toString());

        EvaException error = assertThrows(
                EvaException.class, () -> storage.load());

        assertTrue(error.getMessage().contains("Invalid task status"));
    }

    @Test
    void save_directoryInsteadOfFile_throwsEvaException() {
        Storage storage = new Storage(tempDirectory.toString());

        assertThrows(
                EvaException.class, () -> storage.save(new TaskList()));
    }
}
