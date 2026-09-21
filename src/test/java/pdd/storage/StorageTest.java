package pdd.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import pdd.task.Deadline;
import pdd.task.Event;
import pdd.task.Task;
import pdd.task.Todo;

public class StorageTest {
    @TempDir
    Path tempDir;

    @Test
    public void load_missingFile_returnsEmptyList() {
        Storage storage = new Storage(tempDir.resolve("does-not-exist.txt").toString());
        assertTrue(storage.load().isEmpty());
    }

    @Test
    public void saveThenLoad_roundTripsAllTaskTypesAndDoneStatus() {
        Path file = tempDir.resolve("pdd.txt");
        Storage storage = new Storage(file.toString());

        Todo todo = new Todo("read book");
        todo.markAsDone();
        Deadline deadline = new Deadline("return book", java.time.LocalDate.of(2019, 12, 1));
        Event event = new Event("project meeting", java.time.LocalDate.of(2019, 12, 2), "4pm");

        List<Task> original = new ArrayList<>(List.of(todo, deadline, event));
        storage.save(original);

        List<Task> loaded = storage.load();
        assertEquals(3, loaded.size());
        assertEquals("[T][X] read book", loaded.get(0).toString());
        assertEquals("[D][ ] return book (by: Dec 01 2019)", loaded.get(1).toString());
        assertEquals("[E][ ] project meeting (from: Dec 02 2019 to: 4pm)", loaded.get(2).toString());
    }

    @Test
    public void load_corruptedLine_isSkippedButOtherLinesStillLoad() throws IOException {
        Path file = tempDir.resolve("pdd.txt");
        Files.writeString(file, "T | 0 | good todo" + System.lineSeparator()
                + "X | not a real task line" + System.lineSeparator());

        Storage storage = new Storage(file.toString());
        List<Task> loaded = storage.load();

        assertEquals(1, loaded.size());
        assertEquals("[T][ ] good todo", loaded.get(0).toString());
    }
}
