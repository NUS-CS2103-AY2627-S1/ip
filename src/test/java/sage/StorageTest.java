package sage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import sage.model.Deadline;
import sage.model.Event;
import sage.model.TaskList;
import sage.model.Todo;
import sage.storage.Storage;

class StorageTest {
    @Test
    void saveAndLoadPreservesAllTaskTypesAndCompletion() throws Exception {
        Path directory = Files.createTempDirectory("sage-storage");
        Storage storage = new Storage(directory.resolve("nested/sage.txt").toString());
        TaskList expected = new TaskList();
        expected.add(new Todo("read book"));
        expected.add(new Deadline("report", LocalDateTime.of(2026, 1, 2, 13, 5)));
        expected.add(new Event("meeting", LocalDateTime.of(2026, 1, 2, 14, 0),
                LocalDateTime.of(2026, 1, 2, 15, 0)));
        expected.get(1).markAsDone();

        storage.save(expected);
        TaskList actual = storage.load();

        assertEquals(3, actual.size());
        assertEquals(expected.get(0).toString(), actual.get(0).toString());
        assertEquals(expected.get(1).toString(), actual.get(1).toString());
        assertEquals(expected.get(2).toString(), actual.get(2).toString());
    }

    @Test
    void loadMissingFileReturnsEmptyList() throws Exception {
        Path file = Files.createTempDirectory("sage-storage").resolve("missing.txt");

        assertEquals(0, new Storage(file.toString()).load().size());
    }

    @Test
    void loadInvalidDataReportsLineNumber() throws Exception {
        Path file = Files.createTempFile("sage-invalid", ".txt");
        Files.writeString(file, "T|2|broken|\n");

        Exception exception = assertThrows(Exception.class, () -> new Storage(file.toString()).load());
        assertEquals("Invalid task data on line 1.", exception.getMessage());
    }
}
