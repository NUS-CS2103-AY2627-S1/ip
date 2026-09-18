package alpha;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests command execution when task data is missing or cannot be read. */
class TaskManagerTest {
    @TempDir
    Path temporaryDirectory;

    /** Verifies that a missing data file is created after the first successful task command. */
    @Test
    void startsWithEmptyTasksWhenDataFileIsMissing_thenCreatesFileAfterFirstTask() throws AlphaException {
        Path file = this.temporaryDirectory.resolve("nested").resolve("tasks.txt");
        TaskManager taskManager = new TaskManager(file);

        assertTrue(taskManager.getTasks().isEmpty());
        taskManager.execute("todo water the plants");

        assertTrue(Files.exists(file));
        assertFalse(taskManager.getTasks().isEmpty());
    }

    /** Verifies that an unreadable data path becomes a warning and does not stop command processing. */
    @Test
    void continuesWithEmptyTasksWhenDataPathCannotBeRead() throws Exception {
        Path directory = this.temporaryDirectory.resolve("tasks");
        Files.createDirectory(directory);
        TaskManager taskManager = new TaskManager(directory);

        assertNotNull(taskManager.getLoadingWarning());
        assertTrue(taskManager.getTasks().isEmpty());
        assertTrue(taskManager.execute("list").getMessage().isEmpty());
    }
}
