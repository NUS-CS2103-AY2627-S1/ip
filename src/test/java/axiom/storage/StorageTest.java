package axiom.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermissions;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Set;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import axiom.AxiomException;
import axiom.task.Deadline;
import axiom.task.Event;
import axiom.task.Task;
import axiom.task.TaskList;
import axiom.task.Todo;

class StorageTest {
    @TempDir
    private Path tempDir;

    @Test
    void load_missingFile_returnsEmptyList() throws AxiomException {
        Storage storage = new Storage(tempDir.resolve("missing.txt").toString());
        assertTrue(storage.load().isEmpty());
    }

    @Test
    void load_directoryPath_exceptionThrown() {
        Storage storage = new Storage(tempDir.toString());
        AxiomException exception = assertThrows(AxiomException.class, storage::load);
        assertTrue(exception.getMessage().contains("is a directory"));
    }

    @Test
    void load_unknownTaskType_exceptionThrown() throws IOException {
        Path file = tempDir.resolve("axiom.txt");
        Files.writeString(file, "X | 0 | mystery\n");
        Storage storage = new Storage(file.toString());
        AxiomException exception = assertThrows(AxiomException.class, storage::load);
        assertTrue(exception.getMessage().contains("unknown task type 'X'"));
        assertTrue(exception.getMessage().contains("line 1"));
    }

    @Test
    void load_todoWithExtraField_exceptionThrown() throws IOException {
        Path file = tempDir.resolve("axiom.txt");
        Files.writeString(file, "T | 0 | read book | extra\n");
        Storage storage = new Storage(file.toString());
        AxiomException exception = assertThrows(AxiomException.class, storage::load);
        assertTrue(exception.getMessage().contains("a todo should have 3 fields"));
    }

    @Test
    void load_duplicateTasks_exceptionThrown() throws IOException {
        Path file = tempDir.resolve("axiom.txt");
        Files.writeString(file, "T | 0 | read book\nT | 1 | read book\n");
        Storage storage = new Storage(file.toString());
        AxiomException exception = assertThrows(AxiomException.class, storage::load);
        assertTrue(exception.getMessage().contains("duplicate of an earlier task"));
        assertTrue(exception.getMessage().contains("line 2"));
    }

    @Test
    void load_eventFromAfterTo_exceptionThrown() throws IOException {
        Path file = tempDir.resolve("axiom.txt");
        Files.writeString(file, "E | 0 | meeting | 2019-08-06T16:00 to 2019-08-06T14:00\n");
        Storage storage = new Storage(file.toString());
        AxiomException exception = assertThrows(AxiomException.class, storage::load);
        assertTrue(exception.getMessage().contains("event /from time must be earlier than /to time"));
    }

    @Test
    void load_invalidStoredDate_exceptionThrown() throws IOException {
        Path file = tempDir.resolve("axiom.txt");
        Files.writeString(file, "D | 0 | homework | 2019-02-30T00:00\n");
        Storage storage = new Storage(file.toString());
        AxiomException exception = assertThrows(AxiomException.class, storage::load);
        assertTrue(exception.getMessage().contains("Invalid stored date/time"));
    }

    @Test
    void saveAndLoad_roundTrip_preservesTasks() throws AxiomException {
        Path file = tempDir.resolve("axiom.txt");
        Storage storage = new Storage(file.toString());
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Deadline("homework", LocalDateTime.of(2019, 6, 6, 0, 0)));
        tasks.add(new Event("meeting",
                LocalDateTime.of(2019, 8, 6, 14, 0),
                LocalDateTime.of(2019, 8, 6, 16, 0)));
        storage.save(tasks);

        ArrayList<Task> loaded = storage.load();
        assertEquals(3, loaded.size());
        assertEquals("read book", loaded.get(0).getDescription());
        assertEquals("homework", loaded.get(1).getDescription());
        assertEquals("meeting", loaded.get(2).getDescription());
    }

    @Test
    void save_directoryPath_exceptionThrown() {
        Storage storage = new Storage(tempDir.toString());
        AxiomException exception = assertThrows(AxiomException.class, () -> storage.save(new TaskList()));
        assertTrue(exception.getMessage().contains("is a directory"));
    }

    @Test
    void load_unreadableFile_exceptionThrown() throws IOException {
        Assumptions.assumeTrue(FileSystems.getDefault().supportedFileAttributeViews().contains("posix"));
        Path file = tempDir.resolve("axiom.txt");
        Files.writeString(file, "T | 0 | read book\n");
        Files.setPosixFilePermissions(file, Set.of());
        try {
            Storage storage = new Storage(file.toString());
            AxiomException exception = assertThrows(AxiomException.class, storage::load);
            assertTrue(exception.getMessage().contains("Access denied"));
        } finally {
            Files.setPosixFilePermissions(file, PosixFilePermissions.fromString("rw-------"));
        }
    }
}
