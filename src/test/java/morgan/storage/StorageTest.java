package morgan.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import morgan.task.Deadline;
import morgan.task.Event;
import morgan.task.Task;
import morgan.task.ToDo;

public class StorageTest {

    private static final String TEST_FILE_PATH = "./data/test_morgan.txt";

    @TempDir
    private Path tempDirectory;

    private Storage storage;

    @BeforeEach
    public void setUp() {
        storage = new Storage(TEST_FILE_PATH);
    }

    @AfterEach
    public void tearDown() {
        File file = new File(TEST_FILE_PATH);
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    public void saveAndLoad_todoTask_success() {
        List<Task> originalTasks = new ArrayList<>();
        ToDo todo = new ToDo("read book");
        originalTasks.add(todo);

        storage.save(originalTasks);
        List<Task> loadedTasks = storage.load();

        assertEquals(1, loadedTasks.size());
        assertEquals("read book", loadedTasks.get(0).getName());
        assertEquals(false, loadedTasks.get(0).isDone());
    }

    @Test
    public void saveAndLoad_deadlineTask_success() {
        File file = new File(TEST_FILE_PATH);
        if (file.exists()) {
            file.delete();
        }

        List<Task> originalTasks = new ArrayList<>();
        Deadline deadline = new Deadline("submit assignment", "2026-08-31");
        deadline.mark();
        originalTasks.add(deadline);

        storage.save(originalTasks);
        List<Task> loadedTasks = storage.load();

        assertEquals(1, loadedTasks.size());
        assertEquals("submit assignment", loadedTasks.get(0).getName());
        assertEquals(true, loadedTasks.get(0).isDone());
    }

    @Test
    public void saveAndLoad_eventTask_success() {
        List<Task> originalTasks = new ArrayList<>();
        Event event = new Event("team meeting", "2026-08-31 1400", "2026-08-31 1600");
        originalTasks.add(event);

        storage.save(originalTasks);
        List<Task> loadedTasks = storage.load();

        assertEquals(1, loadedTasks.size());
        assertEquals("team meeting", loadedTasks.get(0).getName());
        assertEquals(false, loadedTasks.get(0).isDone());
    }

    @Test
    public void load_emptyFile_returnsEmptyList() {
        List<Task> tasks = storage.load();

        assertTrue(tasks.isEmpty());
        File file = new File(TEST_FILE_PATH);
        assertTrue(file.exists());
    }

    @Test
    public void save_parentDirectoryMissing_createsDirectoryAndFile() {
        Path nestedFile = tempDirectory.resolve("data").resolve("tasks.txt");
        Storage nestedStorage = new Storage(nestedFile.toString());

        nestedStorage.save(List.of(new ToDo("read book")));

        assertTrue(nestedFile.toFile().exists());
        assertEquals(1, nestedStorage.load().size());
    }
}
