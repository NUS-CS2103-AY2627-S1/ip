package alice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class StorageTest {

    @Test
    void saveAndLoad_withToDos_works(@TempDir Path tempDir) throws IOException {
        Path filePath = tempDir.resolve("duke.txt");
        Storage storage = new Storage(filePath.toString());

        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new ToDos("read book"));

        storage.save(tasks);
        ArrayList<Task> loaded = storage.load();

        assertEquals(1, loaded.size());
        assertTrue(loaded.getFirst() instanceof ToDos);
        assertEquals("read book", loaded.getFirst().getDescription());
        assertFalse(loaded.getFirst().isDone());
    }

    @Test
    void saveAndLoad_withDeadlines_works(@TempDir Path tempDir) throws IOException {
        Path filePath = tempDir.resolve("duke.txt");
        Storage storage = new Storage(filePath.toString());

        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new Deadlines("return book", LocalDate.parse("2024-12-25")));

        storage.save(tasks);
        ArrayList<Task> loaded = storage.load();

        assertEquals(1, loaded.size());
        assertTrue(loaded.getFirst() instanceof Deadlines);
        Deadlines d = (Deadlines) loaded.getFirst();
        assertEquals("return book", d.getDescription());
        assertEquals(LocalDate.parse("2024-12-25"), d.getBy());
        assertFalse(d.isDone());
    }

    @Test
    void saveAndLoad_withEvents_works(@TempDir Path tempDir) throws IOException {
        Path filePath = tempDir.resolve("duke.txt");
        Storage storage = new Storage(filePath.toString());

        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new Events("conference", LocalDate.parse("2024-12-20"), LocalDate.parse("2024-12-22")));

        storage.save(tasks);
        ArrayList<Task> loaded = storage.load();

        assertEquals(1, loaded.size());
        assertTrue(loaded.getFirst() instanceof Events);
        Events e = (Events) loaded.getFirst();
        assertEquals("conference", e.getDescription());
        assertEquals(LocalDate.parse("2024-12-20"), e.getFrom());
        assertEquals(LocalDate.parse("2024-12-22"), e.getTo());
        assertFalse(e.isDone());
    }

    @Test
    void saveAndLoad_withDoneStatus_works(@TempDir Path tempDir) throws IOException {
        Path filePath = tempDir.resolve("duke.txt");
        Storage storage = new Storage(filePath.toString());

        ArrayList<Task> tasks = new ArrayList<>();
        ToDos todo = new ToDos("read book");
        todo.toggleStatus();
        tasks.add(todo);

        storage.save(tasks);
        ArrayList<Task> loaded = storage.load();

        assertEquals(1, loaded.size());
        assertTrue(loaded.getFirst().isDone());
    }

    @Test
    void saveAndLoad_multipleTasks_works(@TempDir Path tempDir) throws IOException {
        Path filePath = tempDir.resolve("duke.txt");
        Storage storage = new Storage(filePath.toString());

        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new ToDos("read book"));
        tasks.add(new Deadlines("return book", LocalDate.parse("2024-12-25")));
        tasks.add(new Events("conference", LocalDate.parse("2024-12-20"), LocalDate.parse("2024-12-22")));

        storage.save(tasks);
        ArrayList<Task> loaded = storage.load();

        assertEquals(3, loaded.size());
        assertTrue(loaded.get(0) instanceof ToDos);
        assertTrue(loaded.get(1) instanceof Deadlines);
        assertTrue(loaded.get(2) instanceof Events);
    }

    @Test
    void load_withNonExistentFile_returnsEmptyList(@TempDir Path tempDir) throws IOException {
        Path filePath = tempDir.resolve("non_existent.txt");
        Storage storage = new Storage(filePath.toString());

        ArrayList<Task> loaded = storage.load();
        assertTrue(loaded.isEmpty());
    }

    @Test
    void load_withCorruptedLine_skipsInvalidLine(@TempDir Path tempDir) throws IOException {
        Path filePath = tempDir.resolve("duke.txt");
        Storage storage = new Storage(filePath.toString());

        // Save a valid task, then manually inject a corrupted line
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new ToDos("read book"));
        storage.save(tasks);

        // Manually append a corrupted line
        java.nio.file.Files.write(filePath, "CORRUPTED_LINE".getBytes(),
                java.nio.file.StandardOpenOption.APPEND);

        ArrayList<Task> loaded = storage.load();
        assertEquals(1, loaded.size());
        assertEquals("read book", loaded.getFirst().getDescription());
    }
}
