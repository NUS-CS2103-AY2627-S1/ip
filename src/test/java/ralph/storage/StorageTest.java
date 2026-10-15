package ralph.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import ralph.model.Deadline;
import ralph.model.Event;
import ralph.model.Task;
import ralph.model.Todo;

class StorageTest {
    @Test
    void saveAndLoad_validTasks_roundTripsSuccessfully() throws IOException {
        Path tempFile = Files.createTempFile("ralph-storage", ".txt");
        Storage storage = new Storage(tempFile.toString());

        Todo todo = new Todo("read book");
        todo.markAsDone();
        Deadline deadline = new Deadline("submit report", LocalDateTime.of(2026, 9, 8, 18, 0));
        Event event = new Event("camp", LocalDateTime.of(2026, 9, 8, 9, 0), LocalDateTime.of(2026, 9, 8, 11, 0));

        storage.save(List.of(todo, deadline, event));
        List<Task> loaded = storage.load();

        assertEquals(3, loaded.size());
        assertInstanceOf(Todo.class, loaded.get(0));
        assertEquals("read book", loaded.get(0).getDescription());
        assertTrue(loaded.get(0).isDone());
        assertInstanceOf(Deadline.class, loaded.get(1));
        assertEquals("submit report", loaded.get(1).getDescription());
        assertInstanceOf(Event.class, loaded.get(2));
        assertEquals("camp", loaded.get(2).getDescription());
    }

    @Test
    void load_malformedLines_ignoresMalformedEntries() throws IOException {
        Path tempFile = Files.createTempFile("ralph-storage-malformed", ".txt");
        Files.write(
            tempFile,
            List.of(
                "T | 1 | valid task",
                "invalid line",
                "D | 1 | bad deadline | not-a-date"
            )
        );

        Storage storage = new Storage(tempFile.toString());
        List<Task> loaded = storage.load();

        assertEquals(1, loaded.size());
        assertEquals("valid task", loaded.get(0).getDescription());
        assertTrue(loaded.get(0).isDone());
    }

    @Test
    void tryParseDateTimeForReuse_supportedFormats_areParsed() {
        LocalDateTime d1 = Storage.tryParseDateTimeForReuse("2023-09-01");
        assertEquals(LocalDateTime.of(2023, 9, 1, 0, 0), d1);

        LocalDateTime d2 = Storage.tryParseDateTimeForReuse("2023-09-01T07:15");
        assertEquals(LocalDateTime.of(2023, 9, 1, 7, 15), d2);

        LocalDateTime d3 = Storage.tryParseDateTimeForReuse("2023-09-01 18:30");
        assertEquals(LocalDateTime.of(2023, 9, 1, 18, 30), d3);

        LocalDateTime d4 = Storage.tryParseDateTimeForReuse("2023-09-01 1830");
        assertEquals(LocalDateTime.of(2023, 9, 1, 18, 30), d4);

        // unsupported / invalid returns null
        LocalDateTime bad = Storage.tryParseDateTimeForReuse("not-a-date");
        assertNull(bad);
    }
}
