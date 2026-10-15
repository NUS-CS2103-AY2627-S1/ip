package neil.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import neil.exception.NeilException;
import neil.task.DeadlineTask;
import neil.task.EventTask;
import neil.task.Task;
import neil.task.ToDoTask;

/**
 * Tests for saving and loading task data from disk.
 */
class StorageTest {

    @TempDir
    Path temporaryDirectory;

    @Test
    void load_fileDoesNotExist_emptyTaskListReturned() throws NeilException {
        Storage storage = new Storage(temporaryDirectory.resolve("missing.txt").toString());

        assertEquals(List.of(), storage.load());
    }

    @Test
    void save_thenLoad_tasksPersistedInOrder() throws NeilException, IOException {
        Path taskFile = temporaryDirectory.resolve("nested/tasks.txt");
        Storage storage = new Storage(taskFile.toString());
        Task todo = new ToDoTask("read book");
        Task deadline = new DeadlineTask("return book", java.time.LocalDate.of(2026, 8, 25));
        deadline.markAsDone();
        Task event = new EventTask("team meeting", "2026-08-25 14:00", "2026-08-25 16:00");

        storage.save(List.of(todo, deadline, event));

        assertEquals(List.of(
                "T | 0 | read book",
                "D | 1 | return book | 2026-08-25",
                "E | 0 | team meeting | 2026-08-25 14:00 | 2026-08-25 16:00"),
                Files.readAllLines(taskFile));
        assertEquals(List.of(
                "T | 0 | read book",
                "D | 1 | return book | 2026-08-25",
                "E | 0 | team meeting | 2026-08-25 14:00 | 2026-08-25 16:00"),
                storage.load().stream().map(Task::encode).toList());
    }

    @Test
    void save_existingFile_fileReplaced() throws NeilException, IOException {
        Path taskFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(taskFile, "T | 0 | existing task\n");
        Storage storage = new Storage(taskFile.toString());

        storage.save(List.of(new ToDoTask("replacement task")));

        assertEquals(List.of("T | 0 | replacement task"), Files.readAllLines(taskFile));
    }

    @Test
    void save_replacementFails_originalFilePreservedAndTemporaryFileRemoved() throws IOException {
        Path taskFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(taskFile, "T | 0 | existing task\n");
        Storage storage = new Storage(taskFile.toString()) {
            @Override
            void replaceFile(Path temporaryFile, Path targetFile) throws IOException {
                assertEquals(List.of("T | 0 | replacement task"), Files.readAllLines(temporaryFile));
                throw new IOException("Simulated replacement failure");
            }
        };

        assertThrows(NeilException.class, () -> storage.save(List.of(new ToDoTask("replacement task"))));

        assertEquals(List.of("T | 0 | existing task"), Files.readAllLines(taskFile));
        try (Stream<Path> files = Files.list(temporaryDirectory)) {
            assertEquals(List.of(taskFile), files.toList());
        }
    }

    @Test
    void save_targetIsDirectory_exceptionThrownAndDirectoryPreserved() throws IOException {
        Path storageDirectory = temporaryDirectory.resolve("tasks");
        Files.createDirectory(storageDirectory);
        Path existingFile = storageDirectory.resolve("existing.txt");
        Files.writeString(existingFile, "existing data");
        Storage storage = new Storage(storageDirectory.toString());
        Task task = new ToDoTask("read book");

        NeilException exception = assertThrows(NeilException.class, () -> storage.save(List.of(task)));

        assertEquals("Neil: Unable to save tasks to " + storageDirectory, exception.getMessage());
        assertTrue(Files.isDirectory(storageDirectory));
        assertEquals("existing data", Files.readString(existingFile));
    }

    @Test
    void save_emptyTaskList_existingFileReplacedWithEmptyFile() throws NeilException, IOException {
        Path taskFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(taskFile, "T | 0 | existing task\n");
        Storage storage = new Storage(taskFile.toString());

        storage.save(List.of());

        assertEquals(0, Files.size(taskFile));
        assertEquals(List.of(), storage.load());
    }

    @Test
    void load_fileWithBlankLines_blankLinesIgnored() throws NeilException, IOException {
        Path taskFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(taskFile, "\nT | 0 | read book\n   \n");
        Storage storage = new Storage(taskFile.toString());

        assertEquals(List.of("T | 0 | read book"),
                storage.load().stream().map(Task::encode).toList());
    }

    @Test
    void load_fileWithInvalidTaskData_exceptionThrown() throws IOException {
        Path taskFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(taskFile, "T | 2 | read book\n");
        Storage storage = new Storage(taskFile.toString());

        assertThrows(NeilException.class, storage::load);
    }

    @Test
    void load_targetIsDirectory_exceptionThrown() throws IOException {
        Path storageDirectory = temporaryDirectory.resolve("tasks");
        Files.createDirectory(storageDirectory);
        Storage storage = new Storage(storageDirectory.toString());

        NeilException exception = assertThrows(NeilException.class, storage::load);

        assertEquals("Neil: Unable to load tasks from " + storageDirectory, exception.getMessage());
    }

    @Test
    void load_fileContainsMalformedUtf8_exceptionThrown() throws IOException {
        Path taskFile = temporaryDirectory.resolve("tasks.txt");
        Files.write(taskFile, new byte[] {(byte) 0xC3, (byte) 0x28});
        Storage storage = new Storage(taskFile.toString());

        NeilException exception = assertThrows(NeilException.class, storage::load);

        assertEquals("Neil: Unable to load tasks from " + taskFile, exception.getMessage());
    }
}
