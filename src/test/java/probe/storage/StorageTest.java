package probe.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import probe.task.Deadline;
import probe.task.Event;
import probe.task.Task;
import probe.task.Todo;

class StorageTest {

    @TempDir
    private Path temporaryDirectory;

    @Test
    void loadReturnsEmptyListWhenFileDoesNotExist() {
        Storage storage = new Storage(
                temporaryDirectory.resolve("missing.txt").toString());

        List<Task> tasks = storage.load();

        assertTrue(tasks.isEmpty());
    }

    @Test
    void saveAndLoadPreservesTasks() {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Storage storage = new Storage(file.toString());

        Todo todo = new Todo("Read a book");
        todo.markAsDone();

        Deadline deadline = new Deadline(
                "Submit report",
                LocalDateTime.of(2026, 8, 28, 14, 30));

        Event event = new Event(
                "Team meeting",
                LocalDateTime.of(2026, 8, 28, 10, 0),
                LocalDateTime.of(2026, 8, 28, 11, 0));

        storage.save(List.of(todo, deadline, event));

        List<Task> loadedTasks = storage.load();

        assertEquals(3, loadedTasks.size());
        assertEquals("Read a book", loadedTasks.get(0).getDescription());
        assertEquals("Submit report", loadedTasks.get(1).getDescription());
        assertEquals("Team meeting", loadedTasks.get(2).getDescription());
        assertTrue(loadedTasks.get(0).getStatusIcon().equals("X"));
        assertFalse(loadedTasks.get(1).getStatusIcon().equals("X"));
    }

    @Test
    void saveAndLoadPreservesTags() {
        Path file = temporaryDirectory.resolve("tagged-tasks.txt");
        Storage storage = new Storage(file.toString());
        Todo todo = new Todo("Read a book");
        todo.addTag("fun", "weekend");

        storage.save(List.of(todo));

        List<Task> loadedTasks = storage.load();

        assertEquals("[T][ ] Read a book #fun #weekend", loadedTasks.get(0).toString());
    }

    @Test
    void loadSkipsMalformedLines() throws Exception {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(
                file,
                "T | 0 | Valid task\n"
                        + "INVALID DATA\n"
                        + "T | 1 | Completed task\n");

        Storage storage = new Storage(file.toString());

        List<Task> tasks = storage.load();

        assertEquals(2, tasks.size());
        assertEquals("Valid task", tasks.get(0).getDescription());
        assertEquals("Completed task", tasks.get(1).getDescription());
    }
}
