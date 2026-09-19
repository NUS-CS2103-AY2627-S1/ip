package yawned.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import yawned.task.Deadline;
import yawned.task.Event;
import yawned.task.Task;
import yawned.task.TaskStatus;
import yawned.task.ToDo;

/** Tests task persistence using an isolated temporary directory. */
class StorageTest {

    @TempDir
    Path temporaryDirectory;

    @Test
    void loadTasks_missingFile_returnsEmptyList() {
        Storage storage = new Storage(temporaryDirectory.resolve("data").resolve("Yawned.txt"));

        assertTrue(storage.loadTasks().isEmpty());
    }

    @Test
    void saveAndLoadTasks_allTaskTypesAndEscapedDescription_preservesData() {
        ToDo todo = new ToDo("read | book \\ notes");
        todo.markAsDone();
        Deadline deadline = new Deadline("submit report", LocalDateTime.of(2026, 1, 1, 9, 0));
        Event event = new Event("project meeting", LocalDateTime.of(2026, 1, 2, 15, 0),
                LocalDateTime.of(2026, 1, 2, 16, 0));
        Storage storage = new Storage(temporaryDirectory.resolve("data").resolve("Yawned.txt"));

        storage.saveTasks(List.of(todo, deadline, event));
        List<Task> loadedTasks = storage.loadTasks();

        assertEquals(3, loadedTasks.size());
        assertEquals("read | book \\ notes", loadedTasks.get(0).getDescription());
        assertEquals(TaskStatus.DONE, loadedTasks.get(0).getStatus());
        Deadline loadedDeadline = (Deadline) loadedTasks.get(1);
        assertEquals("submit report", loadedDeadline.getDescription());
        assertEquals(LocalDateTime.of(2026, 1, 1, 9, 0), loadedDeadline.getEndDate());
        Event loadedEvent = (Event) loadedTasks.get(2);
        assertEquals("project meeting", loadedEvent.getDescription());
        assertEquals(LocalDateTime.of(2026, 1, 2, 15, 0), loadedEvent.getStartDate());
        assertEquals(LocalDateTime.of(2026, 1, 2, 16, 0), loadedEvent.getEndDate());
    }
}
