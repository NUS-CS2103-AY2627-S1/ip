package charliek.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import charliek.exception.TaskStorageException;
import charliek.model.Deadline;
import charliek.model.Event;
import charliek.model.Task;
import charliek.model.ToDo;
import charliek.parser.DateTimeParser;

/**
 * Tests CSV persistence, recovery of valid records, and storage failures.
 */
class StorageTest {
    @TempDir
    Path tempDirectory;

    /**
     * Verifies that a missing task file represents an empty task list.
     */
    @Test
    void load_missingFile_returnsEmptyList() throws Exception {
        Storage storage = new Storage(tempDirectory.resolve("nested/tasks.csv"));

        assertTrue(storage.load().isEmpty());
    }

    /**
     * Verifies that a storage service requires a path naming a file.
     */
    @Test
    void storage_nullOrDirectoryPath_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Storage(null));
        assertThrows(IllegalArgumentException.class, () -> new Storage(tempDirectory.getRoot()));
    }

    /**
     * Verifies that missing storage is initialized with and persists starter tasks.
     */
    @Test
    void loadOrCreate_missingFile_persistsDefaultTasks() throws Exception {
        Storage storage = new Storage(tempDirectory.resolve("nested/tasks.csv"));
        List<Task> defaults = List.of(
                new ToDo("sample to-do"),
                new Deadline("sample deadline", LocalDate.of(2099, 1, 10)));

        List<Task> loaded = storage.loadOrCreate(defaults);

        assertEquals(2, loaded.size());
        assertEquals(defaults.get(0).toString(), loaded.get(0).toString());
        assertEquals(defaults.get(1).toString(), loaded.get(1).toString());
        assertEquals(loaded.stream().map(Task::toString).toList(),
                storage.load().stream().map(Task::toString).toList());
    }

    /**
     * Verifies that an existing empty file is not replaced with starter tasks.
     */
    @Test
    void loadOrCreate_existingEmptyFile_preservesEmptyList() throws Exception {
        Path taskFile = tempDirectory.resolve("tasks.csv");
        Files.createFile(taskFile);
        Storage storage = new Storage(taskFile);

        List<Task> loaded = storage.loadOrCreate(List.of(new ToDo("must not be inserted")));

        assertTrue(loaded.isEmpty());
        assertTrue(storage.load().isEmpty());
    }

    /**
     * Verifies that null starter data is rejected before touching the file system.
     */
    @Test
    void loadOrCreate_nullDefaults_throwsIllegalArgumentException() {
        Storage storage = new Storage(tempDirectory.resolve("tasks.csv"));

        assertThrows(IllegalArgumentException.class, () -> storage.loadOrCreate(null));
    }

    /**
     * Verifies round-trip persistence for all task types and completion states.
     */
    @Test
    void saveAndLoad_allTaskTypes_preservesFieldsAndStatus() throws Exception {
        Storage storage = new Storage(tempDirectory.resolve("nested/tasks.csv"));
        ToDo todo = new ToDo("buy, \"milk\"");
        todo.markAsDone();
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 12, 2));
        Event event = new Event("project meeting",
                DateTimeParser.ParsedDateTime.ofDateTime(LocalDateTime.of(2019, 12, 2, 14, 0)),
                DateTimeParser.ParsedDateTime.ofDateTime(LocalDateTime.of(2019, 12, 2, 15, 0)));

        storage.save(List.of(todo, deadline, event));
        List<Task> loaded = storage.load();

        assertEquals(3, loaded.size());
        assertEquals(todo.toString(), loaded.get(0).toString());
        assertTrue(loaded.get(0).isDone());
        assertInstanceOf(Deadline.class, loaded.get(1));
        assertFalse(loaded.get(1).isDone());
        assertEquals(deadline.getStorageFields(), loaded.get(1).getStorageFields());
        assertInstanceOf(Event.class, loaded.get(2));
        assertEquals(event.getStorageFields(), loaded.get(2).getStorageFields());
    }

    /**
     * Verifies that saving creates missing parent directories and overwrites the file.
     */
    @Test
    void save_missingParentAndExistingFile_createsAndReplacesFile() throws Exception {
        Path taskFile = tempDirectory.resolve("new/path/tasks.csv");
        Storage storage = new Storage(taskFile);

        storage.save(List.of(new ToDo("first")));
        storage.save(List.of(new ToDo("second")));

        assertTrue(Files.isRegularFile(taskFile));
        assertEquals("second", storage.load().get(0).getStorageFields().get(0));
    }

    /**
     * Verifies that saving an empty list creates a readable empty task file.
     */
    @Test
    void save_emptyList_createsEmptyFile() throws Exception {
        Path taskFile = tempDirectory.resolve("tasks.csv");
        Storage storage = new Storage(taskFile);

        storage.save(List.of());

        assertTrue(Files.isRegularFile(taskFile));
        assertTrue(Files.readAllLines(taskFile).isEmpty());
        assertTrue(storage.load().isEmpty());
    }

    /**
     * Verifies that malformed rows are ignored while valid rows remain loadable.
     */
    @Test
    void load_malformedAndValidRows_returnsOnlyValidTasks() throws Exception {
        Path taskFile = tempDirectory.resolve("tasks.csv");
        Files.writeString(taskFile, String.join(System.lineSeparator(),
                "not a CSV record",
                "T,1,valid saved task",
                "D,0,invalid deadline,not-a-date",
                "E,0,missing end,2019-12-02T14:00:00",
                "T,0,\"valid, quoted task\"",
                "T,0,\"unclosed quote",
                ""));

        List<Task> loaded = new Storage(taskFile).load();

        assertEquals(2, loaded.size());
        assertEquals("[T][X] valid saved task", loaded.get(0).toString());
        assertEquals("[T][ ] valid, quoted task", loaded.get(1).toString());
    }

    /**
     * Verifies that unsupported types, statuses, and field counts are ignored.
     */
    @Test
    void load_invalidRecordMetadata_returnsOnlySupportedRecords() throws Exception {
        Path taskFile = tempDirectory.resolve("tasks.csv");
        Files.writeString(taskFile, String.join(System.lineSeparator(),
                "X,0,unknown type",
                "T,2,invalid status",
                "T,0,too,many,fields",
                "D,0,missing deadline",
                "E,0,missing end,2019-12-02T14:00:00",
                "T,0,valid task",
                ""));

        List<Task> loaded = new Storage(taskFile).load();

        assertEquals(1, loaded.size());
        assertEquals("[T][ ] valid task", loaded.get(0).toString());
    }

    /**
     * Verifies duplicate persisted records do not create duplicate tasks in memory.
     */
    @Test
    void load_duplicateRecords_keepsOneTask() throws Exception {
        Path taskFile = tempDirectory.resolve("tasks.csv");
        Files.writeString(taskFile, String.join(System.lineSeparator(),
                "T,0,duplicate task",
                "T,1,duplicate task",
                "D,0,duplicate task,2019-12-02"));

        List<Task> loaded = new Storage(taskFile).load();

        assertEquals(2, loaded.size());
        assertFalse(loaded.get(0).isDone());
        assertInstanceOf(Deadline.class, loaded.get(1));
    }

    /**
     * Verifies CSV escaped quotes and whitespace after a quoted field are parsed safely.
     */
    @Test
    void load_quotedFields_restoresEscapedContent() throws Exception {
        Path taskFile = tempDirectory.resolve("tasks.csv");
        Files.writeString(taskFile, String.join(System.lineSeparator(),
                "T,0,\"quoted \"\"task\"\"\"",
                "T,0,\"trailing quote\"   ",
                "T,0,unquoted\"quote",
                "T,0,\"unclosed"));

        List<Task> loaded = new Storage(taskFile).load();

        assertEquals(2, loaded.size());
        assertEquals("quoted \"task\"", loaded.get(0).getDescription());
        assertEquals("trailing quote   ", loaded.get(1).getDescription());
    }

    /**
     * Verifies that invalid file paths are reported as storage exceptions.
     */
    @Test
    void load_directoryPath_throwsTaskStorageException() throws IOException {
        Path taskPath = tempDirectory.resolve("tasks.csv");
        Files.createDirectory(taskPath);

        TaskStorageException exception = assertThrows(TaskStorageException.class, () -> new Storage(
                taskPath).load());

        assertEquals("I couldn't load saved tasks because the task file path is not a regular file.",
                exception.getMessage());
    }

    /**
     * Verifies that save rejects null lists and reports null task entries.
     */
    @Test
    void save_nullListOrTask_throwsExpectedException() {
        Storage storage = new Storage(tempDirectory.resolve("tasks.csv"));

        assertThrows(IllegalArgumentException.class, () -> storage.save(null));
        assertThrows(TaskStorageException.class, () -> storage.save(Collections.singletonList(null)));
    }

    /**
     * Verifies that saving to an existing directory does not replace that directory.
     */
    @Test
    void save_directoryPath_throwsTaskStorageException() throws IOException {
        Path taskPath = tempDirectory.resolve("tasks.csv");
        Files.createDirectory(taskPath);

        TaskStorageException exception = assertThrows(TaskStorageException.class, () -> new Storage(
                taskPath).save(List.of(new ToDo("cannot save"))));

        assertEquals("I couldn't save tasks. Please check that the data folder is writable.",
                exception.getMessage());
        assertTrue(Files.isDirectory(taskPath));
    }
}
