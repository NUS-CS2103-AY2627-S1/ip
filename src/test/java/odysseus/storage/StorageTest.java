package odysseus.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import odysseus.OdysseusException;
import odysseus.task.Deadline;
import odysseus.task.Event;
import odysseus.task.Task;
import odysseus.task.Todo;

/**
 * Tests that {@link Storage} persists and restores tasks correctly.
 *
 * <p>Uses a temporary directory so no real save file is touched.
 */
public class StorageTest {

    @TempDir
    private Path tempDir;

    @Test
    public void saveThenLoad_mixedTasks_roundTripsToSameSaveFormat() throws OdysseusException {
        Storage storage = new Storage(tempDir.resolve("tasks.txt").toString());
        List<Task> original = List.of(
                new Todo("read book"),
                new Deadline("submit report", "2026-06-06"),
                new Event("camp", "mon", "wed"));

        storage.save(original);
        List<Task> loaded = storage.load();

        assertEquals(original.size(), loaded.size());
        for (int i = 0; i < original.size(); i++) {
            assertEquals(original.get(i).toSaveFormat(), loaded.get(i).toSaveFormat());
        }
    }

    @Test
    public void load_missingFile_returnsEmptyList() throws OdysseusException {
        Storage storage = new Storage(tempDir.resolve("does-not-exist.txt").toString());
        assertEquals(0, storage.load().size());
    }
}
