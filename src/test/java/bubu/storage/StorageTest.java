package bubu.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import bubu.task.Deadline;
import bubu.task.Event;
import bubu.task.Task;
import bubu.task.ToDo;

/** Tests task persistence using isolated temporary files. */
class StorageTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void saveTasks_thenLoad_returnsEquivalentTasks() {
        Storage storage = new Storage(temporaryDirectory.resolve("nested/bubu.txt"));
        LocalDateTime deadline = LocalDateTime.of(2026, 9, 18, 18, 30);
        LocalDateTime start = LocalDateTime.of(2026, 9, 19, 10, 0);
        LocalDateTime end = LocalDateTime.of(2026, 9, 19, 11, 0);
        List<Task> tasks = List.of(new ToDo("read notes"),
                new Deadline("submit report", deadline), new Event("team meeting", start, end));

        storage.saveTasks(tasks);
        List<Task> loadedTasks = storage.load();

        assertEquals(tasks.toString(), loadedTasks.toString());
    }

    @Test
    void load_missingFile_returnsEmptyList() {
        Storage storage = new Storage(temporaryDirectory.resolve("missing.txt"));

        assertEquals(List.of(), storage.load());
    }

    @Test
    void load_malformedRecord_throwsIllegalArgumentException() throws Exception {
        Path file = temporaryDirectory.resolve("bubu.txt");
        Files.writeString(file, "D | 0 | missing time\n");
        Storage storage = new Storage(file);

        assertThrows(IllegalArgumentException.class, storage::load);
    }

    @Test
    void load_invalidEventRange_throwsIllegalArgumentException() throws Exception {
        Path file = temporaryDirectory.resolve("bubu.txt");
        Files.writeString(file, "E | 0 | meeting | 2026-09-19 1100 | 2026-09-19 1000\n");
        Storage storage = new Storage(file);

        assertThrows(IllegalArgumentException.class, storage::load);
    }

    @Test
    void saveTasks_unsupportedTaskType_throwsIllegalArgumentException() {
        Storage storage = new Storage(temporaryDirectory.resolve("bubu.txt"));
        Task unsupportedTask = new Task("unsupported") { };

        assertThrows(IllegalArgumentException.class, () ->
                storage.saveTasks(List.of(unsupportedTask)));
    }
}
