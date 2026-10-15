package magnus.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import magnus.exception.StorageException;
import magnus.task.DeadlineTask;
import magnus.task.EventTask;
import magnus.task.Task;
import magnus.task.ToDoTask;

/**
 * Tests loading and saving tasks through persistent storage.
 */
public class StorageTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void constructor_nullPath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Storage(null));
    }

    @Test
    public void loadTasks_missingDataFile_returnsEmptyList() throws StorageException {
        Storage storage = new Storage(this.temporaryDirectory.resolve("missing.txt"));

        List<Task> tasks = storage.loadTasks();

        assertTrue(tasks.isEmpty());
    }

    @Test
    public void saveTasks_multipleTaskTypes_loadsEquivalentTasks() throws StorageException {
        Path dataFile = this.temporaryDirectory.resolve("data").resolve("magnus.txt");
        Storage storage = new Storage(dataFile);
        ToDoTask todo = new ToDoTask("buy milk, eggs");
        DeadlineTask deadline = new DeadlineTask(
                "submit report", LocalDateTime.of(2026, 9, 2, 15, 0));
        EventTask event = new EventTask(
                "meeting", LocalDateTime.of(2026, 9, 2, 16, 0),
                LocalDateTime.of(2026, 9, 2, 17, 0));
        deadline.markAsDone();
        List<Task> tasks = List.of(todo, deadline, event);

        storage.saveTasks(tasks);
        List<Task> loadedTasks = storage.loadTasks();

        assertEquals(tasks.stream().map(Task::serialize).toList(),
                loadedTasks.stream().map(Task::serialize).toList());
    }

    @Test
    public void saveTasks_emptyList_createsEmptyDataFile() throws StorageException, IOException {
        Path dataFile = this.temporaryDirectory.resolve("magnus.txt");
        Storage storage = new Storage(dataFile);

        storage.saveTasks(List.of());

        assertTrue(Files.exists(dataFile));
        assertEquals("", Files.readString(dataFile, StandardCharsets.UTF_8));
    }

    @Test
    public void saveTasks_replacesExistingContentsWithoutLeavingTemporaryFile()
            throws StorageException, IOException {
        Path dataFile = this.temporaryDirectory.resolve("magnus.txt");
        Storage storage = new Storage(dataFile);
        storage.saveTasks(List.of(new ToDoTask("first"), new ToDoTask("second")));

        storage.saveTasks(List.of(new ToDoTask("replacement")));

        assertEquals("T,0,replacement", Files.readString(dataFile, StandardCharsets.UTF_8));
        try (var files = Files.list(this.temporaryDirectory)) {
            assertFalse(files.anyMatch(path -> path.getFileName().toString().endsWith(".tmp")));
        }
    }

    @Test
    public void saveTasks_nullCollectionOrTask_reportsSerializationError() {
        Storage storage = new Storage(this.temporaryDirectory.resolve("magnus.txt"));

        StorageException nullCollectionException = assertThrows(
                StorageException.class, () -> storage.saveTasks(null));
        StorageException nullTaskException = assertThrows(
                StorageException.class, () -> storage.saveTasks(java.util.Arrays.asList((Task) null)));

        assertTrue(nullCollectionException.getMessage().contains("could not prepare the tasks"));
        assertTrue(nullTaskException.getMessage().contains("could not prepare the tasks"));
        assertTrue(nullCollectionException.getCause() instanceof NullPointerException);
        assertTrue(nullTaskException.getCause() instanceof NullPointerException);
    }

    @Test
    public void saveTasks_parentPathIsFile_reportsSaveError() throws IOException {
        Path parentFile = this.temporaryDirectory.resolve("not-a-directory");
        Files.writeString(parentFile, "occupied", StandardCharsets.UTF_8);
        Storage storage = new Storage(parentFile.resolve("magnus.txt"));

        StorageException exception = assertThrows(
                StorageException.class, () -> storage.saveTasks(List.of(new ToDoTask("read book"))));

        assertTrue(exception.getMessage().contains("I could not save your tasks"));
        assertTrue(exception.getCause() instanceof IOException);
    }

    @Test
    public void loadTasks_malformedRecord_reportsLineNumber() throws IOException {
        Path dataFile = this.temporaryDirectory.resolve("magnus.txt");
        Files.writeString(dataFile, "T,0,read book\ninvalid", StandardCharsets.UTF_8);
        Storage storage = new Storage(dataFile);

        StorageException exception = assertThrows(
                StorageException.class, storage::loadTasks);

        assertEquals("\tThe score sheet contains an invalid position - the data file is corrupted at line 2: "
                        + "missing task type or status",
                exception.getMessage());
    }

    @Test
    public void loadTasks_blankLines_ignoresBlankRecordsAndRetainsPhysicalLineNumbers()
            throws IOException {
        Path dataFile = this.temporaryDirectory.resolve("magnus.txt");
        Files.writeString(dataFile, "\nT,0,read book\n\ninvalid", StandardCharsets.UTF_8);
        Storage storage = new Storage(dataFile);

        StorageException exception = assertThrows(StorageException.class, storage::loadTasks);

        assertTrue(exception.getMessage().contains("corrupted at line 4"));
    }

    @Test
    public void loadTasks_duplicateRecords_reportsDuplicateLine() throws IOException {
        Path dataFile = this.temporaryDirectory.resolve("magnus.txt");
        Files.writeString(dataFile, "T,0,read book\nT,1,read book", StandardCharsets.UTF_8);
        Storage storage = new Storage(dataFile);

        StorageException exception = assertThrows(StorageException.class, storage::loadTasks);

        assertEquals("\tThe score sheet contains an invalid position - duplicate task at line 2.",
                exception.getMessage());
    }

    @Test
    public void loadTasks_dataPathIsDirectory_reportsReadError() throws IOException {
        Path dataPath = this.temporaryDirectory.resolve("magnus.txt");
        Files.createDirectory(dataPath);
        Storage storage = new Storage(dataPath);

        StorageException exception = assertThrows(StorageException.class, storage::loadTasks);

        assertTrue(exception.getMessage().contains("I could not read the data file"));
    }
}
