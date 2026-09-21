package topaz.storage;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import topaz.TopazException;
import topaz.task.Deadline;
import topaz.task.Event;
import topaz.task.FixedDurationTask;
import topaz.task.Task;
import topaz.task.Todo;

/** Tests persistence, reconstruction, and invalid save-file handling. */
class StorageTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void load_missingFile_returnsEmptyList() throws TopazException {
        Storage storage = new Storage(temporaryDirectory.resolve("missing.txt"));

        assertTrue(storage.load().isEmpty());
    }

    @Test
    void saveAndLoad_allTaskTypes_preservesDetailsAndStatus() throws TopazException {
        Storage storage = new Storage(temporaryDirectory.resolve("data").resolve("Topaz.txt"));
        Task todo = new Todo("read book");
        Task deadline = new Deadline("return book", LocalDateTime.of(2019, 12, 2, 18, 0), true);
        Task event = new Event("project meeting", LocalDateTime.of(2019, 10, 15, 0, 0),
                LocalDateTime.of(2019, 10, 16, 0, 0), false, false);
        Task fixedDurationTask = new FixedDurationTask("read report", 120);
        deadline.markAsDone();

        storage.save(List.of(todo, deadline, event, fixedDurationTask));
        List<Task> loaded = storage.load();

        assertEquals(4, loaded.size());
        assertEquals("read book", loaded.get(0).getDescription());
        assertFalse(loaded.get(0).isDone());
        assertEquals("return book (by: Dec 02 2019 1800)", loaded.get(1).getDescription());
        assertTrue(loaded.get(1).isDone());
        assertEquals("project meeting (from: Oct 15 2019 to: Oct 16 2019)",
                loaded.get(2).getDescription());
        assertEquals("read report (for: 2 hours)", loaded.get(3).getDescription());
    }

    @Test
    void load_malformedRecord_throwsException() throws IOException {
        Path file = temporaryDirectory.resolve("malformed.txt");
        Files.writeString(file, "X | 0 | invalid task\n");

        assertThrows(TopazException.class, () -> new Storage(file).load());
    }

    @Test
    void load_invalidStatus_throwsException() throws IOException {
        Path file = temporaryDirectory.resolve("invalid-status.txt");
        Files.writeString(file, "T | 2 | read book\n");

        assertThrows(TopazException.class, () -> new Storage(file).load());
    }

    @Test
    void load_invalidFixedDuration_throwsException() throws IOException {
        Path signedDurationFile = temporaryDirectory.resolve("signed-fixed-duration.txt");
        Path zeroDurationFile = temporaryDirectory.resolve("zero-fixed-duration.txt");
        Files.writeString(signedDurationFile, "F | 0 | read report | +120\n");
        Files.writeString(zeroDurationFile, "F | 0 | read report | 0\n");

        assertThrows(TopazException.class, () -> new Storage(signedDurationFile).load());
        assertThrows(TopazException.class, () -> new Storage(zeroDurationFile).load());
    }
    @Test
    void load_duplicateOrInvalidPeriod_rejectsFileWithoutChangingIt() throws IOException {
        Path file = temporaryDirectory.resolve("bad-data.txt");
        for (String content : new String[] {"T | 0 | read book\nT | 1 | READ BOOK\n",
                "E | 0 | meeting | 2026-12-07 | 2026-12-07\n",
                "E | 0 | meeting | 2026-12-08 | 2026-12-07\n",
                "D | 0 | leap day | 2026-02-29\n"}) {
            Files.writeString(file, content);
            assertThrows(TopazException.class, () -> new Storage(file).load());
            assertEquals(content, Files.readString(file));
        }
    }

    @Test
    void load_corruptRecord_reportsLineAndPreservesFile() throws IOException {
        Path file = temporaryDirectory.resolve("corrupt.txt");
        String content = "T | 0 | valid\n\nX | 0 | corrupt\n";
        Files.writeString(file, content);
        TopazException error = assertThrows(TopazException.class, () -> new Storage(file).load());
        assertTrue(error.getMessage().contains("line 3"));
        assertEquals(content, Files.readString(file));
    }

    @Test
    void load_malformedUtf8_rejectsInsteadOfReplacingCharacters() throws IOException {
        Path file = temporaryDirectory.resolve("encoding.txt");
        byte[] bytes = {(byte) 0xc3, 0x28};
        Files.write(file, bytes);
        TopazException error = assertThrows(TopazException.class, () -> new Storage(file).load());
        assertTrue(error.getMessage().contains("UTF-8"));
        assertArrayEquals(bytes, Files.readAllBytes(file));
    }

    @Test
    void load_bomAndBlankLines_acceptsValidText() throws Exception {
        Path file = temporaryDirectory.resolve("bom.txt");
        Files.writeString(file, "\ufeffT | 0 | read book\n\n");
        assertEquals("read book", new Storage(file).load().get(0).getDescription());
    }

    @Test
    void save_existingFile_replacesCompletelyAndCleansStagingFiles() throws Exception {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Storage storage = new Storage(file);
        storage.save(List.of(new Todo("original"), new Todo("another")));
        storage.save(List.of(new Todo("replacement")));
        assertEquals(List.of("T | 0 | replacement"), Files.readAllLines(file));
        try (var files = Files.list(temporaryDirectory)) {
            assertEquals(List.of(file), files.toList());
        }
    }

    @Test
    void save_readOnlyFile_keepsOriginalContents() throws Exception {
        Path file = temporaryDirectory.resolve("protected.txt");
        Files.writeString(file, "T | 0 | original\n");
        assumeTrue(file.toFile().setReadOnly());
        try {
            assumeTrue(!Files.isWritable(file), "This account can override read-only permissions.");
            TopazException error = assertThrows(TopazException.class,
                    () -> new Storage(file).save(List.of(new Todo("replacement"))));
            assertTrue(error.getMessage().contains("access denied"));
            assertEquals("T | 0 | original\n", Files.readString(file));
        } finally {
            assertTrue(file.toFile().setWritable(true));
        }
    }

    @Test
    void save_blockedDirectory_reportsFailureWithoutChangingBlocker() throws IOException {
        Path blocker = temporaryDirectory.resolve("blocker");
        Files.writeString(blocker, "keep this file");
        Storage storage = new Storage(blocker.resolve("tasks.txt"));
        assertThrows(TopazException.class, () -> storage.save(List.of(new Todo("task"))));
        assertThrows(TopazException.class, storage::load);
        assertEquals("keep this file", Files.readString(blocker));
    }

    @Test
    void save_directoryTarget_reportsFailureWithoutReplacingDirectory() throws IOException {
        Path folder = temporaryDirectory.resolve("folder");
        Files.createDirectory(folder);
        assertThrows(TopazException.class, () -> new Storage(folder).save(List.of(new Todo("task"))));
        assertTrue(Files.isDirectory(folder));
    }

    @Test
    void save_encodingFailure_keepsOriginalAndRemovesStagingFile() throws Exception {
        Path file = temporaryDirectory.resolve("original.txt");
        String original = "T | 0 | original\n";
        Files.writeString(file, original);
        List<Task> tasks = List.of(new Todo("first staged record"), new Todo("invalid surrogate \ud800"));
        assertThrows(TopazException.class, () -> new Storage(file).save(tasks));
        assertEquals(original, Files.readString(file));
        try (var files = Files.list(temporaryDirectory)) {
            assertEquals(List.of(file), files.toList());
        }
    }

}
