package thomas.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import thomas.task.Deadline;
import thomas.task.Event;
import thomas.task.Task;
import thomas.task.TaskList;
import thomas.task.Todo;

class StorageTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void saveAndLoad_mixedTasks_preservesTypesDataAndStatus() throws Exception {
        Storage storage = new Storage(temporaryDirectory.toString(), "tasks.txt");
        ArrayList<Task> original = new ArrayList<>();
        original.add(new Todo("read book"));
        Deadline deadline = new Deadline("submit report", "2026-08-26 1800");
        deadline.markAsDone();
        original.add(deadline);
        original.add(new Event("meeting", "2026-08-26 0900", "2026-08-26 1030"));

        storage.save(original);
        ArrayList<Task> loaded = storage.load();

        assertEquals(3, loaded.size());
        assertInstanceOf(Todo.class, loaded.get(0));
        assertEquals("read book", loaded.get(0).getDescription());
        assertInstanceOf(Deadline.class, loaded.get(1));
        assertTrue(loaded.get(1).isDone());
        assertEquals("2026-08-26", ((Deadline) loaded.get(1)).getByDate().toString());
        assertInstanceOf(Event.class, loaded.get(2));
        assertFalse(loaded.get(2).isDone());
        assertEquals("2026-08-26", ((Event) loaded.get(2)).getFromDate().toString());
    }

    @Test
    void load_missingFile_createsFileAndReturnsEmptyList() throws Exception {
        Path file = temporaryDirectory.resolve("nested").resolve("tasks.txt");
        Storage storage = new Storage(file.getParent().toString(), file.getFileName().toString());

        ArrayList<Task> loaded = storage.load();

        assertTrue(loaded.isEmpty());
        assertTrue(Files.exists(file));
    }

    @Test
    void load_blankAndMalformedLines_skipsInvalidRecords() throws Exception {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(file, "\nT | N | valid\ninvalid line\nT | Y | done\n");
        Storage storage = new Storage(temporaryDirectory.toString(), "tasks.txt");

        ArrayList<Task> loaded = storage.load();

        assertEquals(2, loaded.size());
        assertEquals("valid", loaded.get(0).getDescription());
        assertTrue(loaded.get(1).isDone());
    }

    @Test
    void load_invalidStatusAndExtraFields_skipsInvalidRecords() throws Exception {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(file, "T | maybe | invalid\nT | N | extra | data\nT | N | valid\n");
        Storage storage = new Storage(temporaryDirectory.toString(), "tasks.txt");

        ArrayList<Task> loaded = storage.load();

        assertEquals(1, loaded.size());
        assertEquals("valid", loaded.get(0).getDescription());
    }

    @Test
    void save_taskListOverload_persistsTasks() {
        Storage storage = new Storage(temporaryDirectory.toString(), "tasks.txt");
        TaskList taskList = new TaskList();
        taskList.add(new Todo("read book"));

        storage.save(taskList);

        assertEquals(1, storage.load().size());
        assertEquals("read book", storage.load().get(0).getDescription());
    }
}
