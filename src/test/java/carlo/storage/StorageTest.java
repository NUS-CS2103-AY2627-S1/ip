package carlo.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import carlo.exception.CarloException;
import carlo.task.Deadline;
import carlo.task.Event;
import carlo.task.Task;
import carlo.task.Todo;

/**
 * Tests for {@link Storage#load()} and {@link Storage#save(List)}.
 */
class StorageTest {

    @Test
    void load_missingFile_returnsEmptyList(@TempDir Path tempDir) {
        Storage storage = new Storage(tempDir.resolve("nonexistent.txt").toString());
        List<Task> tasks = storage.load();
        assertTrue(tasks.isEmpty());
    }

    @Test
    void saveThenLoad_mixedTaskTypes_roundTripsCorrectly(@TempDir Path tempDir) throws CarloException {
        Path file = tempDir.resolve("data/carlo.txt");
        Storage storage = new Storage(file.toString());

        Todo todo = new Todo("read book");
        todo.markAsDone();
        Deadline deadline = new Deadline("submit report", "2019-12-02");
        Event event = new Event("trip", "2019-12-01", "2019-12-05");

        storage.save(List.of(todo, deadline, event));
        List<Task> loaded = storage.load();

        assertEquals(3, loaded.size());
        assertEquals("[T][X] read book", loaded.get(0).toString());
        assertEquals("[D][ ] submit report (by: Dec 2 2019)", loaded.get(1).toString());
        assertEquals("[E][ ] trip (from: Dec 1 2019 to: Dec 5 2019)", loaded.get(2).toString());
    }

    @Test
    void load_lineWithTooFewFields_skipsLine(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("carlo.txt");
        Files.writeString(file, "T | 1\nT | 0 | valid todo\n");

        List<Task> tasks = new Storage(file.toString()).load();

        assertEquals(1, tasks.size());
        assertEquals("valid todo", tasks.get(0).getDescription());
    }

    @Test
    void load_invalidCompletionFlag_skipsLine(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("carlo.txt");
        Files.writeString(file, "T | X | broken flag\nT | 1 | good todo\n");

        List<Task> tasks = new Storage(file.toString()).load();

        assertEquals(1, tasks.size());
        assertEquals("good todo", tasks.get(0).getDescription());
        assertTrue(tasks.get(0).isDone());
    }

    @Test
    void load_unknownTaskType_skipsLine(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("carlo.txt");
        Files.writeString(file, "Z | 0 | mystery task\nT | 0 | known todo\n");

        List<Task> tasks = new Storage(file.toString()).load();

        assertEquals(1, tasks.size());
        assertEquals("known todo", tasks.get(0).getDescription());
    }

    @Test
    void load_deadlineMissingDueTime_skipsLine(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("carlo.txt");
        Files.writeString(file, "D | 0 | no due date\nT | 0 | ok\n");

        List<Task> tasks = new Storage(file.toString()).load();

        assertEquals(1, tasks.size());
    }

    @Test
    void load_eventMissingEndTime_skipsLine(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("carlo.txt");
        Files.writeString(file, "E | 0 | trip | 2019-12-01\nT | 0 | ok\n");

        List<Task> tasks = new Storage(file.toString()).load();

        assertEquals(1, tasks.size());
    }

    @Test
    void save_missingParentDirectory_createsItAutomatically(@TempDir Path tempDir) {
        Path file = tempDir.resolve("nested/dir/carlo.txt");
        Storage storage = new Storage(file.toString());

        storage.save(List.of(new Todo("test")));

        assertTrue(Files.exists(file));
    }
}
