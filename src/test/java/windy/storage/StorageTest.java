package windy.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import windy.task.Deadline;
import windy.task.Event;
import windy.task.Task;
import windy.task.Todo;

/** Tests task serialization and safe file replacement. */
public class StorageTest {

    @TempDir
    Path tempDirectory;

    @Test
    public void saveTasks_allTaskTypes_preservesDataFormat() throws Exception {
        Path filePath = tempDirectory.resolve("windy.txt");
        Storage storage = new Storage(filePath.toString());
        List<Task> tasks = List.of(
                new Todo("read book", false),
                new Deadline("return book", true, "2026-9-1"),
                new Event("holiday", false, "2026-9-2", "2026-9-3"));

        storage.saveTasks(tasks);

        assertEquals(List.of(
                "T | 0 | read book",
                "D | 1 | return book | 2026-09-01",
                "E | 0 | holiday | 2026-09-02 | 2026-09-03"), Files.readAllLines(filePath));
    }

    @Test
    public void loadTasks_missingFile_returnsEmptyList() throws IOException {
        Storage storage = new Storage(tempDirectory.resolve("missing/windy.txt").toString());

        assertTrue(storage.loadTasks().isEmpty());
    }

    @Test
    public void saveAndLoadTasks_allTaskTypes_preserveOrderAndFields() throws Exception {
        Storage storage = new Storage(tempDirectory.resolve("data/windy.txt").toString());
        List<Task> original = List.of(
                new Todo("read book", true),
                new Deadline("submit work", false, "2026-9-3"),
                new Event("holiday", true, "2026-9-4", "2026-9-5"));

        storage.saveTasks(original);
        List<Task> loaded = storage.loadTasks();

        assertEquals(List.of(Todo.class, Deadline.class, Event.class),
                loaded.stream().map(Task::getClass).toList());
        assertEquals(original.stream().map(Task::toDataString).toList(),
                loaded.stream().map(Task::toDataString).toList());
    }

    @Test
    public void loadTasks_invalidCompletionStatus_throwsIoException() throws IOException {
        Storage storage = createStorageWithRecord("T | x | read book");

        IOException exception = assertThrows(IOException.class, storage::loadTasks);

        assertTrue(exception.getMessage().contains("completion status must be 0 or 1"));
    }

    @Test
    public void loadTasks_extraField_throwsIoException() throws IOException {
        Storage storage = createStorageWithRecord("T | 0 | read book | unexpected");

        IOException exception = assertThrows(IOException.class, storage::loadTasks);

        assertTrue(exception.getMessage().contains("expected 3 fields but found 4"));
    }

    @Test
    public void loadTasks_missingField_throwsIoException() throws IOException {
        Storage storage = createStorageWithRecord("D | 0 | return book");

        IOException exception = assertThrows(IOException.class, storage::loadTasks);

        assertTrue(exception.getMessage().contains("expected 4 fields but found 3"));
    }

    @Test
    public void loadTasks_unknownTaskType_throwsIoException() throws IOException {
        Storage storage = createStorageWithRecord("X | 0 | read book");

        IOException exception = assertThrows(IOException.class, storage::loadTasks);

        assertTrue(exception.getMessage().contains("unsupported task type 'X'"));
    }

    @Test
    public void loadTasks_invalidDeadlineDate_identifiesFieldAndLine() throws IOException {
        Storage storage = createStorageWithRecord("D | 0 | book | 2019-2-30");

        IOException exception = assertThrows(IOException.class, storage::loadTasks);

        assertEquals("Invalid task record at line 1: deadline date '2019-2-30' is invalid",
                exception.getMessage());
    }

    @Test
    public void loadTasks_invalidSecondRecord_identifiesSecondLine() throws IOException {
        Storage storage = createStorageWithRecord("T | 0 | valid\nD | 0 | invalid | 2025-2-29");

        IOException exception = assertThrows(IOException.class, storage::loadTasks);

        assertEquals("Invalid task record at line 2: deadline date '2025-2-29' is invalid",
                exception.getMessage());
    }

    @Test
    public void loadTasks_invalidEventEndDate_identifiesFieldAndLine() throws IOException {
        Storage storage = createStorageWithRecord("E | 0 | trip | 2026-9-3 | 2026-13-1");

        IOException exception = assertThrows(IOException.class, storage::loadTasks);

        assertEquals("Invalid task record at line 1: event end date '2026-13-1' is invalid",
                exception.getMessage());
    }

    @Test
    public void loadTasks_reversedEventDates_rejectsRecord() throws IOException {
        Storage storage = createStorageWithRecord("E | 0 | trip | 2026-9-5 | 2026-9-3");

        IOException exception = assertThrows(IOException.class, storage::loadTasks);

        assertEquals("Invalid task record at line 1: The event end date cannot be before its start date",
                exception.getMessage());
    }

    @Test
    public void saveTasks_failedReplacement_cleansTemporaryFileAndPreservesTarget() throws IOException {
        Path target = tempDirectory.resolve("windy.txt");
        Files.createDirectory(target);
        Path original = target.resolve("original.txt");
        Files.writeString(original, "preserved");
        Storage storage = new Storage(target.toString());

        assertThrows(IOException.class, () -> storage.saveTasks(List.of(new Todo("A", false))));

        assertEquals("preserved", Files.readString(original));
        try (var files = Files.list(tempDirectory)) {
            assertEquals(List.of(target), files.toList());
        }
    }

    private Storage createStorageWithRecord(String record) throws IOException {
        Path filePath = tempDirectory.resolve("windy.txt");
        Files.writeString(filePath, record);
        return new Storage(filePath.toString());
    }
}
