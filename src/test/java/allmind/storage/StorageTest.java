package allmind.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import allmind.model.Deadline;
import allmind.model.Task;
import allmind.model.ToDo;
import allmind.parser.Parser;

/** Tests task file loading and saving. */
public class StorageTest {
    private final Parser parser = new Parser();

    @Test
    public void load_missingFile_createsEmptyFile(@TempDir Path directory) throws Exception {
        Path file = directory.resolve("tasks.txt");

        List<Task> tasks = new Storage(file.toString()).load(parser);

        assertEquals(List.of(), tasks);
        org.junit.jupiter.api.Assertions.assertTrue(Files.exists(file));
    }

    @Test
    public void saveThenLoad_tasksAndBlankLines_roundTrip(@TempDir Path directory) throws Exception {
        Path file = directory.resolve("tasks.txt");
        Storage storage = new Storage(file.toString());
        ToDo todo = new ToDo("read");
        Deadline deadline = new Deadline("submit", LocalDate.of(2026, 10, 20));

        storage.save(List.of(todo, deadline), parser);
        Files.writeString(file, Files.readString(file) + System.lineSeparator());

        List<Task> loaded = storage.load(parser);

        assertEquals(2, loaded.size());
        assertEquals("read", loaded.get(0).getDescription());
        assertEquals(LocalDate.of(2026, 10, 20), ((Deadline) loaded.get(1)).getBy());
    }
}
