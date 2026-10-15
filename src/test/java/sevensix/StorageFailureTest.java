package sevensix;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Verifies storage failures never silently replace unreadable or damaged task files. */
class StorageFailureTest {
    /** Isolates every filesystem fixture from the user's task data. */
    @TempDir
    private Path temporaryDirectory;

    /** A bad configured path must disable storage instead of crashing startup. */
    @Test
    void loadInvalidPathReturnsVisibleWarning() {
        TaskStorage storage = new TaskStorage("invalid\0path");

        assertTrue(storage.load().isEmpty());
        assertEquals(Ui.LOAD_FAILURE, storage.getLoadWarning());
        assertFalse(storage.save(List.of(new Todo("never saved"))));
    }

    /** A directory cannot be read or overwritten as a task file. */
    @Test
    void loadDirectoryBlocksSaving() {
        TaskStorage storage = new TaskStorage(temporaryDirectory);

        assertTrue(storage.load().isEmpty());
        assertEquals(Ui.LOAD_FAILURE, storage.getLoadWarning());
        assertFalse(storage.save(List.of(new Todo("never saved"))));
        assertTrue(Files.isDirectory(temporaryDirectory));
    }

    /** Symlinks must not be followed or replaced, preserving the actual target. */
    @Test
    void loadSymbolicLinkBlocksSavingAndPreservesTarget() throws IOException {
        assumeTrue(Files.getFileStore(temporaryDirectory).supportsFileAttributeView("posix"));
        Path target = temporaryDirectory.resolve("original.txt");
        Path link = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(target, "T | 0 | keep me\n");
        Files.createSymbolicLink(link, target);
        TaskStorage storage = new TaskStorage(link);

        assertTrue(storage.load().isEmpty());
        assertEquals(Ui.LOAD_FAILURE, storage.getLoadWarning());
        assertFalse(storage.save(List.of(new Todo("replacement"))));
        assertTrue(Files.isSymbolicLink(link));
        assertEquals("T | 0 | keep me\n", Files.readString(target));
    }

    /** A denied read must block saving even if permissions are repaired later in the session. */
    @Test
    void loadDeniedReadRequiresRestartBeforeSaving() throws IOException {
        assumeTrue(Files.getFileStore(temporaryDirectory).supportsFileAttributeView("posix"));
        Path file = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(file, "T | 0 | keep me\n");
        Files.setPosixFilePermissions(file, PosixFilePermissions.fromString("---------"));
        TaskStorage storage = new TaskStorage(file);
        try {
            assumeTrue(!Files.isReadable(file));
            assertTrue(storage.load().isEmpty());
            assertEquals(Ui.LOAD_FAILURE, storage.getLoadWarning());
        } finally {
            Files.setPosixFilePermissions(file, PosixFilePermissions.fromString("rw-------"));
        }
        assertFalse(storage.save(List.of()));
        assertEquals("T | 0 | keep me\n", Files.readString(file));
    }

    /** A file occupying a required parent directory must remain untouched. */
    @Test
    void loadBlockedParentPreservesExistingFile() throws IOException {
        Path parent = temporaryDirectory.resolve("parent");
        Files.writeString(parent, "keep this file");
        TaskStorage storage = new TaskStorage(parent.resolve("tasks.txt"));

        assertTrue(storage.load().isEmpty());
        assertEquals(Ui.LOAD_FAILURE, storage.getLoadWarning());
        assertFalse(storage.save(List.of(new Todo("never saved"))));
        assertEquals("keep this file", Files.readString(parent));
    }

    /** Invalid UTF-8 is a read failure, not an empty task list that can be overwritten. */
    @Test
    void loadInvalidEncodingBlocksSaving() throws IOException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        byte[] invalidBytes = {(byte) 0xc3, (byte) 0x28};
        Files.write(file, invalidBytes);
        TaskStorage storage = new TaskStorage(file);

        assertTrue(storage.load().isEmpty());
        assertEquals(Ui.LOAD_FAILURE, storage.getLoadWarning());
        assertFalse(storage.save(List.of()));
        assertEquals(2, Files.size(file));
    }

    /** Invalid records and duplicates must be reported while preserving the original bytes. */
    @Test
    void loadCorruptDataPreservesFileAndLoadsValidUniqueTasks() throws IOException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        String original = String.join("\n", "T | 0 | valid", "T | 1 | valid", "T | 7 | bad status",
                "D | 0 | impossible | 2024-02-30", "E | 0 | reversed | 2024-01-02 | 2024-01-01",
                "E | 0 | equal | 2024-01-01 | 2024-01-01", "X | 0 | unknown", "T | 0 | ",
                "T | 0 | trailing\\", "T | 0 | invalid\\q", "T | 0 | extra | field");
        Files.writeString(file, original);
        TaskStorage storage = new TaskStorage(file);

        assertEquals(1, storage.load().size());
        assertEquals(Ui.CORRUPT_STORAGE, storage.getLoadWarning());
        assertFalse(storage.save(List.of(new Todo("replacement"))));
        assertEquals(original, Files.readString(file));
    }

    /** Replacing an existing task file should save the full new list without temporary debris. */
    @Test
    void saveReplacesCompleteFileAndCleansTemporarySibling() throws IOException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        TaskStorage storage = new TaskStorage(file);
        assertTrue(storage.save(List.of(new Todo("old"))));
        assertTrue(storage.save(List.of(new Todo("new"), new Todo("second"))));

        assertEquals(List.of("new", "second"), storage.load().stream().map(Task::getDescription).toList());
        try (var files = Files.list(temporaryDirectory)) {
            assertEquals(List.of(file), files.toList());
        }
    }

    /** An unsuccessful move should clean up its temporary file and preserve the target. */
    @Test
    void saveFailedReplacementCleansTemporarySibling() throws IOException {
        Path file = Files.createDirectory(temporaryDirectory.resolve("tasks.txt"));
        TaskStorage storage = new TaskStorage(file);

        assertFalse(storage.save(List.of(new Todo("new"))));
        assertTrue(Files.isDirectory(file));
        try (var files = Files.list(temporaryDirectory)) {
            assertEquals(List.of(file), files.toList());
        }
    }

    /** Atomic replacement must respect a file explicitly marked read-only. */
    @Test
    void saveReadOnlyFileFailsWithoutReplacingIt() throws IOException {
        assumeTrue(Files.getFileStore(temporaryDirectory).supportsFileAttributeView("posix"));
        Path file = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(file, "T | 0 | keep me\n");
        Files.setPosixFilePermissions(file, PosixFilePermissions.fromString("r--------"));
        try {
            assumeTrue(!Files.isWritable(file));
            assertFalse(new TaskStorage(file).save(List.of(new Todo("replacement"))));
            assertEquals("T | 0 | keep me\n", Files.readString(file));
        } finally {
            Files.setPosixFilePermissions(file, PosixFilePermissions.fromString("rw-------"));
        }
    }
}
