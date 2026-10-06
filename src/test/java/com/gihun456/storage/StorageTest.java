package com.gihun456.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.gihun456.model.Deadline;
import com.gihun456.model.Event;
import com.gihun456.model.Task;
import com.gihun456.model.Todo;

public class StorageTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void saveAndLoad_allTaskTypes_preservesTasksAndStatus() throws Exception {
        Path file = temporaryDirectory.resolve("nested").resolve("games.txt");
        Todo todo = new Todo("Read book");
        Deadline deadline = new Deadline("Submit report", LocalDateTime.of(2026, 9, 15, 10, 30));
        Event event = new Event("Meeting",
                LocalDateTime.of(2026, 9, 15, 11, 0),
                LocalDateTime.of(2026, 9, 15, 12, 0));
        event.markAsDone();

        new Storage(file).save(List.of(todo, deadline, event));
        List<Task> loaded = new Storage(file).load();

        assertTrue(Files.exists(file));
        assertEquals(List.of(todo.toString(), deadline.toString(), event.toString()),
                loaded.stream().map(Task::toString).toList());
        assertTrue(loaded.get(2).isDone());
    }

    @Test
    public void load_missingFile_returnsEmptyList() throws Exception {
        assertTrue(new Storage(temporaryDirectory.resolve("missing.txt")).load().isEmpty());
    }

    @Test
    public void load_malformedRecords_skipsInvalidLines() throws Exception {
        Path file = temporaryDirectory.resolve("malformed.txt");
        Files.writeString(file, String.join(System.lineSeparator(),
                "not a task",
                "X |   | Unknown",
                "D |   | Missing date",
                "D |   | Invalid date | tomorrow",
                "T |   | Valid todo"));

        List<Task> loaded = new Storage(file).load();

        assertEquals(1, loaded.size());
        assertEquals("Valid todo", loaded.get(0).getTaskName());
        assertFalse(loaded.get(0).isDone());
    }
}
