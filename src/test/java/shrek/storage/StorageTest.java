package shrek.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import shrek.task.Task;
import shrek.task.Todo;

public class StorageTest {

    @TempDir
    Path tempDir;

    @Test
    public void saveAndLoad_taggedTaskPreservesTags() {
        Path dataFile = tempDir.resolve("shrek.txt");
        Storage storage = new Storage(dataFile.toString());
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new Todo("play game", List.of("#weekend", "#fun")));

        storage.save(tasks);

        ArrayList<Task> loaded = storage.load();
        assertEquals(1, loaded.size());
        assertEquals(List.of("#fun", "#weekend"), loaded.get(0).getTags());
        assertEquals("T | 0 | play game | #fun,#weekend", loaded.get(0).toSaveFormat());
    }

    @Test
    public void load_legacyRecord_keepsTaskUntagged() throws IOException {
        Path dataFile = tempDir.resolve("shrek.txt");
        Files.writeString(dataFile, "T | 0 | read book\n");

        ArrayList<Task> loaded = new Storage(dataFile.toString()).load();

        assertEquals(1, loaded.size());
        assertTrue(loaded.get(0).getTags().isEmpty());
        assertEquals("T | 0 | read book", loaded.get(0).toSaveFormat());
    }

    @Test
    public void load_malformedTaggedRecord_skipsOnlyThatRecord() throws IOException {
        Path dataFile = tempDir.resolve("shrek.txt");
        Files.writeString(dataFile, "T | 0 | bad task | #valid,#valid\nT | 0 | good task\n");

        ArrayList<Task> loaded = new Storage(dataFile.toString()).load();

        assertEquals(1, loaded.size());
        assertEquals("good task", loaded.get(0).getDescription());
    }
}
