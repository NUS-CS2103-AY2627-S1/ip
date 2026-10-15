package taterror.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import taterror.task.Deadline;
import taterror.task.Event;
import taterror.task.Priority;
import taterror.task.Task;
import taterror.task.Todo;

/**
 * Tests for {@link Storage}, using a temp file per test so nothing ever
 * touches the real {@code data/tasks.txt} save file.
 */
public class StorageTest {

    @TempDir
    Path tempDir;

    private Storage storageAt(String fileName) {
        return new Storage(tempDir.resolve(fileName).toString());
    }

    @Test
    public void load_fileDoesNotExist_returnsEmptyList() {
        Storage storage = storageAt("nonexistent.txt");
        assertTrue(storage.load().isEmpty());
    }

    @Test
    public void saveThenLoad_todoWithPriority_roundTripsCorrectly() {
        Storage storage = storageAt("tasks.txt");
        Todo todo = new Todo("read book");
        todo.setPriority(Priority.HIGH);
        todo.markAsDone();

        storage.save(List.of(todo));
        List<Task> loaded = storage.load();

        assertEquals(1, loaded.size());
        Task result = loaded.get(0);
        assertEquals("read book", result.getDescription());
        assertEquals(Priority.HIGH, result.getPriority());
        assertTrue(result.isDone());
    }

    @Test
    public void saveThenLoad_deadlineWithoutPriority_roundTripsAsNonePriority() {
        Storage storage = storageAt("tasks.txt");
        Deadline deadline = new Deadline("submit report", "2019-10-15");

        storage.save(List.of(deadline));
        List<Task> loaded = storage.load();

        assertEquals(1, loaded.size());
        Deadline result = (Deadline) loaded.get(0);
        assertEquals("2019-10-15", result.getByRaw());
        assertEquals(Priority.NONE, result.getPriority());
        assertTrue(!result.isDone());
    }

    @Test
    public void saveThenLoad_eventWithPriority_roundTripsCorrectly() {
        Storage storage = storageAt("tasks.txt");
        Event event = new Event("standup", "9am", "10am");
        event.setPriority(Priority.LOW);

        storage.save(List.of(event));
        List<Task> loaded = storage.load();

        assertEquals(1, loaded.size());
        Event result = (Event) loaded.get(0);
        assertEquals("9am", result.getFromRaw());
        assertEquals("10am", result.getToRaw());
        assertEquals(Priority.LOW, result.getPriority());
    }

    @Test
    public void load_oldFormatLineWithoutPriorityColumn_stillLoadsCorrectly() throws IOException {
        Path file = tempDir.resolve("legacy.txt");
        Files.writeString(file, "T | 1 | legacy task\n");
        Storage storage = new Storage(file.toString());

        List<Task> loaded = storage.load();

        assertEquals(1, loaded.size());
        assertEquals("legacy task", loaded.get(0).getDescription());
        assertEquals(Priority.NONE, loaded.get(0).getPriority());
        assertTrue(loaded.get(0).isDone());
    }
}
