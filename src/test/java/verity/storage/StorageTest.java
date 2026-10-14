package verity.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import verity.task.Deadline;
import verity.task.Event;
import verity.task.Task;
import verity.task.Todo;

class StorageTest {

    @TempDir
    Path tempDir;

    @Test
    void load_fileDoesNotExist_emptyListReturned() throws IOException {
        Storage storage = new Storage(tempDir.resolve("verity.txt").toString());

        List<Task> loaded = storage.load();

        assertTrue(loaded.isEmpty());
    }

    @Test
    void save_missingParentDirectory_isCreatedAutomatically() throws IOException {
        Path filePath = tempDir.resolve("data").resolve("verity.txt");
        Storage storage = new Storage(filePath.toString());

        storage.save(List.of(new Todo("read book")));

        assertTrue(Files.exists(filePath));
    }

    @Test
    void saveThenLoad_roundTrip_preservesAllTaskTypesAndDoneStatus() throws IOException {
        Storage storage = new Storage(tempDir.resolve("verity.txt").toString());
        Todo todo = new Todo("read book");
        todo.markAsDone();
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 10, 15));
        Event event = new Event("project meeting", LocalDate.of(2019, 8, 6), LocalDate.of(2019, 8, 7));
        List<Task> original = List.of(todo, deadline, event);

        storage.save(original);
        List<Task> loaded = storage.load();

        assertEquals(3, loaded.size());
        assertEquals("[T][X] read book", loaded.get(0).toString());
        assertEquals("[D][ ] return book (by: Oct 15 2019)", loaded.get(1).toString());
        assertEquals("[E][ ] project meeting (from: Aug 06 2019 to: Aug 07 2019)", loaded.get(2).toString());
    }

    @Test
    void load_unrecognizedTaskType_lineSkippedRestStillLoaded() throws IOException {
        Path filePath = tempDir.resolve("verity.txt");
        Files.writeString(filePath, "T | 1 | read book\nX | 0 | garbage type\nT | 0 | write essay\n");
        Storage storage = new Storage(filePath.toString());

        List<Task> loaded = storage.load();

        assertEquals(2, loaded.size());
        assertEquals("[T][X] read book", loaded.get(0).toString());
        assertEquals("[T][ ] write essay", loaded.get(1).toString());
    }

    @Test
    void load_invalidDoneFlag_lineSkipped() throws IOException {
        Path filePath = tempDir.resolve("verity.txt");
        Files.writeString(filePath, "T | maybe | read book\n");
        Storage storage = new Storage(filePath.toString());

        List<Task> loaded = storage.load();

        assertTrue(loaded.isEmpty());
    }

    @Test
    void load_deadlineMissingDueDateField_lineSkipped() throws IOException {
        Path filePath = tempDir.resolve("verity.txt");
        Files.writeString(filePath, "D | 0 | return book\n");
        Storage storage = new Storage(filePath.toString());

        List<Task> loaded = storage.load();

        assertTrue(loaded.isEmpty());
    }

    @Test
    void load_eventMissingEndDateField_lineSkipped() throws IOException {
        Path filePath = tempDir.resolve("verity.txt");
        Files.writeString(filePath, "E | 0 | conference | 2019-12-01\n");
        Storage storage = new Storage(filePath.toString());

        List<Task> loaded = storage.load();

        assertTrue(loaded.isEmpty());
    }

    @Test
    void load_lineWithTooFewFields_lineSkipped() throws IOException {
        Path filePath = tempDir.resolve("verity.txt");
        Files.writeString(filePath, "T | 1\n");
        Storage storage = new Storage(filePath.toString());

        List<Task> loaded = storage.load();

        assertTrue(loaded.isEmpty());
    }

    @Test
    void load_blankLine_silentlySkippedNotTreatedAsCorrupted() throws IOException {
        Path filePath = tempDir.resolve("verity.txt");
        Files.writeString(filePath, "T | 1 | read book\n\n");
        Storage storage = new Storage(filePath.toString());

        List<Task> loaded = storage.load();

        assertEquals(1, loaded.size());
    }

    @Test
    void load_invalidDateField_lineSkipped() throws IOException {
        Path filePath = tempDir.resolve("verity.txt");
        Files.writeString(filePath, "D | 0 | return book | not-a-date\n");
        Storage storage = new Storage(filePath.toString());

        List<Task> loaded = storage.load();

        assertTrue(loaded.isEmpty());
    }

    @Test
    void load_emptyDescriptionOrReverseEvent_lineSkippedRestStillLoaded() throws IOException {
        Path filePath = tempDir.resolve("verity.txt");
        Files.writeString(filePath, "T | 0 |   \n"
                + "E | 0 | conference | 2019-12-03 | 2019-12-01\n"
                + "T | 0 | read book\n");
        Storage storage = new Storage(filePath.toString());

        List<Task> loaded = storage.load();

        assertEquals(1, loaded.size());
        assertEquals("[T][ ] read book", loaded.get(0).toString());
    }

    @Test
    void save_emptyTaskList_writesEmptyFile() throws IOException {
        Path filePath = tempDir.resolve("verity.txt");
        Storage storage = new Storage(filePath.toString());

        storage.save(new ArrayList<>());

        assertTrue(Files.readString(filePath).isEmpty());
        assertTrue(storage.load().isEmpty());
    }
}
