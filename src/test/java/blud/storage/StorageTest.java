package blud.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import blud.exception.StorageException;
import blud.model.Deadline;
import blud.model.Event;
import blud.model.Task;
import blud.model.TaskList;
import blud.model.ToDo;

/** Tests persistence, reconstruction, and validation of stored tasks. */
public class StorageTest {
    @TempDir
    private Path temporaryDirectory;

    /** Verifies that all task types round-trip through storage. */
    @Test
    public void saveAndLoadTasks_allTaskTypes_preservesTasks() {
        Path taskFile = temporaryDirectory.resolve("nested/tasks.txt");
        Storage storage = new Storage(taskFile.toString());
        TaskList taskList = new TaskList(List.of(
                new ToDo(new String[] {"todo read"}),
                new Deadline(new String[] {"deadline report", "by 1/6/2026 0900"}),
                new Event(new String[] {"event meeting", "from 1/6/2026 0900", "to 1/6/2026 1000"})));

        storage.saveTasks(taskList);
        List<Task> loadedTasks = storage.loadTasks();

        assertEquals(taskList.getTaskList().toString(), loadedTasks.toString());
        assertEquals(3, loadedTasks.size());
        assertEquals("[T][ ] read", loadedTasks.get(0).toString());
        assertEquals("[D][ ] report (by: Jun 01 2026, 9:00 am)", loadedTasks.get(1).toString());
    }

    /** Verifies that completion status is preserved by storage. */
    @Test
    public void saveAndLoadTasks_completedTask_preservesStatus() {
        Path taskFile = temporaryDirectory.resolve("tasks.txt");
        Storage storage = new Storage(taskFile.toString());
        ToDo task = new ToDo(new String[] {"todo read"});
        task.mark();

        storage.saveTasks(new TaskList(List.of(task)));

        assertEquals("[T][X] read", storage.loadTasks().get(0).toString());
    }

    /** Verifies malformed stored records are reported as storage errors. */
    @Test
    public void loadTasks_malformedRecord_throwsStorageException() throws Exception {
        Path taskFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(taskFile, "D | 0 | report\n");

        StorageException exception = assertThrows(StorageException.class,
                () -> new Storage(taskFile.toString()).loadTasks());

        assertTrue(exception.getMessage().startsWith("Unable to load tasks:"));
    }

    /** Verifies duplicate stored records are rejected. */
    @Test
    public void loadTasks_duplicateRecords_throwsStorageException() throws Exception {
        Path taskFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(taskFile, "T | 0 | read\nT | 1 | read\n");

        assertThrows(StorageException.class, () -> new Storage(taskFile.toString()).loadTasks());
    }

    /** Verifies that an empty storage path is rejected. */
    @Test
    public void create_emptyPath_throwsStorageException() {
        assertThrows(StorageException.class, () -> new Storage(" "));
    }
}
