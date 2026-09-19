package anniechat.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import anniechat.task.Task;
import anniechat.task.ToDo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests saving tasks to the file system through {@link Storage}. */
public class StorageTest {
    /** Temporary directory provided by JUnit for each test. */
    @TempDir
    Path temporaryDirectory;

    @Test
    public void save_tasks_writesExpectedLines() throws IOException {
        Path filePath = temporaryDirectory.resolve("test-tasks.txt");
        Storage storage = new Storage(filePath.toString());
        List<Task> tasks = List.of(new ToDo("todo read book"));

        storage.save(tasks);

        List<String> actualLines = Files.readAllLines(filePath);
        List<String> expectedLines = List.of("T | 0 | read book");

        assertEquals(expectedLines, actualLines);
    }
}
