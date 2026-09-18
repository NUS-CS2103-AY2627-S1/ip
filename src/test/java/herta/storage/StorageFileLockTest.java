package herta.storage;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.FileSystemException;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests acquisition and release of storage file locks.
 */
class StorageFileLockTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    void acquire_equivalentDirectories_usesOneLock() throws Exception {
        Path activeFile = temporaryDirectory.resolve("herta.txt");
        Path archiveFile = temporaryDirectory.resolve("herta-archive.txt");

        try (StorageFileLock ignored = StorageFileLock.acquire(activeFile, archiveFile)) {
            // Acquiring both paths succeeds only when the shared lock path is deduplicated.
        }
    }

    @Test
    void acquire_overlappingLock_failsWithLockException() throws Exception {
        Path activeFile = temporaryDirectory.resolve("herta.txt");
        Path archiveFile = temporaryDirectory.resolve("herta-archive.txt");

        try (StorageFileLock ignored = StorageFileLock.acquire(activeFile, archiveFile)) {
            FileSystemException exception = assertThrows(FileSystemException.class, () ->
                    StorageFileLock.acquire(archiveFile, activeFile));
            String message = exception.getReason();
            org.junit.jupiter.api.Assertions.assertEquals("file is locked", message);
        }
    }

    @Test
    void close_releasesLock_allowsNewAcquisition() throws Exception {
        Path activeFile = temporaryDirectory.resolve("herta.txt");
        Path archiveFile = temporaryDirectory.resolve("herta-archive.txt");
        StorageFileLock lock = StorageFileLock.acquire(activeFile, archiveFile);

        lock.close();

        try (StorageFileLock ignored = StorageFileLock.acquire(archiveFile, activeFile)) {
            // The closed lock must release every channel before acquisition can succeed again.
        }
    }
}
