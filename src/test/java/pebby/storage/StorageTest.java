package pebby.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import pebby.task.Deadline;
import pebby.task.Event;
import pebby.task.Task;
import pebby.task.Todo;

class StorageTest {

    @TempDir
    Path temporaryDirectory;

    @Test
    void load_missingFile_returnsEmptyResult() throws IOException {
        Storage.LoadResult result = new Storage(temporaryDirectory.resolve("missing.txt")).load();

        assertTrue(result.tasks.isEmpty());
        assertEquals(0, result.skippedRecords);
    }

    @Test
    void saveThenLoad_allTaskTypesAndStatuses_preservesTaskDetails() throws IOException {
        Path filePath = temporaryDirectory.resolve("nested").resolve("pebby.txt");
        Todo todo = new Todo("read | café");
        Deadline deadline = new Deadline("return book", "15 June 2026");
        Event event = new Event("project meeting", "2026-06-15", "2026-06-16");
        deadline.markAsDone();

        new Storage(filePath).save(List.of(todo, deadline, event));
        Storage.LoadResult result = new Storage(filePath).load();

        assertEquals(0, result.skippedRecords);
        assertEquals(3, result.tasks.size());
        assertEquals("[T] [ ] read | café", result.tasks.get(0).toString());
        assertEquals("[D] [X] return book (by: Jun 15 2026)", result.tasks.get(1).toString());
        assertEquals("[E] [ ] project meeting (from: 2026-06-15 to: 2026-06-16)",
                result.tasks.get(2).toString());
    }

    @Test
    void load_malformedAndDuplicateRecords_skipsOnlyInvalidRecords() throws IOException {
        Path filePath = temporaryDirectory.resolve("pebby.txt");
        Files.write(filePath, List.of(
                "T | 0 | cmVhZCBib29r",
                "T | 0 | cmVhZCBib29r",
                "D | 1 | cmV0dXJuIGJvb2s= | bm90LWEtZGF0ZQ==",
                "E | 0 | bWVldGluZw== | MjAyNi0wNi0xNg== | MjAyNi0wNi0xNQ==",
                "unknown record"), StandardCharsets.UTF_8);

        Storage.LoadResult result = new Storage(filePath).load();

        assertEquals(1, result.tasks.size());
        assertEquals("read book", result.tasks.get(0).getDescription());
        assertEquals(4, result.skippedRecords);
    }

    @Test
    void save_unknownTaskType_throwsException() {
        Storage storage = new Storage(temporaryDirectory.resolve("pebby.txt"));
        Task unsupportedTask = new Task("ordinary task");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> storage.save(List.of(unsupportedTask)));

        assertEquals("Cannot save an unknown task type", exception.getMessage());
    }

    @Test
    void load_directoryInsteadOfFile_throwsIoException() throws IOException {
        Path directoryPath = Files.createDirectory(temporaryDirectory.resolve("data"));

        IOException exception = assertThrows(IOException.class, () -> new Storage(directoryPath).load());

        assertTrue(exception.getMessage().contains("not a readable file"));
        assertFalse(Files.isRegularFile(directoryPath));
    }
}
