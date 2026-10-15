package stephen.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import stephen.task.Deadline;
import stephen.task.Event;
import stephen.task.Task;
import stephen.task.Todo;

/** JUnit tests for repeatable task persistence and malformed-record recovery. */
class StorageJUnitTest {
    @TempDir
    Path tempDirectory;

    /** Verifies that loading a missing file has no filesystem side effects. */
    @Test
    void loadMissingFileReturnsEmptyListWithoutCreatingFile() throws IOException {
        Path dataFile = tempDirectory.resolve("missing").resolve("tasks.txt");

        assertEquals(List.of(), new Storage(dataFile).load());
        assertFalse(Files.exists(dataFile));
        assertFalse(Files.exists(dataFile.getParent()));
    }

    /** Verifies that an unreadable data path is reported as a checked I/O failure. */
    @Test
    void loadPathIsDirectoryThrowsIoException() throws IOException {
        Path dataFile = tempDirectory.resolve("tasks.txt");
        Files.createDirectory(dataFile);

        assertThrows(IOException.class, () -> new Storage(dataFile).load());
    }

    /** Verifies lossless persistence of all task types and escaped content. */
    @Test
    void saveAndLoadAllTaskTypesStatusEscapesAndDuplicatesRoundTrip() throws IOException {
        Path dataFile = tempDirectory.resolve("nested").resolve("tasks.txt");
        Storage storage = new Storage(dataFile);
        Todo special = new Todo("line one\nline two | path\\notes");
        Deadline deadline = new Deadline("submit", LocalDate.of(2024, 2, 29));
        Event event = new Event("conference",
                LocalDate.of(2024, 3, 1), LocalDate.of(2024, 3, 3));
        deadline.markAsDone();

        storage.save(List.of(special, deadline, event, special));

        assertEquals(List.of(
                "T | 0 | line one\\nline two \\| path\\\\notes",
                "D | 1 | submit | 2024-02-29",
                "E | 0 | conference | 2024-03-01 | 2024-03-03",
                "T | 0 | line one\\nline two \\| path\\\\notes"),
                serialized(storage.load()));
    }

    /** Verifies that saving an empty list clears existing file contents. */
    @Test
    void saveEmptyListReplacesExistingContents() throws IOException {
        Path dataFile = tempDirectory.resolve("tasks.txt");
        Storage storage = new Storage(dataFile);
        storage.save(List.of(new Todo("old task")));

        storage.save(List.of());

        assertEquals(List.of(), storage.load());
        assertEquals(List.of(), Files.readAllLines(dataFile, StandardCharsets.UTF_8));
    }

    /** Verifies that an unusable destination is reported instead of terminating the application. */
    @Test
    void savePathIsDirectoryThrowsIoException() throws IOException {
        Path dataFile = tempDirectory.resolve("tasks.txt");
        Files.createDirectory(dataFile);

        assertThrows(IOException.class, () ->
                new Storage(dataFile).save(List.of(new Todo("unsaved task"))));
    }

    /** Verifies that malformed records are skipped without discarding valid records. */
    @Test
    void loadBlankAndMalformedRecordsSkipsOnlyBadLines() throws IOException {
        Path dataFile = tempDirectory.resolve("tasks.txt");
        Files.write(dataFile, List.of(
                "",
                "T | 0 | first",
                "T | 0 | first",
                "unknown",
                "T | 2 | invalid status",
                "T | 0 | broken escape\\q",
                "D | 1 | bad date | 2023-02-29",
                "E | 0 | missing end | 2024-01-01",
                "E | 0 | reversed | 2024-01-03 | 2024-01-02",
                "E | 1 | valid | 2024-01-01 | 2024-01-02"), StandardCharsets.UTF_8);

        assertEquals(List.of(
                "T | 0 | first",
                "T | 0 | first",
                "E | 1 | valid | 2024-01-01 | 2024-01-02"),
                serialized(new Storage(dataFile).load()));
    }

    /**
     * Converts tasks to their stable persisted representation for exact comparison.
     *
     * @param tasks tasks to serialize
     * @return serialized task records in list order
     */
    private static List<String> serialized(List<Task> tasks) {
        return tasks.stream().map(Task::toDataString).toList();
    }
}
