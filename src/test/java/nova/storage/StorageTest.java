package nova.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import nova.exception.NovaException;
import nova.task.Deadline;
import nova.task.Event;
import nova.task.Task;
import nova.task.Todo;

public class StorageTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void saveAndLoad_multipleTasks_preservesTasks() throws NovaException {
        Storage storage = new Storage(temporaryDirectory.resolve("tasks.txt").toString());
        Task todo = new Todo("read book");
        Task deadline = new Deadline("submit report", LocalDate.parse("2026-09-05"));
        Task event = new Event("demo", LocalDate.parse("2026-09-06"),
                LocalDate.parse("2026-09-07"));
        deadline.markDone();

        storage.save(List.of(todo, deadline, event));
        List<Task> loadedTasks = storage.load();

        assertEquals(3, loadedTasks.size());
        assertEquals(todo.toDataString(), loadedTasks.get(0).toDataString());
        assertEquals(deadline.toDataString(), loadedTasks.get(1).toDataString());
        assertEquals(event.toDataString(), loadedTasks.get(2).toDataString());
    }

    @Test
    public void saveAndLoad_descriptionContainingDelimiter_preservesDescription()
            throws NovaException {
        Storage storage = new Storage(temporaryDirectory.resolve("tasks.txt").toString());
        List<Task> tasks = List.of(
                new Todo("compare A | B"),
                new Deadline("choose A | B", LocalDate.parse("2026-09-05")),
                new Event("meeting A | B", LocalDate.parse("2026-09-06"),
                        LocalDate.parse("2026-09-07")));

        storage.save(tasks);
        List<Task> loadedTasks = storage.load();

        assertEquals(tasks.size(), loadedTasks.size());
        for (int i = 0; i < tasks.size(); i++) {
            assertEquals(tasks.get(i).toDataString(), loadedTasks.get(i).toDataString());
        }
    }

    @Test
    public void load_corruptedEvent_throwsNovaException() throws IOException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(file, "E | 0 | demo | 2026-09-05");
        Storage storage = new Storage(file.toString());

        assertThrows(NovaException.class, storage::load);
    }

    @Test
    public void load_invalidStatus_throwsNovaException() throws IOException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(file, "T | maybe | read book");
        Storage storage = new Storage(file.toString());

        assertThrows(NovaException.class, storage::load);
    }

    @Test
    public void load_missingFile_returnsEmptyList() throws NovaException {
        Storage storage = new Storage(temporaryDirectory.resolve("missing.txt").toString());

        assertTrue(storage.load().isEmpty());
    }
}
