package jelly.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import jelly.model.Deadline;
import jelly.model.Event;
import jelly.model.TaskList;
import jelly.model.Todo;

/** Tests persistence, including completion state, tags, and malformed records. */
class StorageTest {
    @Test
    void saveAndLoad_roundTripsAllTaskTypesAndStates() throws Exception {
        Path file = Files.createTempFile("jelly-storage", ".txt");
        Storage storage = new Storage(file);
        Todo todo = new Todo("buy milk");
        todo.setTag("home");
        todo.markAsDone();
        Deadline deadline = new Deadline("submit report", LocalDateTime.of(2026, 3, 1, 18, 0));
        Event event = new Event("team meeting", LocalDateTime.of(2026, 3, 2, 10, 0),
                LocalDateTime.of(2026, 3, 2, 11, 0));

        storage.save(new TaskList(List.of(todo, deadline, event)));
        TaskList loaded = storage.load();

        assertEquals(3, loaded.size());
        assertEquals("buy milk", loaded.getTask(0).getDescription());
        assertEquals("home", loaded.getTask(0).getTag());
        assertEquals(true, loaded.getTask(0).isDone());
        assertEquals(LocalDateTime.of(2026, 3, 1, 18, 0), ((Deadline) loaded.getTask(1)).getBy());
        assertEquals(LocalDateTime.of(2026, 3, 2, 11, 0), ((Event) loaded.getTask(2)).getTo());
    }

    @Test
    void load_missingOrMalformedRecords_skipsRecords() throws Exception {
        Path file = Files.createTempFile("jelly-storage", ".txt");
        Files.write(file, List.of("T | 0 | valid | ", "T | 0 | valid | bad tag!", "bad",
                "D | 2 | invalid status | 2026-03-01T18:00:00 | ",
                "E | 0 | invalid range | 2026-03-02T11:00:00 | 2026-03-02T10:00:00 | ",
                "Z | 0 | unknown | "));

        TaskList loaded = new Storage(file).load();

        assertEquals(1, loaded.size());
        assertFalse(loaded.getTask(0).isDone());
    }
}
