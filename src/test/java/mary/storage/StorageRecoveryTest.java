package mary.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import mary.exception.MaryException;
import mary.task.Event;
import mary.task.Todo;

/**
 * Checks damaged-file protection, safe replacement, and environment failures.
 */
class StorageRecoveryTest {
    @TempDir
    Path directory;

    /**
     * Creates absent folders and replaces records without leaving temporary files.
     */
    @Test
    void save_missingParents_createsFoldersAndReplacesFile() throws Exception {
        Path file = directory.resolve("nested/data/tasks.txt");
        Storage storage = new Storage(file.toString());
        assertTrue(storage.load().isEmpty());
        storage.save(List.of(new Todo("first")));
        storage.save(List.of(new Todo("second")));
        assertEquals(List.of("T | 0 | second"), Files.readAllLines(file));
        try (var files = Files.list(file.getParent())) {
            assertEquals(List.of(file), files.toList());
        }
    }

    /**
     * Refuses writes after bad UTF-8 or corrupt records until a successful reload.
     */
    @Test
    void load_corruption_blocksOverwriteAndAllowsRecovery() throws Exception {
        Path file = directory.resolve("tasks.txt");
        Storage storage = new Storage(file.toString());
        Files.write(file, new byte[] {(byte) 0xc3, (byte) 0x28});
        assertThrows(MaryException.class, storage::load);
        assertTrue(assertThrows(MaryException.class, () -> storage.save(List.of(new Todo("new"))))
                .getMessage().contains("saving is disabled"));
        assertEquals(2, Files.size(file));
        Files.writeString(file, "T | 0 | repaired\n");
        assertEquals(1, storage.load().size());
        storage.save(List.of(new Todo("new")));
        assertEquals(List.of("T | 0 | new"), Files.readAllLines(file));
    }

    /**
     * Rejects reversed and zero-duration saved events without overwriting the file.
     */
    @Test
    void load_invalidEventRange_preservesOriginalData() throws Exception {
        Path file = directory.resolve("tasks.txt");
        for (String end : List.of("2026-01-01T12:00", "2025-12-31T12:00")) {
            String record = "E | 0 | camp | 2026-01-01T12:00 | " + end;
            Files.writeString(file, record);
            Storage storage = new Storage(file.toString());
            assertTrue(assertThrows(MaryException.class, storage::load).getMessage().contains("after its start"));
            assertThrows(MaryException.class, () -> storage.save(List.of()));
            assertEquals(record, Files.readString(file));
        }
    }

    /**
     * Invalid records from non-parser callers cannot truncate an existing valid file.
     */
    @Test
    void save_invalidRecord_preservesPreviouslySavedTasks() throws Exception {
        Path file = directory.resolve("tasks.txt");
        Storage storage = new Storage(file.toString());
        storage.save(List.of(new Todo("keep")));
        LocalDateTime time = LocalDateTime.of(2026, 1, 1, 12, 0);
        assertThrows(MaryException.class, () -> storage.save(List.of(new Todo("a\nb"))));
        assertThrows(MaryException.class, () -> storage.save(List.of(new Todo("a | b"))));
        assertThrows(MaryException.class, () -> storage.save(List.of(new Event("bad", time, time))));
        assertEquals(List.of("T | 0 | keep"), Files.readAllLines(file));
    }

    /**
     * Invalid paths and a file where a folder is needed are reported as application errors.
     */
    @Test
    void save_invalidPathOrParent_reportsRecoverableError() throws Exception {
        Storage invalid = new Storage("bad\u0000path");
        assertTrue(assertThrows(MaryException.class, invalid::load).getMessage().contains("invalid data-file path"));
        assertThrows(MaryException.class, () -> invalid.save(List.of()));
        Path parent = directory.resolve("not-a-folder");
        Files.writeString(parent, "keep");
        Path file = parent.resolve("tasks.txt");
        assertThrows(MaryException.class, () -> new Storage(file.toString()).save(List.of(new Todo("new"))));
        assertEquals("keep", Files.readString(parent));
        assertFalse(Files.exists(file));
    }
}
