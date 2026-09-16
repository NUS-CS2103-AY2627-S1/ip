package aider.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import aider.AiderException;
import aider.model.Deadline;
import aider.model.Event;
import aider.model.Task;
import aider.model.Todo;

/** Tests persistence of the supported task types. */
class StorageTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void saveAndLoad_roundTripsTasks() throws Exception {
        Storage storage = new Storage(temporaryDirectory.resolve("aider.txt").toString());
        Todo todo = new Todo("read book");
        todo.markAsDone();
        ArrayList<Task> tasks = new ArrayList<>(List.of(
                todo,
                new Deadline("submit report", LocalDateTime.of(2026, 9, 1, 9, 0)),
                new Event("conference", LocalDateTime.of(2026, 9, 1, 10, 0),
                        LocalDateTime.of(2026, 9, 1, 12, 0))));

        storage.save(tasks);
        ArrayList<Task> loadedTasks = storage.load();

        assertEquals(3, loadedTasks.size());
        assertEquals("[T][X] read book", loadedTasks.get(0).toString());
        assertEquals(tasks.get(1).toString(), loadedTasks.get(1).toString());
        assertEquals(tasks.get(2).toString(), loadedTasks.get(2).toString());
    }

    @Test
    void save_rejectsDirectoryAsDataFile() {
        Storage storage = new Storage(temporaryDirectory.toString());

        assertThrows(AiderException.class, () -> storage.save(new ArrayList<>()));
    }

    @Test
    void load_skipsInvalidCompletionFlag() throws Exception {
        Path file = temporaryDirectory.resolve("invalid.txt");
        Files.writeString(file, "T | 2 | read book" + System.lineSeparator());

        assertEquals(0, new Storage(file.toString()).load().size());
    }

    @Test
    void load_returnsEmptyListWhenFileDoesNotExist() throws Exception {
        Path file = temporaryDirectory.resolve("missing.txt");

        assertEquals(0, new Storage(file.toString()).load().size());
    }

    @Test
    void load_keepsValidLinesWhenOneLineIsMalformed() throws Exception {
        Path file = temporaryDirectory.resolve("mixed.txt");
        Files.writeString(file, "not a task\nT | 0 | read book" + System.lineSeparator());

        assertEquals(1, new Storage(file.toString()).load().size());
    }

    @Test
    void save_acceptsNullAsAnEmptyTaskList() throws Exception {
        Path file = temporaryDirectory.resolve("empty.txt");
        Storage storage = new Storage(file.toString());

        storage.save(null);

        assertEquals("", Files.readString(file));
    }
}
