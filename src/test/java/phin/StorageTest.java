package phin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Checks storage creation, round trips, and corrupt-data rejection in an isolated directory.
 */
class StorageTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void load_missingFile_returnsEmptyList() throws IOException {
        Storage storage = new Storage(temporaryDirectory.resolve("nested/phin.txt"));
        assertTrue(storage.load().isEmpty());
    }

    @Test
    void saveAndLoad_allTaskTypesAndSpecialCharacters_roundTrips() throws IOException {
        Path file = temporaryDirectory.resolve("nested/phin.txt");
        Storage storage = new Storage(file);
        Todo todo = new Todo("pipe | percent % plus +");
        Deadline deadline = new Deadline("submit", "2024-02-29");
        Event event = new Event("meeting", "2024-03-01", "2024-03-02");
        deadline.markAsDone();

        storage.save(List.of(todo, deadline, event));

        assertEquals(List.of(todo.toString(), deadline.toString(), event.toString()),
                storage.load().stream().map(Task::toString).toList());
        assertTrue(storage.load().get(1).isDone);
    }

    @Test
    void load_invalidRecords_reportsLineAndPreservesFile() throws IOException {
        Path file = temporaryDirectory.resolve("phin.txt");
        String contents = "T|0|valid\nE|0|broken|2024-02-30|2024-03-01";
        Files.writeString(file, contents, StandardCharsets.UTF_8);
        Storage storage = new Storage(file);

        IOException exception = assertThrows(IOException.class, storage::load);

        assertEquals("Invalid task at line 2", exception.getMessage());
        assertEquals(contents, Files.readString(file, StandardCharsets.UTF_8));
    }

    @Test
    void load_malformedEscaping_rejected() throws IOException {
        Path file = temporaryDirectory.resolve("phin.txt");
        Files.writeString(file, "T|0|bad%2", StandardCharsets.UTF_8);

        assertThrows(IOException.class, new Storage(file)::load);
    }
}
