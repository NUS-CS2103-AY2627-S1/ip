package niulai.service;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import niulai.model.Deadline;
import niulai.model.Event;
import niulai.model.TaskList;
import niulai.model.Todo;

/** Tests persistence, escaping, replacement, and malformed-data handling. */
class StorageTest {
    /** Temporary directory isolated from the application's real data file. */
    @TempDir
    Path temporaryDirectory;

    /** Verifies that all task types and completion states survive a save/load round trip. */
    @Test
    void saveAndLoad_mixedTasks_preservesTaskData() throws IOException {
        Storage storage = new Storage(temporaryDirectory.resolve("niulai.txt").toString());
        TaskList saved = new TaskList();
        saved.add(new Todo("read | review \\ draft"));
        Deadline deadline = new Deadline("submit report", "2026-08-31");
        deadline.markAsDone();
        saved.add(deadline);
        saved.add(new Event("meeting", "2026-08-30", "2026-09-02"));

        storage.save(saved);
        TaskList loaded = storage.load().tasks();

        assertEquals(3, loaded.size());
        assertEquals("read | review \\ draft", loaded.get(0).getDescription());
        assertEquals(" ", loaded.get(0).getStatusIcon());
        assertEquals("X", loaded.get(1).getStatusIcon());
        assertEquals("[D][X] submit report (by: Aug 31 2026)", loaded.get(1).toString());
        assertEquals("[E][ ] meeting (from: 2026-08-30 to: 2026-09-02)",
                loaded.get(2).toString());
    }

    /** Verifies that saving replaces an existing file, which is the Windows regression case. */
    @Test
    void save_existingFile_replacesContents() throws IOException {
        Path file = temporaryDirectory.resolve("niulai.txt");
        Files.writeString(file, "T | 0 | old task");
        Storage storage = new Storage(file.toString());
        TaskList tasks = new TaskList();
        tasks.add(new Todo("new task"));

        storage.save(tasks);

        assertEquals("T | 0 | new task", Files.readString(file).strip());
    }

    /** Verifies that malformed and duplicate lines are skipped independently. */
    @Test
    void load_mixedValidInvalidAndDuplicateLines_recoversUniqueValidTasks() throws IOException {
        Path file = temporaryDirectory.resolve("niulai.txt");
        Files.writeString(file,
                "T | 0 | first\ninvalid\nT | 1 | FIRST\nD | 0 | report | 2026-09-10");

        Storage.LoadResult result = new Storage(file.toString()).load();

        assertEquals(2, result.tasks().size());
        assertEquals("first", result.tasks().get(0).getDescription());
        assertEquals("report", result.tasks().get(1).getDescription());
        assertEquals(List.of(2, 3), result.issues().stream()
                .map(Storage.LoadIssue::lineNumber)
                .toList());
    }

    /** Verifies that an unsupported completion state is reported without losing later records. */
    @Test
    void load_invalidCompletionState_issueReportedAndLaterRecordRecovered() throws IOException {
        Path file = temporaryDirectory.resolve("niulai.txt");
        Files.writeString(file, "T | 2 | invalid state\nT | 0 | valid task");

        Storage.LoadResult result = new Storage(file.toString()).load();

        assertEquals(1, result.tasks().size());
        assertEquals("valid task", result.tasks().get(0).getDescription());
        assertEquals(List.of(1), result.issues().stream()
                .map(Storage.LoadIssue::lineNumber)
                .toList());
    }

    /** Verifies that a missing data file is treated as an empty task list. */
    @Test
    void load_missingFile_returnsEmptyTaskList() throws IOException {
        Storage.LoadResult result =
                new Storage(temporaryDirectory.resolve("missing.txt").toString()).load();

        assertEquals(0, result.tasks().size());
        assertEquals(List.of(), result.issues());
    }

    /** Verifies that recovery saves protect the exact original bytes before replacing the file. */
    @Test
    void save_afterPartialRecovery_createsBackupBeforeReplacingData() throws IOException {
        Path file = temporaryDirectory.resolve("niulai.txt");
        byte[] original = "T | 0 | first\ninvalid".getBytes(StandardCharsets.UTF_8);
        Files.write(file, original);
        Storage storage = new Storage(file.toString());
        Storage.LoadResult result = storage.load();

        storage.save(result.tasks());

        assertArrayEquals(original, Files.readAllBytes(file.resolveSibling("niulai.txt.bak")));
        assertEquals("T | 0 | first", Files.readString(file).strip());
    }

    /** Verifies that recovery never replaces an existing backup. */
    @Test
    void save_existingBackup_usesNextAvailableBackupName() throws IOException {
        Path file = temporaryDirectory.resolve("niulai.txt");
        byte[] original = "T | 0 | first\ninvalid".getBytes(StandardCharsets.UTF_8);
        Files.write(file, original);
        Path firstBackup = file.resolveSibling("niulai.txt.bak");
        Files.writeString(firstBackup, "older backup");
        Storage storage = new Storage(file.toString());
        Storage.LoadResult result = storage.load();

        storage.save(result.tasks());

        assertEquals("older backup", Files.readString(firstBackup));
        assertArrayEquals(original, Files.readAllBytes(file.resolveSibling("niulai.txt.bak.1")));
    }

    /** Verifies that backup failure leaves the damaged data file untouched. */
    @Test
    void save_backupCreationFails_exceptionLeavesOriginalFileUntouched() throws IOException {
        Path file = temporaryDirectory.resolve("niulai.txt");
        byte[] original = "T | 0 | first\ninvalid".getBytes(StandardCharsets.UTF_8);
        Files.write(file, original);
        Storage storage = new Storage(file.toString(), Files::move, (backup, bytes) -> {
            throw new AccessDeniedException(backup.toString());
        });
        Storage.LoadResult result = storage.load();

        StorageException exception = assertThrows(StorageException.class,
                () -> storage.save(result.tasks()));

        assertArrayEquals(original, Files.readAllBytes(file));
        assertInstanceOf(AccessDeniedException.class, exception.getCause());
    }

    /** Verifies that invalid UTF-8 blocks writes rather than risking destructive replacement. */
    @Test
    void load_invalidUtf8_storageExceptionBlocksLaterSave() throws IOException {
        Path file = temporaryDirectory.resolve("niulai.txt");
        byte[] invalidUtf8 = {(byte) 0xC3, (byte) 0x28};
        Files.write(file, invalidUtf8);
        Storage storage = new Storage(file.toString());

        StorageException loadError = assertThrows(StorageException.class, storage::load);
        StorageException saveError = assertThrows(StorageException.class,
                () -> storage.save(new TaskList(new Todo("must not save"))));

        assertEquals(StorageException.READ_BLOCKED_MESSAGE, loadError.getUserMessage());
        assertEquals(StorageException.READ_BLOCKED_MESSAGE, saveError.getUserMessage());
        assertArrayEquals(invalidUtf8, Files.readAllBytes(file));
    }

    /** Verifies that a directory used as the data path produces a typed loading failure. */
    @Test
    void load_directoryPath_storageExceptionThrown() {
        Storage storage = new Storage(temporaryDirectory.toString());

        StorageException exception = assertThrows(StorageException.class, storage::load);

        assertEquals(StorageException.READ_BLOCKED_MESSAGE, exception.getUserMessage());
    }

    /** Verifies that a file changed after loading is not overwritten. */
    @Test
    void save_externalModification_exceptionLeavesExternalFileUntouched() throws IOException {
        Path file = temporaryDirectory.resolve("niulai.txt");
        Files.writeString(file, "T | 0 | first");
        Storage storage = new Storage(file.toString());
        Storage.LoadResult result = storage.load();
        Files.writeString(file, "T | 0 | externally changed");

        StorageException exception = assertThrows(StorageException.class,
                () -> storage.save(result.tasks()));

        assertEquals(StorageException.EXTERNAL_CHANGE_MESSAGE, exception.getUserMessage());
        assertEquals("T | 0 | externally changed", Files.readString(file));
    }

    /** Verifies that transient Windows access denial during replacement is retried. */
    @Test
    void save_transientAccessDeniedDuringMove_retriesAndSucceeds() throws IOException {
        Path file = temporaryDirectory.resolve("niulai.txt");
        AtomicInteger moveAttempts = new AtomicInteger();
        Storage storage = new Storage(file.toString(), (source, target, options) -> {
            if (moveAttempts.incrementAndGet() < 3) {
                throw new AccessDeniedException(target.toString());
            }
            Files.move(source, target, options);
        });

        storage.save(new TaskList(new Todo("saved after retry")));

        assertEquals(3, moveAttempts.get());
        assertEquals("T | 0 | saved after retry", Files.readString(file).strip());
    }

    /** Verifies that persistent access denial remains a save failure after bounded retries. */
    @Test
    void save_persistentAccessDeniedDuringMove_exceptionLeavesTargetAbsent() {
        Path file = temporaryDirectory.resolve("niulai.txt");
        AtomicInteger moveAttempts = new AtomicInteger();
        Storage storage = new Storage(file.toString(), (source, target, options) -> {
            moveAttempts.incrementAndGet();
            throw new AccessDeniedException(target.toString());
        });

        StorageException exception = assertThrows(StorageException.class,
                () -> storage.save(new TaskList(new Todo("cannot save"))));

        assertEquals(5, moveAttempts.get());
        assertFalse(Files.exists(file));
        assertInstanceOf(AccessDeniedException.class, exception.getCause());
    }

    /** Verifies UTF-8 byte-order marks and blank lines do not create phantom tasks. */
    @Test
    void load_bomAndBlankLines_loadsOnlyMeaningfulRecords() throws IOException {
        Path file = temporaryDirectory.resolve("niulai.txt");
        Files.writeString(file, "\uFEFFT | 1 | completed\r\n\r\nT | 0 | pending\r\n");

        Storage.LoadResult result = new Storage(file.toString()).load();

        assertEquals(2, result.tasks().size());
        assertEquals("X", result.tasks().get(0).getStatusIcon());
        assertEquals("pending", result.tasks().get(1).getDescription());
        assertEquals(List.of(), result.issues());
    }

    /** Verifies malformed fields are isolated while unknown escaped characters remain literal. */
    @Test
    void load_variedMalformedFields_reportsEveryBadLineAndKeepsValidRecord() throws IOException {
        Path file = temporaryDirectory.resolve("niulai.txt");
        Files.writeString(file, String.join("\n",
                "X | 0 | unknown type",
                "T | 0 | ",
                "T | 0 | todo | extra",
                "D | 0 | missing deadline",
                "D | 0 | blank deadline | ",
                "E | 0 | missing endpoint | start",
                "E | 0 | too many | start | end | extra",
                "T | zero | invalid status",
                "T | 0 | dangling\\",
                "T | 0 | keep\\qvalue"));

        Storage.LoadResult result = new Storage(file.toString()).load();

        assertEquals(1, result.tasks().size());
        assertEquals("keep\\qvalue", result.tasks().get(0).getDescription());
        assertEquals(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9), result.issues().stream()
                .map(Storage.LoadIssue::lineNumber)
                .toList());
    }

    /** Verifies a file created after a missing-file load is treated as an external change. */
    @Test
    void save_fileCreatedAfterMissingLoad_externalChangeReported() throws IOException {
        Path file = temporaryDirectory.resolve("niulai.txt");
        Storage storage = new Storage(file.toString());
        Storage.LoadResult result = storage.load();
        Files.writeString(file, "T | 0 | external");

        StorageException exception = assertThrows(StorageException.class,
                () -> storage.save(result.tasks()));

        assertEquals(StorageException.EXTERNAL_CHANGE_MESSAGE, exception.getUserMessage());
        assertEquals("T | 0 | external", Files.readString(file));
    }

    /** Verifies a loaded file deleted externally is not silently recreated. */
    @Test
    void save_fileDeletedAfterLoad_externalChangeReported() throws IOException {
        Path file = temporaryDirectory.resolve("niulai.txt");
        Files.writeString(file, "T | 0 | original");
        Storage storage = new Storage(file.toString());
        Storage.LoadResult result = storage.load();
        Files.delete(file);

        StorageException exception = assertThrows(StorageException.class,
                () -> storage.save(result.tasks()));

        assertEquals(StorageException.EXTERNAL_CHANGE_MESSAGE, exception.getUserMessage());
        assertFalse(Files.exists(file));
    }

    /** Verifies replacing a loaded file with a directory is detected before writing. */
    @Test
    void save_fileReplacedByDirectoryAfterLoad_externalChangeReported() throws IOException {
        Path file = temporaryDirectory.resolve("niulai.txt");
        Files.writeString(file, "T | 0 | original");
        Storage storage = new Storage(file.toString());
        Storage.LoadResult result = storage.load();
        Files.delete(file);
        Files.createDirectory(file);

        StorageException exception = assertThrows(StorageException.class,
                () -> storage.save(result.tasks()));

        assertEquals(StorageException.EXTERNAL_CHANGE_MESSAGE, exception.getUserMessage());
        assertTrue(Files.isDirectory(file));
    }

    /** Verifies unsupported atomic replacement falls back to a regular replacement move. */
    @Test
    void save_atomicMoveUnsupported_fallsBackAndSucceeds() throws IOException {
        Path file = temporaryDirectory.resolve("niulai.txt");
        AtomicInteger moveAttempts = new AtomicInteger();
        Storage storage = new Storage(file.toString(), (source, target, options) -> {
            if (moveAttempts.incrementAndGet() == 1) {
                throw new AtomicMoveNotSupportedException(
                        source.toString(), target.toString(), "Simulated unsupported move.");
            }
            Files.move(source, target, options);
        });

        storage.save(new TaskList(new Todo("saved by fallback")));

        assertEquals(2, moveAttempts.get());
        assertEquals("T | 0 | saved by fallback", Files.readString(file).strip());
    }

    /** Verifies interruption aborts retry backoff and preserves the interrupted flag. */
    @Test
    void save_retryInterrupted_storageExceptionPreservesInterrupt() {
        Path file = temporaryDirectory.resolve("niulai.txt");
        Storage storage = new Storage(file.toString(), (source, target, options) -> {
            throw new AccessDeniedException(target.toString());
        });

        try {
            Thread.currentThread().interrupt();

            StorageException exception = assertThrows(StorageException.class,
                    () -> storage.save(new TaskList(new Todo("cannot save"))));

            assertTrue(Thread.currentThread().isInterrupted());
            assertEquals("Interrupted while retrying task-file replacement.",
                    exception.getCause().getMessage());
        } finally {
            Thread.interrupted();
        }
    }

    /** Verifies cleanup errors are retained without hiding the primary save failure. */
    @Test
    void save_moveAndCleanupFail_primaryFailureRetainsSuppressedCleanup() {
        Path file = temporaryDirectory.resolve("niulai.txt");
        Storage storage = new Storage(file.toString(), (source, target, options) -> {
            Files.delete(source);
            Files.createDirectory(source);
            Files.writeString(source.resolve("child.txt"), "prevents directory deletion");
            throw new IOException("Simulated move failure.");
        });

        StorageException exception = assertThrows(StorageException.class,
                () -> storage.save(new TaskList(new Todo("cannot save"))));

        assertEquals("Simulated move failure.", exception.getCause().getMessage());
        assertEquals(1, exception.getSuppressed().length);
    }

    /** Verifies storage constructors and save reject absent required dependencies. */
    @Test
    void storage_nullDependencies_exceptionThrown() {
        assertThrows(NullPointerException.class, () -> new Storage(null));
        assertThrows(NullPointerException.class,
                () -> new Storage("unused.txt", null));
        assertThrows(NullPointerException.class,
                () -> new Storage("unused.txt", Files::move, null));
        assertThrows(NullPointerException.class,
                () -> new Storage("unused.txt").save(null));
    }

    /** Verifies load records validate inputs and defensively copy their issue list. */
    @Test
    void loadRecords_invalidOrMutableInputs_validateAndCopy() {
        assertThrows(IllegalArgumentException.class,
                () -> new Storage.LoadIssue(0, "invalid"));
        assertThrows(NullPointerException.class,
                () -> new Storage.LoadIssue(1, null));
        assertThrows(NullPointerException.class,
                () -> new Storage.LoadResult(null, List.of()));
        assertThrows(NullPointerException.class,
                () -> new Storage.LoadResult(new TaskList(), null));

        ArrayList<Storage.LoadIssue> mutableIssues = new ArrayList<>();
        mutableIssues.add(new Storage.LoadIssue(2, "invalid line"));
        Storage.LoadResult result = new Storage.LoadResult(new TaskList(), mutableIssues);
        mutableIssues.clear();

        assertEquals(List.of(new Storage.LoadIssue(2, "invalid line")), result.issues());
        assertThrows(UnsupportedOperationException.class,
                () -> result.issues().add(new Storage.LoadIssue(3, "another line")));
    }
}
