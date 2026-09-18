package alpha.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import alpha.task.Deadline;
import alpha.task.Event;
import alpha.task.Task;
import alpha.task.TaskList;
import alpha.task.Todo;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests task-file creation, serialization, and restoration. */
class StorageTest {
    @TempDir
    Path temporaryDirectory;

    /** Verifies directory creation and round-trip preservation of all task types. */
    @Test
    void createsMissingParentDirectoriesAndLoadsAllTaskTypes() throws StorageException {
        Path file = this.temporaryDirectory.resolve("nested").resolve("tasks.txt");
        Storage storage = new Storage(file);
        TaskList original = new TaskList();
        original.addTask(new Todo("read | book"));
        original.addTask(new Deadline("return book", LocalDateTime.of(2019, 12, 2, 18, 0)));
        original.addTask(new Event("project meeting", LocalDateTime.of(2019, 10, 15, 14, 0),
                LocalDateTime.of(2019, 10, 15, 16, 0)));
        original.markDone(2);

        storage.save(original);

        assertTrue(Files.exists(file));
        List<Task> loaded = storage.load();
        assertEquals(3, loaded.size());
        assertEquals("read | book", loaded.get(0).getDescription());

        Deadline deadline = assertInstanceOf(Deadline.class, loaded.get(1));
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), deadline.getBy());
        assertTrue(deadline.isDone());

        Event event = assertInstanceOf(Event.class, loaded.get(2));
        assertEquals(LocalDateTime.of(2019, 10, 15, 14, 0), event.getFrom());
        assertEquals(LocalDateTime.of(2019, 10, 15, 16, 0), event.getTo());
        assertFalse(event.isDone());
    }

    /** Verifies that a first run with no storage file starts with no tasks. */
    @Test
    void treatsMissingFileAsEmptyTaskCollection() throws StorageException {
        Storage storage = new Storage(this.temporaryDirectory.resolve("missing").resolve("tasks.txt"));

        assertTrue(storage.load().isEmpty());
    }
}
