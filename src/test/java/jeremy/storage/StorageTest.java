package jeremy.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import jeremy.task.Deadline;
import jeremy.task.Event;
import jeremy.task.Task;
import jeremy.task.Todo;

/** Tests persistence behaviour without modifying the project's runtime data. */
public class StorageTest {

    @TempDir
    private Path temporaryDirectory;

    @Test
    public void saveThenLoad_preservesAllTaskTypesAndStatuses() throws Exception {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Storage storage = new Storage(file.toString());

        Todo todo = new Todo("read notes");
        Deadline deadline = new Deadline("submit report", "18/9/2026 5pm");
        Event event = new Event("project meeting", "18/9/2026 2pm", "4pm");
        deadline.markAsDone();

        storage.save(List.of(todo, deadline, event));
        List<Task> loadedTasks = storage.load();

        assertEquals(3, loadedTasks.size());
        assertEquals("read notes", loadedTasks.get(0).getDescription());
        assertFalse(loadedTasks.get(0).isDone());
        assertEquals("18/9/2026 5pm", ((Deadline) loadedTasks.get(1)).getBy());
        assertTrue(loadedTasks.get(1).isDone());
        assertEquals("18/9/2026 2pm", ((Event) loadedTasks.get(2)).getFrom());
        assertEquals("4pm", ((Event) loadedTasks.get(2)).getTo());
    }

    @Test
    public void load_missingFile_returnsEmptyList() throws Exception {
        Path file = temporaryDirectory.resolve("missing.txt");
        Storage storage = new Storage(file.toString());

        assertTrue(storage.load().isEmpty());
    }

    @Test
    public void load_malformedLines_skipsInvalidTasks() throws Exception {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(file, "T|0|valid task\nnot a task\nD|1|done task|tomorrow\n");
        Storage storage = new Storage(file.toString());

        List<Task> loadedTasks = storage.load();

        assertEquals(2, loadedTasks.size());
        assertEquals("valid task", loadedTasks.get(0).getDescription());
        assertEquals("done task", loadedTasks.get(1).getDescription());
        assertTrue(loadedTasks.get(1).isDone());
    }

    @Test
    public void save_createsMissingParentDirectories() throws IOException {
        Path file = temporaryDirectory.resolve("nested/data/tasks.txt");
        Storage storage = new Storage(file.toString());

        storage.save(List.of(new Todo("created safely")));

        assertTrue(Files.exists(file));
    }
}
