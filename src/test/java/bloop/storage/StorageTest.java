package bloop.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import bloop.exception.DukeException;
import bloop.task.Priority;
import bloop.task.Task;
import bloop.task.TaskList;
import bloop.task.Todo;

/** Tests priority persistence and compatibility with older task data. */
public class StorageTest {
    @TempDir
    private Path temporaryDirectory;

    /** Verifies that saved version-three data retains its priority when loaded. */
    @Test
    public void saveAndLoad_highPriorityTask_retainsPriority() throws DukeException, IOException {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Storage storage = new Storage(dataFile);
        storage.save(new TaskList(List.of(new Todo("read book", Priority.HIGH))));

        List<Task> loadedTasks = storage.load();

        assertEquals("V3 | T | 0 | high | cmVhZCBib29r", Files.readString(dataFile).trim());
        assertEquals(Priority.HIGH, loadedTasks.get(0).getPriority());
    }

    /** Verifies that legacy and version-two data receive the default low priority. */
    @Test
    public void load_olderTaskRecords_assignsLowPriority() throws IOException, DukeException {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(dataFile, "T | 0 | legacy task\nV2 | T | 0 | dmVyc2lvbiB0d28=");
        Storage storage = new Storage(dataFile);

        List<Task> loadedTasks = storage.load();

        assertEquals(Priority.LOW, loadedTasks.get(0).getPriority());
        assertEquals(Priority.LOW, loadedTasks.get(1).getPriority());
    }
}
