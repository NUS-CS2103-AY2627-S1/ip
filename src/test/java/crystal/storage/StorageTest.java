package crystal.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import crystal.exception.CrystalException;
import crystal.task.Deadline;
import crystal.task.Event;
import crystal.task.Task;
import crystal.task.Todo;
import crystal.task.WithinPeriod;

/**
 * Tests task persistence without reading or writing Crystal's real data file.
 */
public class StorageTest {
    @TempDir
    private Path tempDirectory;

    /** Verifies that first-run loading succeeds when the data file does not exist. */
    @Test
    public void loadTasks_missingDataFile_returnsEmptyList() throws CrystalException {
        Storage storage = new Storage(tempDirectory.resolve("data").resolve("crystal.txt"));

        ArrayList<Task> tasks = storage.loadTasks();

        assertTrue(tasks.isEmpty());
    }

    /** Verifies that an existing empty data file loads as an empty task list. */
    @Test
    public void loadTasks_emptyDataFile_returnsEmptyList() throws IOException, CrystalException {
        Path dataFile = tempDirectory.resolve("crystal.txt");
        Files.createFile(dataFile);
        Storage storage = new Storage(dataFile);

        assertTrue(storage.loadTasks().isEmpty());
    }

    /** Verifies missing directory creation and UTF-8 task persistence. */
    @Test
    public void saveTasks_missingParentDirectory_createsDirectoryAndUtf8File()
            throws IOException, CrystalException {
        Path dataFile = tempDirectory.resolve("nested").resolve("data").resolve("crystal.txt");
        Storage storage = new Storage(dataFile);
        Todo task = new Todo("réviser 日本語");

        storage.saveTasks(List.of(task));

        assertTrue(Files.isRegularFile(dataFile));
        assertEquals(List.of("T | 0 | réviser 日本語"),
                Files.readAllLines(dataFile, StandardCharsets.UTF_8));
    }

    /** Verifies that saving replaces rather than appends to an existing data file. */
    @Test
    public void saveTasks_existingDataFile_replacesOldContents()
            throws IOException, CrystalException {
        Path dataFile = tempDirectory.resolve("crystal.txt");
        Files.writeString(dataFile, "T | 0 | obsolete task\n", StandardCharsets.UTF_8);
        Storage storage = new Storage(dataFile);
        Deadline replacement = new Deadline("submit report", "2Dec26 0900");
        replacement.markAsDone();

        storage.saveTasks(List.of(replacement));

        assertEquals(List.of("D | 1 | submit report | 02 Dec 2026 0900"),
                Files.readAllLines(dataFile, StandardCharsets.UTF_8));
    }

    /** Verifies reconstruction of every task type, field, and completion status. */
    @Test
    public void loadTasks_validStoredTasks_reconstructsTypesFieldsAndStatuses()
            throws IOException, CrystalException {
        Path dataFile = tempDirectory.resolve("crystal.txt");
        Files.write(dataFile, List.of(
                "T | 0 | read book",
                "D | 1 | submit report | 02 Dec 2026 0900",
                "E | 0 | workshop | Monday 0600 | 1830",
                "W | 1 | collect certificate | after the ceremony | before closing"),
                StandardCharsets.UTF_8);
        Storage storage = new Storage(dataFile);

        ArrayList<Task> tasks = storage.loadTasks();

        assertEquals(4, tasks.size());
        assertInstanceOf(Todo.class, tasks.get(0));
        assertInstanceOf(Deadline.class, tasks.get(1));
        assertInstanceOf(Event.class, tasks.get(2));
        assertInstanceOf(WithinPeriod.class, tasks.get(3));
        assertEquals("[T][ ] read book", tasks.get(0).toString());
        assertEquals("[D][X] submit report (by: 02 Dec 2026 0900)",
                tasks.get(1).toString());
        assertEquals("[E][ ] workshop (from: Monday 0600 to: 1830)",
                tasks.get(2).toString());
        assertEquals("[W][X] collect certificate (from: after the ceremony to: before closing)",
                tasks.get(3).toString());
        assertFalse(tasks.get(0).isDone());
        assertTrue(tasks.get(1).isDone());
        assertTrue(tasks.get(3).isDone());
    }

    /** Verifies that saving then loading preserves every task's data representation. */
    @Test
    public void saveAndLoadTasks_allTaskTypes_preservesStorageRepresentations()
            throws CrystalException {
        Path dataFile = tempDirectory.resolve("crystal.txt");
        Storage storage = new Storage(dataFile);
        Todo todo = new Todo("read book");
        Deadline deadline = new Deadline("submit report", "2Dec26 0900");
        Event event = new Event("workshop", "2Dec26 0800", "2Dec26 1000");
        WithinPeriod withinPeriod = new WithinPeriod(
                "collect certificate", "after the ceremony", "before closing");
        deadline.markAsDone();
        List<Task> original = List.of(todo, deadline, event, withinPeriod);

        storage.saveTasks(original);
        List<Task> loaded = storage.loadTasks();

        assertEquals(original.stream().map(Task::toDataString).toList(),
                loaded.stream().map(Task::toDataString).toList());
    }

    /** Verifies consistent rejection of representative corrupted record shapes. */
    @Test
    public void loadTasks_corruptedDataShapes_throwConsistentException() throws IOException {
        Path dataFile = tempDirectory.resolve("crystal.txt");
        Storage storage = new Storage(dataFile);
        List<String> invalidLines = List.of(
                "not task data",
                "X | 0 | unknown type",
                "T | 2 | invalid status",
                "T | 0 | ",
                "D | 0 | submit report | ",
                "E | 0 | workshop | Monday | ",
                "T | 0 | extra | field",
                "D | 0 | missing deadline",
                "E | 0 | missing | end",
                "W | 0 | missing end | 15 Jan 2027");

        for (String invalidLine : invalidLines) {
            Files.writeString(dataFile, invalidLine, StandardCharsets.UTF_8);

            CrystalException exception = assertThrows(
                    CrystalException.class, storage::loadTasks, invalidLine);
            assertEquals("Your saved task data is invalid.", exception.getMessage());
        }
    }

    /** Verifies that a corrupt later record rejects the complete saved task file. */
    @Test
    public void loadTasks_validThenCorruptRecords_rejectsCompleteFile() throws IOException {
        Path dataFile = tempDirectory.resolve("crystal.txt");
        Files.write(dataFile, List.of(
                "T | 0 | read book",
                "not task data"), StandardCharsets.UTF_8);
        Storage storage = new Storage(dataFile);

        CrystalException exception = assertThrows(CrystalException.class, storage::loadTasks);

        assertEquals("Your saved task data is invalid.", exception.getMessage());
        assertEquals(List.of("T | 0 | read book", "not task data"),
                Files.readAllLines(dataFile, StandardCharsets.UTF_8));
    }

    /** Verifies user-facing read failure reporting when the data path is a directory. */
    @Test
    public void loadTasks_dataPathIsDirectory_throwsReadError() throws IOException {
        Path dataDirectory = tempDirectory.resolve("crystal.txt");
        Files.createDirectory(dataDirectory);
        Storage storage = new Storage(dataDirectory);

        CrystalException exception = assertThrows(CrystalException.class, storage::loadTasks);

        assertEquals("I couldn't load your tasks from the hard disk.", exception.getMessage());
    }

    /** Verifies user-facing write failure reporting when the data path is a directory. */
    @Test
    public void saveTasks_dataPathIsDirectory_throwsWriteError() throws IOException {
        Path dataDirectory = tempDirectory.resolve("crystal.txt");
        Files.createDirectory(dataDirectory);
        Storage storage = new Storage(dataDirectory);

        CrystalException exception = assertThrows(
                CrystalException.class, () -> storage.saveTasks(List.of(new Todo("read book"))));

        assertEquals("I couldn't save your tasks to the hard disk.", exception.getMessage());
    }
}
