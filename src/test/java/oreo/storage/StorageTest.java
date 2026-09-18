package oreo.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import oreo.model.Task;
import oreo.model.TaskList;
import oreo.model.Todo;

/** Tests merchandise persistence and compatibility with older task records. */
class StorageTest {
    @Test
    void saveAndLoad_preservesMerchandiseAndSpecialCharacters() throws Exception {
        Path directory = Files.createTempDirectory("oreo-storage-test");
        Path file = directory.resolve("oreo.txt");
        Storage storage = new Storage(file);
        Todo task = new Todo("parent");
        task.setMerchandise("details | with symbols");

        storage.save(new TaskList(java.util.List.of(task)));

        Task loaded = storage.load().get(0);
        assertEquals("details | with symbols", loaded.getMerchandise());
    }

    @Test
    void load_oldRecord_defaultsMerchandiseToEmpty() throws Exception {
        Path directory = Files.createTempDirectory("oreo-storage-legacy-test");
        Path file = directory.resolve("oreo.txt");
        Files.writeString(file, "T|0|cGFyZW50\n");

        Task loaded = new Storage(file).load().get(0);
        assertTrue(loaded.getMerchandise().isEmpty());
    }

    @Test
    void saveAndLoad_taskWithoutMerchandise_preservesTask() throws Exception {
        Path directory = Files.createTempDirectory("oreo-storage-empty-merchandise-test");
        Path file = directory.resolve("oreo.txt");
        TaskList tasks = new TaskList();
        tasks.add(new Todo("buy milk"));

        Storage storage = new Storage(file);
        storage.save(tasks);

        Task loaded = storage.load().get(0);
        assertEquals("buy milk", loaded.getDescription());
        assertTrue(loaded.getMerchandise().isEmpty());
    }
}
