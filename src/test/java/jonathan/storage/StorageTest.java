package jonathan.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import jonathan.task.Task;
import jonathan.task.ToDo;

public class StorageTest {

    @TempDir
    Path temporaryDirectory;

    @Test
    public void saveThenLoad_preservesTaskData() throws Exception {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Storage storage = new Storage(file.toString());

        Task task = new ToDo("read book");
        storage.save(new Task[] {task}, 1);

        assertEquals(List.of("T | 0 | read book"), storage.load());
    }

    @Test
    public void loadMissingFile_returnsEmptyList() throws Exception {
        Path file = temporaryDirectory.resolve("missing.txt");
        Storage storage = new Storage(file.toString());

        assertEquals(List.of(), storage.load());
    }
}
