package shrekanddonkey.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import shrekanddonkey.task.Deadline;
import shrekanddonkey.task.Event;
import shrekanddonkey.task.Task;
import shrekanddonkey.task.Todo;

/**
 * Tests loading and saving task data in {@link Storage}.
 */
public class StorageTest {

    @Test
    public void load_missingFile_createsEmptyFileAndReturnsNoTasks(@TempDir Path tempDir) throws IOException {
        Path filePath = tempDir.resolve("data").resolve("happyFile.txt");

        List<Task> tasks = new Storage(filePath.toString()).load();

        assertTrue(Files.exists(filePath));
        assertTrue(tasks.isEmpty());
        assertTrue(Files.readString(filePath).isEmpty());
    }

    @Test
    public void saveAndLoad_allTaskTypes_preservesTaskDetails(@TempDir Path tempDir) throws IOException {
        Path filePath = tempDir.resolve("tasks.txt");
        Storage storage = new Storage(filePath.toString());
        Todo todo = new Todo("buy onions");
        todo.markAsDone();
        Deadline deadline = new Deadline("submit report", LocalDateTime.of(2026, 10, 15, 18, 0));
        Event event = new Event("swamp meeting", LocalDateTime.of(2026, 10, 20, 14, 0),
                LocalDateTime.of(2026, 10, 20, 16, 0));

        storage.save(List.of(todo, deadline, event));
        List<Task> loadedTasks = storage.load();

        assertEquals(List.of("[T][X] buy onions", "[D][ ] submit report (by: Oct 15 2026, 6:00 PM)",
                "[E][ ] swamp meeting (from: Oct 20 2026, 2:00 PM to: Oct 20 2026, 4:00 PM)"),
                loadedTasks.stream().map(Task::toString).toList());
        assertEquals("T | 1 | buy onions", Files.readAllLines(filePath).get(0));
    }

    @Test
    public void load_legacyDisplayFormat_loadsExistingTasks(@TempDir Path tempDir) throws IOException {
        Path filePath = tempDir.resolve("happyFile.txt");
        Files.writeString(filePath, "[D][ ] submit report (by: Oct 15 2026, 6:00 PM)\n"
                + "[E][ ] swamp meeting (from: Oct 20 2026, 2:00 PM to: Oct 20 2026, 4:00 PM)\n"
                + "[T][X] buy onions\n");

        List<Task> loadedTasks = new Storage(filePath.toString()).load();

        assertEquals(3, loadedTasks.size());
        assertEquals("[D][ ] submit report (by: Oct 15 2026, 6:00 PM)", loadedTasks.get(0).toString());
        assertEquals("[E][ ] swamp meeting (from: Oct 20 2026, 2:00 PM to: Oct 20 2026, 4:00 PM)",
                loadedTasks.get(1).toString());
        assertEquals("[T][X] buy onions", loadedTasks.get(2).toString());
    }

    @Test
    public void load_invalidData_throwsIOException(@TempDir Path tempDir) throws IOException {
        Path filePath = tempDir.resolve("happyFile.txt");
        Files.writeString(filePath, "Not a stored task");

        IOException exception = org.junit.jupiter.api.Assertions.assertThrows(IOException.class,
                () -> new Storage(filePath.toString()).load());

        assertFalse(exception.getMessage().isEmpty());
    }
}
