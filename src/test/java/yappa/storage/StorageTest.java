package yappa.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import yappa.task.Deadline;
import yappa.task.Event;
import yappa.task.TaskList;
import yappa.task.Todo;

/**
 * Tests persistence of each task type without touching the user's real data file.
 */
public class StorageTest {

    @TempDir
    Path tempDir;

    /**
     * Verifies that loading a missing file produces an empty task list.
     *
     * @throws Exception If storage access unexpectedly fails.
     */
    @Test
    public void loadTasks_missingFile_returnsEmptyTaskList() throws Exception {
        Storage storage = new Storage(tempDir.resolve("missing.txt"));

        assertEquals(0, storage.loadTasks().tasks().size());
    }

    /**
     * Verifies that saving and loading preserve every supported task type.
     *
     * @throws Exception If storage access or task construction unexpectedly fails.
     */
    @Test
    public void saveAndLoadTasks_allTaskTypes_preservesTaskData() throws Exception {
        LocalDateTime start = LocalDateTime.of(2026, 12, 2, 14, 0);
        LocalDateTime end = LocalDateTime.of(2026, 12, 2, 16, 0);
        TaskList originalTasks = new TaskList(List.of(
                new Todo("read book", true),
                new Deadline("submit report", false, end),
                new Event("project meeting", true, start, end)));
        Storage storage = new Storage(tempDir.resolve("nested/tasks.txt"));

        storage.saveTasks(originalTasks);
        TaskList loadedTasks = storage.loadTasks().tasks();

        assertEquals(3, loadedTasks.size());
        assertEquals(originalTasks.toString(), loadedTasks.toString());
    }

    /** Verifies that invalid records are skipped and reported as warnings. */
    @Test
    public void loadTasks_invalidRecord_returnsWarningAndValidTasks() throws Exception {
        Path storagePath = tempDir.resolve("tasks.txt");
        Files.writeString(storagePath,
                "T | 0 | valid task" + System.lineSeparator()
                        + "invalid record" + System.lineSeparator());
        Storage storage = new Storage(storagePath);

        LoadResult result = storage.loadTasks();

        assertEquals(1, result.tasks().size());
        assertEquals(1, result.warnings().size());
    }
}
