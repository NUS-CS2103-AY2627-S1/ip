package zinc.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Tests saving, loading, and recovery of persisted tasks. */
public class TaskStorageTest {
    private static final Path STORAGE_FILE = Path.of("data", "zincTasks.txt");

    @BeforeEach
    @AfterEach
    public void clearStorage() throws Exception {
        Files.deleteIfExists(STORAGE_FILE);
    }

    @Test
    public void loadTasks_fileDoesNotExist_returnsEmptyList() {
        TaskStorage storage = new TaskStorage();

        assertTrue(storage.loadTasks().isEmpty());
        assertFalse(storage.wasDataCorrupted());
    }

    @Test
    public void saveTasks_thenLoadTasks_restoresAllTasks() {
        Task[] tasks = {
            new Todo("Buy bread"),
            new Deadline("Submit report", LocalDateTime.of(2026, 8, 27, 18, 30)),
            new Event("Meeting", LocalDateTime.of(2026, 8, 27, 10, 0),
                    LocalDateTime.of(2026, 8, 27, 11, 0))
        };
        tasks[1].markAsCompleted();
        TaskStorage storage = new TaskStorage();

        storage.saveTasks(List.of(tasks));
        List<Task> loadedTasks = storage.loadTasks();

        assertEquals(3, loadedTasks.size());
        assertTrue(loadedTasks.get(0) instanceof Todo);
        assertEquals("Buy bread", loadedTasks.get(0).getDescription());
        assertTrue(loadedTasks.get(1) instanceof Deadline);
        assertTrue(loadedTasks.get(1).isCompleted());
        Deadline loadedDeadline = (Deadline) loadedTasks.get(1);
        assertEquals(LocalDateTime.of(2026, 8, 27, 18, 30), loadedDeadline.getDeadline());
        assertTrue(loadedTasks.get(2) instanceof Event);
        assertFalse(storage.wasDataCorrupted());
    }

    @Test
    public void saveTasks_withZeroCount_createsEmptyFile() throws Exception {
        new TaskStorage().saveTasks(List.of());

        assertTrue(Files.exists(STORAGE_FILE));
        assertTrue(Files.readAllLines(STORAGE_FILE).isEmpty());
    }

    @Test
    public void loadTasks_withMalformedFile_returnsEmptyListAndClearsFile() throws Exception {
        Files.createDirectories(STORAGE_FILE.getParent());
        Files.writeString(STORAGE_FILE, "X | 0 | Unknown task");

        TaskStorage storage = new TaskStorage();

        List<Task> loadedTasks = storage.loadTasks();

        assertTrue(loadedTasks.isEmpty());
        assertTrue(Files.readAllLines(STORAGE_FILE).isEmpty());
        assertTrue(storage.wasDataCorrupted());
    }

    @Test
    public void loadTasks_withIncompleteTask_returnsEmptyListAndClearsFile() throws Exception {
        Files.createDirectories(STORAGE_FILE.getParent());
        Files.writeString(STORAGE_FILE, "D | 0 | Missing deadline");

        List<Task> loadedTasks = new TaskStorage().loadTasks();

        assertFalse(Files.readAllLines(STORAGE_FILE).iterator().hasNext());
        assertTrue(loadedTasks.isEmpty());
    }
}
