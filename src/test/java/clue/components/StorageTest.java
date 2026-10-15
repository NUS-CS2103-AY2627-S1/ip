package clue.components;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import clue.tasks.Event;
import clue.tasks.Task;
import clue.tasks.ToDo;

/** Tests saving and loading tasks through {@link Storage}. */
class StorageTest {

    @TempDir
    Path temporaryDirectory;

    @Test
    void saveAndLoad_tasks_restoresPersistedTasks() {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Storage storage = new Storage(file.toString());
        List<Task> tasks = List.of(
                new ToDo("buy groceries"),
                new Event("project meeting",
                        LocalDateTime.of(2026, 9, 1, 10, 0),
                        LocalDateTime.of(2026, 9, 1, 11, 0)));

        assertTrue(storage.save(tasks));
        assertEquals(tasks.stream().map(t -> t.save(Storage.SEPARATOR)).toList(),
                storage.load(new ArrayList<>()).stream().map(t -> t.save(Storage.SEPARATOR)).toList());
    }

    @Test
    void save_emptyTaskList_createsEmptyStorageFile() {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Storage storage = new Storage(file.toString());

        assertTrue(storage.save(List.of()));
        assertTrue(Files.exists(file));
        assertTrue(storage.load(new ArrayList<>()).isEmpty());
    }

    @Test
    void load_missingFile_returnsEmptyTaskList() {
        Path file = temporaryDirectory.resolve("does-not-exist.txt");

        assertTrue(new Storage(file.toString()).load(new ArrayList<>()).isEmpty());
    }
}
