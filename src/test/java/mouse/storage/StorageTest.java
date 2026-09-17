package mouse.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import mouse.MouseException;
import mouse.task.Deadline;
import mouse.task.Priority;
import mouse.task.TaskList;
import mouse.task.ToDo;

/**
 * Tests for {@link Storage} load and save.
 */
public class StorageTest {
    @TempDir
    Path tempDir;

    @Test
    public void load_missingFile_returnsEmptyList() {
        Storage storage = new Storage(tempDir.resolve("missing.txt").toString());
        assertEquals(0, storage.load().size());
    }

    @Test
    public void saveThenLoad_roundTrip_restoresTasks() throws MouseException {
        Path file = tempDir.resolve("mouse.txt");
        Storage storage = new Storage(file.toString());
        TaskList tasks = new TaskList();
        ToDo todo = new ToDo("read book");
        todo.markAsDone();
        todo.setPriority(Priority.HIGH);
        tasks.add(todo);
        tasks.add(new Deadline("return book", "2019-12-02"));
        storage.save(tasks);

        TaskList loaded = storage.load();
        assertEquals(2, loaded.size());
        assertEquals("read book", loaded.get(0).getDescription());
        assertTrue(loaded.get(0).isDone());
        assertEquals(Priority.HIGH, loaded.get(0).getPriority());
        assertEquals("[D][ ] return book (by: Dec 02 2019)", loaded.get(1).toString());
        assertNull(storage.getLoadWarning());
    }

    @Test
    public void load_corruptLines_skipsThemAndWarns() throws Exception {
        Path file = tempDir.resolve("mouse.txt");
        Files.writeString(file, String.join("\n",
                "T | 0 | none | keep me",
                "this is not a task",
                "D | 0 | none",
                ""), StandardCharsets.UTF_8);

        Storage storage = new Storage(file.toString());
        TaskList loaded = storage.load();
        assertEquals(1, loaded.size());
        assertEquals("keep me", loaded.get(0).getDescription());
        assertTrue(storage.getLoadWarning().contains("mouldy"));
    }

    @Test
    public void load_missingFile_hasNoWarning() {
        Storage storage = new Storage(tempDir.resolve("missing.txt").toString());
        storage.load();
        assertNull(storage.getLoadWarning());
    }
}
