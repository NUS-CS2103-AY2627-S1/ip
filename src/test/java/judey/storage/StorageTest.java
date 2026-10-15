package judey.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import judey.task.Task;

class StorageTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void load_acceptsValidStatusValues() throws Exception {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Files.write(file, List.of("T | 0 | incomplete", "T | 1 | complete"));

        List<Task> tasks = new Storage(file.toString()).load();

        assertEquals(2, tasks.size());
        assertEquals(" ", tasks.get(0).getStatusIcon());
        assertEquals("X", tasks.get(1).getStatusIcon());
    }

    @Test
    void load_skipsInvalidStatusAndKeepsValidRecords() throws Exception {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Files.write(file, List.of("T | 2 | corrupted", "T | 0 | valid"));

        List<Task> tasks = new Storage(file.toString()).load();

        assertEquals(1, tasks.size());
        assertTrue(tasks.get(0).getDescription().equals("valid"));
    }

    @Test
    void load_skipsEventWithInvalidTimeRangeAndKeepsValidRecords() throws Exception {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Files.write(file, List.of(
                "E | 0 | invalid | 2026-09-26T15:00 | 2026-09-26T14:00",
                "E | 0 | valid | 2026-09-26T14:00 | 2026-09-26T15:00"));

        List<Task> tasks = new Storage(file.toString()).load();

        assertEquals(1, tasks.size());
        assertEquals("valid", tasks.get(0).getDescription());
    }
}
