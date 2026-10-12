package duke;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class StorageTest {

    @Test
    public void saveThenLoad_roundTripsTasks(@TempDir Path tempDir) throws IOException, DuckeException {
        Storage storage = new Storage(tempDir.resolve("tasks.txt").toString());

        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        Task deadline = Deadline.of("return book /by 2019-10-15");
        deadline.markDone();
        tasks.add(deadline);

        storage.save(tasks);
        ArrayList<Task> loaded = storage.load();

        assertEquals(2, loaded.size());
        assertEquals("[T][ ] read book", loaded.get(0).toString());
        assertEquals("[D][X] return book (by: Oct 15 2019)", loaded.get(1).toString());
    }

    @Test
    public void load_missingFile_returnsEmptyList(@TempDir Path tempDir) throws IOException, DuckeException {
        Storage storage = new Storage(tempDir.resolve("does-not-exist.txt").toString());
        assertEquals(0, storage.load().size());
    }
}
