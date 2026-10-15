package remy.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import remy.task.Deadline;
import remy.task.Event;
import remy.task.Task;
import remy.task.TaskList;
import remy.task.Todo;

/**
 * Tests task-file persistence through {@link Storage}.
 *
 * @author LEE-wz
 */
class StorageTest {
    /**
     * Temporary directory provided by JUnit for creating temporary files during tests.
     */
    @TempDir
    Path temporaryDirectory;

    /**
     * Tests that the load method returns an empty task list when the task file does not exist.
     *
     * @throws IOException if an I/O error occurs during the test
     */
    @Test
    void load_taskFileDoesNotExist_emptyTaskListReturned() throws IOException {
        Storage storage = new Storage(temporaryDirectory.resolve("tasks.txt").toString());

        StorageLoadResult loadResult = storage.loadWithRecoveryDetails();

        assertTrue(loadResult.getTasks().isEmpty());
        assertTrue(loadResult.getInvalidLineNumbers().isEmpty());
    }

    /**
     * Tests that save and load preserve all task types and completion status.
     *
     * @throws IOException if an I/O error occurs during the test
     */
    @Test
    void saveAndLoad_tasksOfAllTypes_tasksAndCompletionStatusPreserved() throws IOException {
        Task todo = new Todo("Read book");
        todo.markAsDone();
        Task dateDeadline = new Deadline("Submit assignment", LocalDate.of(2026, 8, 23));
        Task dateTimeDeadline = new Deadline("Pay fees", LocalDateTime.of(2026, 8, 23, 18, 0));
        Task dateEvent = new Event("Project period", LocalDate.of(2026, 8, 24),
                LocalDate.of(2026, 8, 25));
        Task dateTimeEvent = new Event("Project meeting", LocalDateTime.of(2026, 8, 24, 14, 30),
                LocalDateTime.of(2026, 8, 24, 16, 0));
        dateTimeEvent.markAsDone();
        TaskList tasks = new TaskList(List.of(
                todo, dateDeadline, dateTimeDeadline, dateEvent, dateTimeEvent));
        Path taskFile = temporaryDirectory.resolve("data").resolve("tasks.txt");
        Storage storage = new Storage(taskFile.toString());

        storage.save(tasks);
        TaskList loadedTasks = storage.load();

        assertTrue(Files.exists(taskFile));
        assertEquals(tasks.getTasks().stream().map(Task::toString).toList(),
                loadedTasks.getTasks().stream().map(Task::toString).toList());
    }

    @Test
    void save_nullTaskList_assertionErrorThrown() {
        Storage storage = new Storage(temporaryDirectory.resolve("tasks.txt").toString());

        assertThrows(AssertionError.class, () -> storage.save(null));
    }

    /**
     * Tests that the load method recovers valid tasks and reports invalid line numbers.
     *
     * @throws IOException if an I/O error occurs during the test
     */
    @Test
    void load_taskFileContainsInvalidLines_validTasksRecoveredAndInvalidLinesReported() throws IOException {
        Path taskFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(taskFile, String.join(System.lineSeparator(),
                "[T][X] Read book",
                "not a task",
                "[D][ ] Missing deadline",
                "[T][ ] Return book"));
        Storage storage = new Storage(taskFile.toString());

        StorageLoadResult loadResult = storage.loadWithRecoveryDetails();

        assertEquals(List.of("[T][X] Read book", "[T][ ] Return book"),
                loadResult.getTasks().getTasks().stream().map(Task::toString).toList());
        assertEquals(List.of(2, 3), loadResult.getInvalidLineNumbers());
    }

    @Test
    void load_taskFileContainsMalformedTaskMarkers_invalidLineNumbersReported() throws IOException {
        Path taskFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(taskFile, String.join(System.lineSeparator(),
                "[T][X] Read book",
                "[Q][X] Unknown task type",
                "[T][?] Unknown task status",
                "[T][X]   ",
                "[T][X]"));
        Storage storage = new Storage(taskFile.toString());

        StorageLoadResult loadResult = storage.loadWithRecoveryDetails();

        assertEquals(List.of("[T][X] Read book"),
                loadResult.getTasks().getTasks().stream().map(Task::toString).toList());
        assertEquals(List.of(2, 3, 4, 5), loadResult.getInvalidLineNumbers());
    }

    @Test
    void load_taskFileContainsMalformedTaskDetails_allLinesRejected() throws IOException {
        Path taskFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(taskFile, String.join(System.lineSeparator(),
                "[D][ ] Report (by: Sep 24 2026",
                "[D][ ] Report by Sep 24 2026)",
                "[D][ ]  (by: Sep 24 2026)",
                "[D][ ] Report (by: )",
                "[D][ ] Report (by: Sep 31 2026)",
                "[E][ ] Meeting (from: Sep 24 2026 to: Sep 25 2026",
                "[E][ ] Meeting from Sep 24 2026 to: Sep 25 2026)",
                "[E][ ] Meeting (from: Sep 24 2026 until Sep 25 2026)",
                "[E][ ] Meeting to: Sep 25 2026 (from: Sep 24 2026)",
                "[E][ ]  (from: Sep 24 2026 to: Sep 25 2026)",
                "[E][ ] Meeting (from:  to: Sep 25 2026)",
                "[E][ ] Meeting (from: Sep 24 2026 to: )",
                "[E][ ] Meeting (from: Sep 24 2026 1200 to: Sep 25 2026)"));
        Storage storage = new Storage(taskFile.toString());

        StorageLoadResult loadResult = storage.loadWithRecoveryDetails();

        assertTrue(loadResult.getTasks().isEmpty());
        assertEquals(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13),
                loadResult.getInvalidLineNumbers());
    }

    @Test
    void load_taskFileContainsInvalidValues_validTasksRecoveredAndInvalidLinesReported() throws IOException {
        Path taskFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(taskFile, String.join(System.lineSeparator(),
                "[T][ ] Read book",
                "[T][X] Read book",
                "[D][ ] Impossible deadline (by: Feb 30 2026)",
                "[E][ ] Reversed event (from: Sep 25 2026 to: Sep 24 2026)",
                "[E][ ] Empty event (from: Sep 24 2026 15:00 to: Sep 24 2026 15:00)",
                "[D][ ] Valid leap day (by: Feb 29 2024)"));
        Storage storage = new Storage(taskFile.toString());

        StorageLoadResult loadResult = storage.loadWithRecoveryDetails();

        assertEquals(List.of("[T][ ] Read book", "[D][ ] Valid leap day (by: Feb 29 2024)"),
                loadResult.getTasks().getTasks().stream().map(Task::toString).toList());
        assertEquals(List.of(2, 3, 4, 5), loadResult.getInvalidLineNumbers());
    }

    @Test
    void load_taskPathIsDirectory_actionableStorageExceptionThrown() throws IOException {
        Path taskPath = temporaryDirectory.resolve("tasks");
        Files.createDirectory(taskPath);
        Storage storage = new Storage(taskPath.toString());

        StorageException exception = assertThrows(StorageException.class, storage::load);

        assertTrue(exception.getMessage().contains(taskPath.toString()));
        assertTrue(exception.getMessage().contains("is a directory, not a file"));
    }

    @Test
    void load_taskFileIsNotUtf8_actionableStorageExceptionThrown() throws IOException {
        Path taskFile = temporaryDirectory.resolve("tasks.txt");
        Files.write(taskFile, new byte[]{(byte) 0xC3, (byte) 0x28});
        Storage storage = new Storage(taskFile.toString());

        StorageException exception = assertThrows(StorageException.class, storage::load);

        assertTrue(exception.getMessage().contains(taskFile.toString()));
        assertTrue(exception.getMessage().contains("not valid UTF-8 text"));
    }

    @Test
    void load_taskFileAccessDenied_actionableStorageExceptionThrown() throws IOException {
        assumeTrue(FileSystems.getDefault().supportedFileAttributeViews().contains("posix"));
        Path taskFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(taskFile, "[T][ ] Read book");
        Set<PosixFilePermission> originalPermissions = Files.getPosixFilePermissions(taskFile);

        try {
            Files.setPosixFilePermissions(taskFile, Set.of());
            assumeFalse(Files.isReadable(taskFile));
            Storage storage = new Storage(taskFile.toString());

            StorageException exception = assertThrows(StorageException.class, storage::load);

            assertTrue(exception.getMessage().contains(taskFile.toString()));
            assertTrue(exception.getMessage().contains("access was denied"));
        } finally {
            Files.setPosixFilePermissions(taskFile, originalPermissions);
        }
    }

    @Test
    void save_parentPathIsFile_actionableStorageExceptionThrown() throws IOException {
        Path parentPath = temporaryDirectory.resolve("not-a-folder");
        Files.writeString(parentPath, "blocking file");
        Path taskFile = parentPath.resolve("tasks.txt");
        Storage storage = new Storage(taskFile.toString());
        TaskList tasks = new TaskList(List.of(new Todo("Read book")));

        StorageException exception = assertThrows(StorageException.class, () -> storage.save(tasks));

        assertTrue(exception.getMessage().contains(taskFile.toString()));
        assertTrue(exception.getMessage().contains("kept only for this session"));
    }

    @Test
    void save_taskPathIsDirectory_temporaryFileRemovedAndActionableExceptionThrown() throws IOException {
        Path taskPath = Files.createDirectory(temporaryDirectory.resolve("tasks.txt"));
        Storage storage = new Storage(taskPath.toString());
        TaskList tasks = new TaskList(List.of(new Todo("Read book")));

        StorageException exception = assertThrows(StorageException.class, () -> storage.save(tasks));

        assertTrue(exception.getMessage().contains(taskPath.toString()));
        assertTrue(exception.getMessage().contains("kept only for this session"));
        try (Stream<Path> files = Files.list(temporaryDirectory)) {
            assertEquals(List.of(taskPath), files.toList());
        }
    }

    @Test
    void save_parentFolderAccessDenied_actionableStorageExceptionThrown() throws IOException {
        assumeTrue(FileSystems.getDefault().supportedFileAttributeViews().contains("posix"));
        Path parentPath = Files.createDirectory(temporaryDirectory.resolve("read-only-folder"));
        Path taskFile = parentPath.resolve("tasks.txt");
        Set<PosixFilePermission> originalPermissions = Files.getPosixFilePermissions(parentPath);

        try {
            Files.setPosixFilePermissions(parentPath, Set.of(
                    PosixFilePermission.OWNER_READ,
                    PosixFilePermission.OWNER_EXECUTE));
            assumeFalse(Files.isWritable(parentPath));
            Storage storage = new Storage(taskFile.toString());
            TaskList tasks = new TaskList(List.of(new Todo("Read book")));

            StorageException exception = assertThrows(StorageException.class, () -> storage.save(tasks));

            assertTrue(exception.getMessage().contains(taskFile.toString()));
            assertTrue(exception.getMessage().contains("Access was denied"));
        } finally {
            Files.setPosixFilePermissions(parentPath, originalPermissions);
        }
    }
}
