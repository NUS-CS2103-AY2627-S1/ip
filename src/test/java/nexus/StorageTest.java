package nexus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests loading and saving the persistent task format. */
class StorageTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    void load_missingFile_returnsEmptyList() {
        assertTrue(new Storage(temporaryDirectory.resolve("missing.txt")).load().isEmpty());
    }

    @Test
    void saveThenLoad_preservesTasksAndStatus() {
        Path file = temporaryDirectory.resolve("nested").resolve("nexus.txt");
        Task completed = new Todo("done task");
        completed.markAsDone();
        new Storage(file).save(List.of(new Todo("open task"), completed));

        List<Task> loaded = new Storage(file).load();
        assertEquals(2, loaded.size());
        assertEquals("[T][ ] open task", loaded.get(0).toString());
        assertEquals("[T][X] done task", loaded.get(1).toString());
        assertTrue(Files.exists(file));
    }

    @Test
    void load_blankAndCorruptRecords_ignoresBlankAndRecoversEmpty() throws Exception {
        Path file = temporaryDirectory.resolve("nexus.txt");
        Files.writeString(file, "\n[T][ ] valid\n[bad] corrupt\n");

        assertTrue(new Storage(file).load().isEmpty());
    }
}
