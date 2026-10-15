package bingusdingus.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Tests task storage serialization, loading, and malformed-record handling. */
class TaskStorageTest {
    private static final Path STORAGE_PATH = Path.of(".", "data", "bingusdingus.txt");
    private byte[] originalStorage;
    private boolean storageExisted;

    @BeforeEach
    void isolateStorage() throws IOException {
        storageExisted = Files.exists(STORAGE_PATH);
        originalStorage = storageExisted ? Files.readAllBytes(STORAGE_PATH) : null;
        Files.deleteIfExists(STORAGE_PATH);
    }

    @AfterEach
    void restoreStorage() throws IOException {
        if (storageExisted) {
            Files.createDirectories(STORAGE_PATH.getParent());
            Files.write(STORAGE_PATH, originalStorage);
        } else {
            Files.deleteIfExists(STORAGE_PATH);
        }
    }

    @Test
    void save_replacesExistingContentsAndPreservesTaskFormats() throws IOException {
        TaskStorage storage = new TaskStorage();
        storage.save(List.of(new Todo("first"),
                new Deadline("second", LocalDateTime.of(2026, 9, 14, 0, 0)),
                new Event("third", LocalDateTime.of(2026, 9, 14, 9, 0),
                        LocalDateTime.of(2026, 9, 14, 10, 0))));

        storage.save(List.of(new Todo("replacement")));

        assertEquals(List.of("T | 0 | replacement"),
                Files.readAllLines(STORAGE_PATH, StandardCharsets.UTF_8));
    }

    @Test
    void load_missingOrEmptyFile_returnsEmptyList() throws IOException {
        TaskStorage storage = new TaskStorage();
        assertEquals(List.of(), storage.load());

        Files.createDirectories(STORAGE_PATH.getParent());
        Files.writeString(STORAGE_PATH, "", StandardCharsets.UTF_8);
        assertEquals(List.of(), storage.load());
    }

    @Test
    void load_acceptsWhitespaceAndBomAroundValidRecords() throws IOException {
        Files.createDirectories(STORAGE_PATH.getParent());
        Files.write(STORAGE_PATH, List.of(
                "\uFEFFT | 1 | todo",
                " D | 0 | deadline | 2026-09-14T00:00 ",
                " E | 1 | event | 2026-09-14T09:00 | 2026-09-14T10:00 "),
                StandardCharsets.UTF_8);

        List<Task> tasks = new TaskStorage().load();

        assertEquals(3, tasks.size());
        assertTrue(tasks.get(0).isDone());
        assertFalse(tasks.get(1).isDone());
        assertTrue(tasks.get(2).isDone());
    }

    @Test
    void load_ignoresBlankAndMalformedRecordsButKeepsValidRecords() throws IOException {
        Files.createDirectories(STORAGE_PATH.getParent());
        Files.write(STORAGE_PATH, List.of(
                "",
                "T | 2 | invalid status",
                "T | 0 | ",
                "D | 0 | missing date |",
                "D | 0 | invalid date | not-a-date",
                "E | 0 | backwards | 2026-09-14T10:00 | 2026-09-14T09:00",
                "X | 0 | unknown",
                "T | 0 | valid"), StandardCharsets.UTF_8);

        List<Task> tasks = new TaskStorage().load();

        assertEquals(1, tasks.size());
        assertEquals("valid", tasks.get(0).getDescription());
    }
}
