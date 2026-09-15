package tianyi.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import tianyi.task.Deadline;
import tianyi.task.Event;
import tianyi.task.Task;
import tianyi.task.TaskTime;
import tianyi.task.ToDo;

/**
 * Tests loading and saving tasks using isolated temporary files.
 */
public class StorageTest {
    @TempDir
    private Path tempDir;

    @Test
    public void load_fileDoesNotExist_returnsEmptyList()
            throws StorageException {
        Storage storage = new Storage(tempDir.resolve("missing.txt").toString());

        List<Task> tasks = storage.load();

        assertTrue(tasks.isEmpty());
    }

    @Test
    public void load_emptyFile_returnsEmptyList()
            throws IOException, StorageException {
        Path dataFile = Files.createFile(tempDir.resolve("empty.txt"));
        Storage storage = new Storage(dataFile.toString());

        List<Task> tasks = storage.load();

        assertTrue(tasks.isEmpty());
    }

    @Test
    public void load_validMixedTasks_restoresTypesOrderAndStatus()
            throws IOException, StorageException {
        Path dataFile = tempDir.resolve("tasks.txt");
        Files.writeString(dataFile, "T | 0 | read book" + System.lineSeparator()
                + "D | 1 | submit report | 2-12-2019 18:00" + System.lineSeparator()
                + "E | 0 | workshop | 2-12-2019 | 3-12-2019 17:00"
                + System.lineSeparator());
        Storage storage = new Storage(dataFile.toString());

        List<Task> tasks = storage.load();

        assertEquals(3, tasks.size());
        assertInstanceOf(ToDo.class, tasks.get(0));
        assertEquals("T | 0 | read book", tasks.get(0).getData());
        assertInstanceOf(Deadline.class, tasks.get(1));
        assertEquals("D | 1 | submit report | 2-12-2019 18:00", tasks.get(1).getData());
        assertInstanceOf(Event.class, tasks.get(2));
        assertEquals("E | 0 | workshop | 2-12-2019 | 3-12-2019 17:00",
                tasks.get(2).getData());
    }

    @Test
    public void load_fileContainingInvalidRecord_exceptionThrown()
            throws IOException {
        Path dataFile = tempDir.resolve("invalid.txt");
        Files.writeString(dataFile, "T | 0 | valid" + System.lineSeparator()
                + "X | 0 | invalid" + System.lineSeparator());
        Storage storage = new Storage(dataFile.toString());

        StorageException exception = assertThrows(StorageException.class, storage::load);

        assertEquals("Unknown task type: X | 0 | invalid", exception.getMessage());
    }

    @Test
    public void load_sourcePathIsDirectory_exceptionThrown()
            throws IOException {
        Path directory = Files.createDirectory(tempDir.resolve("directory"));
        Storage storage = new Storage(directory.toString());

        StorageException exception = assertThrows(StorageException.class, storage::load);

        assertEquals("Unable to load tasks from " + directory + ".", exception.getMessage());
    }

    @Test
    public void save_mixedTasks_writesExpectedRecords()
            throws IOException, StorageException {
        Path dataFile = tempDir.resolve("tasks.txt");
        Storage storage = new Storage(dataFile.toString());
        ToDo todo = new ToDo("read book");
        Deadline deadline = new Deadline("submit report", new TaskTime("2-12-2019 18:00"));
        deadline.markAsDone();
        Event event = new Event("workshop", new TaskTime("2-12-2019"),
                new TaskTime("3-12-2019 17:00"));

        storage.save(List.of(todo, deadline, event));

        assertEquals("T | 0 | read book" + System.lineSeparator()
                + "D | 1 | submit report | 2-12-2019 18:00" + System.lineSeparator()
                + "E | 0 | workshop | 2-12-2019 | 3-12-2019 17:00"
                + System.lineSeparator(), Files.readString(dataFile));
    }

    @Test
    public void save_parentDirectoryDoesNotExist_createsDirectoryAndFile()
            throws StorageException {
        Path dataFile = tempDir.resolve("nested/data/tasks.txt");
        Storage storage = new Storage(dataFile.toString());

        storage.save(List.of(new ToDo("read book")));

        assertTrue(Files.isRegularFile(dataFile));
    }

    @Test
    public void save_parentPathIsFile_exceptionThrown()
            throws IOException {
        Path parentFile = Files.createFile(tempDir.resolve("parent-file"));
        Path dataFile = parentFile.resolve("nested/tasks.txt");
        Storage storage = new Storage(dataFile.toString());

        StorageException exception = assertThrows(
                StorageException.class, () -> storage.save(List.of(new ToDo("read book"))));

        assertEquals("Unable to create the data folder.", exception.getMessage());
        assertFalse(Files.exists(dataFile));
    }

    @Test
    public void save_emptyList_overwritesExistingContent()
            throws IOException, StorageException {
        Path dataFile = tempDir.resolve("tasks.txt");
        Files.writeString(dataFile, "old content");
        Storage storage = new Storage(dataFile.toString());

        storage.save(List.of());

        assertEquals("", Files.readString(dataFile));
    }

    @Test
    public void saveThenLoad_tasksRoundTripWithoutDataLoss()
            throws StorageException {
        Path dataFile = tempDir.resolve("round-trip.txt");
        Storage storage = new Storage(dataFile.toString());
        List<Task> originalTasks = List.of(
                new ToDo("read book"),
                new Deadline("submit report", new TaskTime("2-12-2019 18:00")),
                new Event("workshop", new TaskTime("2-12-2019"),
                        new TaskTime("3-12-2019 17:00")));

        storage.save(originalTasks);
        List<Task> loadedTasks = storage.load();

        assertEquals(originalTasks.size(), loadedTasks.size());
        for (int i = 0; i < originalTasks.size(); i += 1) {
            assertEquals(originalTasks.get(i).getData(), loadedTasks.get(i).getData());
        }
    }

    @Test
    public void save_targetPathIsDirectory_exceptionThrown()
            throws IOException {
        Path directory = Files.createDirectory(tempDir.resolve("directory"));
        Storage storage = new Storage(directory.toString());

        StorageException exception = assertThrows(
                StorageException.class, () -> storage.save(List.of(new ToDo("read book"))));

        assertEquals("Unable to save tasks to " + directory + ".", exception.getMessage());
        assertFalse(Files.isRegularFile(directory));
    }
}
