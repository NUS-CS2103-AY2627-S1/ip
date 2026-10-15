package dave.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import dave.task.Deadline;
import dave.task.Event;
import dave.task.Task;
import dave.task.Todo;

/**
 * Unit tests for the {@link Storage} class.
 */
public class StorageTest {

    @TempDir
    private Path tempFolder;

    @Test
    public void load_nonExistentFile_returnsEmptyList() {
        Path filePath = this.tempFolder.resolve("non_existent.txt");
        Storage storage = new Storage(filePath.toString());

        ArrayList<Task> tasks = storage.load();
        assertTrue(tasks.isEmpty());
    }

    @Test
    public void load_validFileWithTasks_returnsParsedTaskList() throws IOException {
        Path filePath = this.tempFolder.resolve("tasks.txt");
        List<String> lines = List.of(
                "T | 1 | borrow book",
                "D | 0 | return book | 2026-10-15 18:00",
                "E | 1 | orientation | 2026-08-01 | 2026-08-02",
                "INVALID | line | format"
        );
        Files.write(filePath, lines);

        Storage storage = new Storage(filePath.toString());
        ArrayList<Task> tasks = storage.load();

        assertEquals(3, tasks.size());

        assertTrue(tasks.get(0) instanceof Todo);
        assertEquals("borrow book", tasks.get(0).getDescription());
        assertTrue(tasks.get(0).isDone());

        assertTrue(tasks.get(1) instanceof Deadline);
        assertEquals("return book", tasks.get(1).getDescription());
        assertFalse(tasks.get(1).isDone());

        assertTrue(tasks.get(2) instanceof Event);
        assertEquals("orientation", tasks.get(2).getDescription());
        assertTrue(tasks.get(2).isDone());
    }

    @Test
    public void save_taskList_writesToFileSuccessfully() throws IOException {
        Path filePath = this.tempFolder.resolve("sub_dir").resolve("saved_tasks.txt");
        Storage storage = new Storage(filePath.toString());

        List<Task> tasks = new ArrayList<>();
        Todo todo = new Todo("buy groceries");
        todo.setDone(true);
        tasks.add(todo);

        storage.save(tasks);

        assertTrue(Files.exists(filePath));
        List<String> writtenLines = Files.readAllLines(filePath);
        assertEquals(1, writtenLines.size());
        assertEquals("T | 1 | buy groceries", writtenLines.get(0));
    }
}
