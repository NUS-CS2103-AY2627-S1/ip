package max.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import max.maxexception.InvalidStorageDataException;
import max.task.Deadline;
import max.task.Event;
import max.task.Task;
import max.task.Todo;

public class StorageTest {

    @Test
    public void saveAndLoadDescriptionWithPipe_preservesTask() throws Exception {
        Path file = Files.createTempFile("max-storage-test", ".txt");
        Storage storage = new Storage(file.toString());
        List<Task> tasks = List.of(
                new Todo("plan \\ folder | implement"),
                new Deadline("review | submit", LocalDate.of(2026, 9, 17)));

        storage.save(tasks);

        List<Task> loaded = storage.load();
        assertEquals("plan \\ folder | implement", loaded.get(0).getDescription());
        assertEquals("review | submit", loaded.get(1).getDescription());
    }

    @Test
    public void saveAndLoad_eventRoundTrip_preservesDates() throws Exception {
        Path file = Files.createTempFile("max-storage-event", ".txt");
        Storage storage = new Storage(file.toString());
        List<Task> tasks = List.of(
                new Event("trip", LocalDate.of(2026, 9, 17), LocalDate.of(2026, 9, 19)));

        storage.save(tasks);

        List<Task> loaded = storage.load();
        assertEquals("trip", loaded.get(0).getDescription());
        assertEquals("[E] | [ ] | trip | 2026-09-17 | 2026-09-19", loaded.get(0).getItemString());
    }

    @Test
    public void saveAndLoad_timeRoundTrip_preservesDateTimes() throws Exception {
        Path file = Files.createTempFile("max-storage-time", ".txt");
        Storage storage = new Storage(file.toString());
        storage.save(List.of(new Deadline("report",
                LocalDateTime.of(2026, 9, 17, 18, 30))));

        List<Task> loaded = storage.load();
        assertEquals("[D] | [ ] | report | 2026-09-17T18:30", loaded.get(0).getItemString());
    }

    @Test
    public void load_invalidRecord_throwsInvalidStorageDataException() throws Exception {
        Path file = Files.createTempFile("max-storage-invalid", ".txt");
        Files.writeString(file, "[T] | [ ] | valid\n[Z] | [ ] | broken\n");

        Storage storage = new Storage(file.toString());
        assertThrows(InvalidStorageDataException.class, storage::load);
    }
}
