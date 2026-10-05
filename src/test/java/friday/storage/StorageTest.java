package friday.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import friday.FridayException;
import friday.task.Deadline;
import friday.task.RepeatFrequency;
import friday.task.Task;
import friday.task.TaskList;
import friday.task.Todo;

/**
 * Tests loading and saving task data.
 */
class StorageTest {
    private static final Path PARENTLESS_FILE_PATH = Path.of("storage-test.txt");

    @TempDir
    private Path tempDir;

    @AfterEach
    void deleteParentlessTestFile() throws IOException {
        Files.deleteIfExists(PARENTLESS_FILE_PATH);
    }

    @Test
    void load_missingFile_returnsEmptyList() throws FridayException {
        Storage storage = new Storage(tempDir.resolve("missing.txt"));

        assertTrue(storage.load().isEmpty());
    }

    @Test
    void saveThenLoad_multipleTaskStates_restoresAllData() throws FridayException {
        Storage storage = new Storage(tempDir.resolve("data/duke.txt"));
        TaskList originalTasks = new TaskList();
        Todo todo = new Todo("read book");
        todo.markAsDone();
        Deadline deadline = new Deadline("submit report", "2/12/2025 1800");
        deadline.setRepeatFrequency(RepeatFrequency.WEEKLY);
        originalTasks.add(todo);
        originalTasks.add(deadline);

        storage.save(originalTasks);
        ArrayList<Task> loadedTasks = storage.load();

        assertEquals(2, loadedTasks.size());
        assertEquals(todo.toFileString(), loadedTasks.get(0).toFileString());
        assertEquals(deadline.toFileString(), loadedTasks.get(1).toFileString());
    }

    @Test
    void load_invalidSecondLine_reportsLineNumber() throws IOException {
        Path filePath = tempDir.resolve("duke.txt");
        Files.writeString(filePath, "T | 0 | valid task\nT | invalid | broken task\n", StandardCharsets.UTF_8);
        Storage storage = new Storage(filePath);

        FridayException exception = assertThrows(FridayException.class, storage::load);

        assertTrue(exception.getMessage().contains("line 2"));
    }

    @Test
    void save_fileWithoutParent_writesFile() throws FridayException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        new Storage(PARENTLESS_FILE_PATH).save(tasks);

        assertTrue(Files.exists(PARENTLESS_FILE_PATH));
    }
}
