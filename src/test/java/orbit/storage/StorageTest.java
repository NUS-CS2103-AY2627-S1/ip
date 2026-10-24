package orbit.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import orbit.exception.OrbitException;
import orbit.task.Deadline;
import orbit.task.Event;
import orbit.task.Task;
import orbit.task.Todo;

class StorageTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void saveAndLoad_mixedTasks_preservesEveryField() throws OrbitException {
        Path dataFile = temporaryDirectory.resolve("nested").resolve("orbit.txt");
        Storage storage = new Storage(dataFile);
        Todo todo = new Todo("read | review");
        Deadline deadline = new Deadline("submit", LocalDate.of(2028, 2, 29));
        deadline.markAsDone();
        Event event = new Event("camp", LocalDate.of(2026, 9, 1),
                LocalDate.of(2026, 9, 2));

        storage.save(List.of(todo, deadline, event));
        List<Task> loadedTasks = storage.load();

        assertEquals(3, loadedTasks.size());
        assertInstanceOf(Todo.class, loadedTasks.get(0));
        assertEquals("read | review", loadedTasks.get(0).getDescription());
        Deadline loadedDeadline = assertInstanceOf(Deadline.class, loadedTasks.get(1));
        assertTrue(loadedDeadline.isDone());
        assertEquals(LocalDate.of(2028, 2, 29), loadedDeadline.getBy());
        Event loadedEvent = assertInstanceOf(Event.class, loadedTasks.get(2));
        assertFalse(loadedEvent.isDone());
        assertEquals(LocalDate.of(2026, 9, 1), loadedEvent.getFrom());
        assertEquals(LocalDate.of(2026, 9, 2), loadedEvent.getTo());
    }

    @Test
    void load_missingFile_createsEmptyDataFile() throws OrbitException {
        Path dataFile = temporaryDirectory.resolve("missing").resolve("orbit.txt");
        Storage storage = new Storage(dataFile);

        List<Task> loadedTasks = storage.load();

        assertTrue(loadedTasks.isEmpty());
        assertTrue(Files.isRegularFile(dataFile));
    }

    @Test
    void load_invalidStoredDate_reportsCorruptLine() throws IOException {
        Path dataFile = temporaryDirectory.resolve("orbit.txt");
        String description = encode("broken deadline");
        String invalidDate = encode("not-a-date");
        Files.writeString(dataFile, "D\t0\t" + description + "\t" + invalidDate,
                StandardCharsets.UTF_8);
        Storage storage = new Storage(dataFile);

        OrbitException exception = assertThrows(OrbitException.class, storage::load);

        assertEquals("The data file is invalid at line 1.", exception.getMessage());
    }

    @Test
    void load_eventWithInvalidDateRange_reportsCorruptLine() throws IOException {
        Path dataFile = temporaryDirectory.resolve("orbit.txt");
        String description = encode("broken event");
        String from = encode("2026-09-03");
        String to = encode("2026-09-02");
        Files.writeString(dataFile, "E\t0\t" + description + "\t" + from + "\t" + to,
                StandardCharsets.UTF_8);
        Storage storage = new Storage(dataFile);

        OrbitException exception = assertThrows(OrbitException.class, storage::load);

        assertEquals("The data file is invalid at line 1.", exception.getMessage());
    }

    private String encode(String value) {
        return Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }
}
