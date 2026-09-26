package amadeus.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import amadeus.AmadeusException;
import amadeus.task.Task;
import amadeus.task.Todo;

/**
 * Unit tests for how {@link Storage} saves and loads a task's tags.
 * <p>
 * Each test works against its own temporary folder rather than the real "data"
 * folder, so the tests cannot see or disturb whatever is actually saved there.
 */
public class StorageTest {

    @TempDir
    private Path tempDir;

    /** A tagged task, once saved and reloaded, keeps the same tags. */
    @Test
    void saveThenLoad_taskWithTags_tagsSurviveRoundTrip() throws AmadeusException {
        Storage storage = new Storage(tempDir.toString(), "amadeus.txt");
        Todo todo = new Todo("read book");
        todo.setTags(List.of("#fun", "#urgent"));

        List<Task> tasks = new ArrayList<>();
        tasks.add(todo);
        storage.save(tasks);

        List<Task> loaded = storage.load();
        assertEquals(1, loaded.size());
        assertEquals(List.of("#fun", "#urgent"), loaded.get(0).getTags());
    }

    /** A line saved before tagging existed has no trailing tag field; it should still load, with no tags. */
    @Test
    void load_lineWithoutTagField_loadsWithNoTags() throws AmadeusException, IOException {
        Path file = tempDir.resolve("amadeus.txt");
        Files.writeString(file, "T | 0 | read book\n", StandardCharsets.UTF_8);
        Storage storage = new Storage(tempDir.toString(), "amadeus.txt");

        List<Task> loaded = storage.load();
        assertEquals(1, loaded.size());
        assertTrue(loaded.get(0).getTags().isEmpty());
    }
}
