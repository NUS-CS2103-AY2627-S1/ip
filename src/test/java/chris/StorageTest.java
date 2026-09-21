package chris;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests saving and loading task data. */
public class StorageTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    public void saveAndLoad_multipleTaskTypes_preservesTaskData() throws ChrisException {
        Storage storage = new Storage(temporaryDirectory.resolve("data/tasks.txt").toString());
        ArrayList<Task> tasks = new ArrayList<>();
        Todo todo = new Todo("read book");
        todo.markAsDone();
        tasks.add(todo);
        tasks.add(new Deadline("return book", "2026-12-02 1800"));

        storage.save(tasks);
        ArrayList<Task> loadedTasks = storage.load();

        assertEquals(2, loadedTasks.size());
        assertEquals(tasks.get(0).toString(), loadedTasks.get(0).toString());
        assertEquals(tasks.get(1).toString(), loadedTasks.get(1).toString());
    }

    @Test
    public void load_missingFile_returnsEmptyList() throws ChrisException {
        Storage storage = new Storage(temporaryDirectory.resolve("missing/tasks.txt").toString());

        assertTrue(storage.load().isEmpty());
    }

    @Test
    public void load_invalidStatus_reportsLineAndDoesNotRewriteData() throws java.io.IOException {
        Path file = temporaryDirectory.resolve("data/tasks.txt");
        java.nio.file.Files.createDirectories(file.getParent());
        java.nio.file.Files.writeString(file, "T | 0 | good\nT | 2 | bad\n");
        Storage storage = new Storage(file.toString());

        ChrisException error = assertThrows(ChrisException.class, storage::load);

        assertTrue(error.getMessage().contains("line 2"));
        assertEquals("T | 0 | good\nT | 2 | bad\n", java.nio.file.Files.readString(file));
    }

    @Test
    public void save_replacesDataAndLeavesNoTemporaryFiles() throws Exception {
        Path file = temporaryDirectory.resolve("data/tasks.txt");
        Storage storage = new Storage(file.toString());
        storage.save(java.util.List.of(new Todo("first")));
        storage.save(java.util.List.of(new Todo("second")));

        assertEquals(List.of("T | 0 | second"), java.nio.file.Files.readAllLines(file));
        try (var files = java.nio.file.Files.list(file.getParent())) {
            assertEquals(1, files.count());
        }
    }
}
