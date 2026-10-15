package olaf;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests preservation of saved snapshots when loading, writing, replacement, or cleanup fails. */
class StorageTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    void load_missingFile_emptyListWithoutCreatingFile() throws StorageException {
        Path path = temporaryDirectory.resolve("data/olaf.txt");

        assertEquals(0, new Storage(path).load().size());
        assertFalse(Files.exists(path));
    }

    @Test
    void save_nestedPathAndEscapedUnicodeTasks_roundTripPreserved() throws Exception {
        Path path = temporaryDirectory.resolve("data/olaf.txt");
        LocalDate date = LocalDate.of(2028, 2, 29);
        TaskList tasks = new TaskList(List.of(new Todo("read \u96ea | notes \\ chapter"),
                new Deadline("return book", date), new Event("meeting", date, date)));
        tasks.markAsDone(2);
        Storage storage = new Storage(path);

        storage.save(tasks);

        assertEquals(ResponseFormatter.formatTaskList(tasks), ResponseFormatter.formatTaskList(storage.load()));
        try (var entries = Files.list(path.getParent())) {
            assertEquals(List.of(path), entries.toList());
        }
    }

    @Test
    void load_malformedRecord_pathLineAndRecoveryGuidanceWithoutChangingFile() throws IOException {
        Path path = temporaryDirectory.resolve("olaf.txt");
        String[] records = {"bad record", "T | 2 | read book", "D | 0 | book | 2026-02-29",
                "T | 0 | ", "T | 0 | invalid\\escape", ""};
        for (String record : records) {
            Files.writeString(path, "T | 0 | valid task\n" + record + "\n");
            byte[] original = Files.readAllBytes(path);

            StorageException exception = assertThrows(StorageException.class, () -> new Olaf(path));

            assertTrue(exception.getMessage().contains(path.toString()));
            assertTrue(exception.getMessage().contains("line 2"));
            assertTrue(exception.getMessage().contains("restore a backup"));
            assertArrayEquals(original, Files.readAllBytes(path));
        }
    }

    @Test
    void load_invalidUtf8_filePreservedAndEncodingExplained() throws IOException {
        Path path = temporaryDirectory.resolve("olaf.txt");
        byte[] invalidBytes = {(byte) 0xc3, (byte) 0x28};
        Files.write(path, invalidBytes);

        StorageException exception = assertThrows(StorageException.class, () -> new Storage(path).load());

        assertTrue(exception.getMessage().contains("not valid UTF-8"));
        assertArrayEquals(invalidBytes, Files.readAllBytes(path));
    }

    @Test
    void load_unreadableFile_startupBlockedWithoutChangingFile() throws IOException {
        Path path = temporaryDirectory.resolve("olaf.txt");
        Files.writeString(path, "T | 0 | original\n");
        FailingStorageFiles files = new FailingStorageFiles();
        files.failure = FailingStorageFiles.Failure.READ;

        StorageException exception = assertThrows(StorageException.class,
                () -> new Olaf(new Storage(path, files)));

        assertTrue(exception.getMessage().contains(path.toString()));
        assertTrue(exception.getMessage().contains("access to the file or directory was denied"));
        assertEquals("T | 0 | original\n", Files.readString(path));
    }

    @Test
    void load_directoryInsteadOfFile_actionableErrorReturned() throws IOException {
        Path path = Files.createDirectory(temporaryDirectory.resolve("olaf.txt"));

        StorageException exception = assertThrows(StorageException.class, () -> new Storage(path).load());

        assertTrue(exception.getMessage().contains("directory, not a file"));
        assertTrue(Files.isDirectory(path));
    }

    @Test
    void save_writeOrReplacementFails_oldSnapshotPreservedAndTemporaryFileRemoved() throws Exception {
        Path path = temporaryDirectory.resolve("olaf.txt");
        List<FailingStorageFiles.Failure> failures = List.of(FailingStorageFiles.Failure.WRITE,
                FailingStorageFiles.Failure.INTERRUPTED_WRITE, FailingStorageFiles.Failure.REPLACE,
                FailingStorageFiles.Failure.UNSUPPORTED_MOVE);
        for (FailingStorageFiles.Failure failure : failures) {
            Files.writeString(path, "T | 0 | original\n");
            byte[] original = Files.readAllBytes(path);
            FailingStorageFiles files = new FailingStorageFiles();
            files.failure = failure;
            Storage storage = new Storage(path, files);
            TaskList candidate = new TaskList(List.of(new Todo("replacement")));

            StorageException exception = assertThrows(StorageException.class, () -> storage.save(candidate));

            assertTrue(exception.getMessage().contains("change was not saved"));
            if (failure == FailingStorageFiles.Failure.UNSUPPORTED_MOVE) {
                assertTrue(exception.getMessage().contains("atomic file replacement is not supported"));
            }
            assertArrayEquals(original, Files.readAllBytes(path), failure.toString());
            assertFalse(Files.exists(files.lastTemporaryFile), failure.toString());
            assertEquals(failure == FailingStorageFiles.Failure.REPLACE
                    || failure == FailingStorageFiles.Failure.UNSUPPORTED_MOVE, files.wasReplacementAttempted);

            files.failure = FailingStorageFiles.Failure.NONE;
            storage.save(candidate);
            assertEquals("replacement", storage.load().getTasks().getFirst().getDescription());
        }
    }

    @Test
    void save_firstWriteFails_noInvalidDataFileCreated() {
        Path path = temporaryDirectory.resolve("olaf.txt");
        FailingStorageFiles files = new FailingStorageFiles();
        files.failure = FailingStorageFiles.Failure.WRITE;

        assertThrows(StorageException.class,
                () -> new Storage(path, files).save(new TaskList(List.of(new Todo("new task")))));

        assertFalse(Files.exists(path));
        assertFalse(Files.exists(files.lastTemporaryFile));
    }

    @Test
    void save_parentIsFile_blockerPreserved() throws IOException {
        Path parent = temporaryDirectory.resolve("data");
        Files.writeString(parent, "keep this file");

        StorageException exception = assertThrows(StorageException.class,
                () -> new Storage(parent.resolve("olaf.txt")).save(new TaskList()));

        assertTrue(exception.getMessage().contains("change was not saved"));
        assertEquals("keep this file", Files.readString(parent));
    }

    @Test
    void save_cleanupAlsoFails_originalFailureRetained() throws IOException {
        Path path = temporaryDirectory.resolve("olaf.txt");
        Files.writeString(path, "T | 0 | original\n");
        FailingStorageFiles files = new FailingStorageFiles();
        files.failure = FailingStorageFiles.Failure.WRITE;
        files.shouldFailCleanup = true;

        StorageException exception = assertThrows(StorageException.class,
                () -> new Storage(path, files).save(new TaskList()));

        assertTrue(exception.getMessage().contains("No space left"));
        assertEquals(1, exception.getSuppressed().length);
        assertEquals("Temporary file cleanup failed", exception.getSuppressed()[0].getMessage());
        assertEquals("T | 0 | original\n", Files.readString(path));
    }
}
