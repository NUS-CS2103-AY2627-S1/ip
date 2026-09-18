package herta.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests strict file-manager reads, writes, snapshots, and path handling.
 */
class StorageFileManagerTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void getPathStatus_missingFileRegularFileAndDirectory_returnsCorrectStatuses() throws Exception {
        Path missingFile = temporaryDirectory.resolve("missing.txt");
        Path regularFile = temporaryDirectory.resolve("regular.txt");
        Path directory = temporaryDirectory.resolve("directory");
        Files.writeString(regularFile, "contents", StandardCharsets.UTF_8);
        Files.createDirectory(directory);

        assertEquals(StorageFileManager.PathStatus.MISSING,
                new StorageFileManager(missingFile).getPathStatus());
        assertEquals(StorageFileManager.PathStatus.REGULAR_FILE,
                new StorageFileManager(regularFile).getPathStatus());
        assertEquals(StorageFileManager.PathStatus.DIRECTORY,
                new StorageFileManager(directory).getPathStatus());
    }

    @Test
    void getPathStatus_supportedNonRegularPath_returnsOtherWhenAvailable() throws Exception {
        Path targetFile = temporaryDirectory.resolve("target.txt");
        Path symbolicLink = temporaryDirectory.resolve("link.txt");
        Files.writeString(targetFile, "contents", StandardCharsets.UTF_8);
        try {
            Files.createSymbolicLink(symbolicLink, targetFile.getFileName());
            assertEquals(StorageFileManager.PathStatus.OTHER,
                    new StorageFileManager(symbolicLink).getPathStatus());
        } catch (UnsupportedOperationException | IOException | SecurityException exception) {
            Assumptions.assumeTrue(false, "Symbolic links are unavailable on this host.");
        }
    }

    @Test
    void readLines_missingFile_throwsNoSuchFileException() {
        Path missingFile = temporaryDirectory.resolve("missing.txt");

        assertThrows(NoSuchFileException.class, () -> new StorageFileManager(missingFile).readLines());
    }

    @Test
    void readLines_directoryPath_throwsRegularFileError() throws Exception {
        Path directory = temporaryDirectory.resolve("directory");
        Files.createDirectory(directory);

        IOException exception = assertThrows(IOException.class, () ->
                new StorageFileManager(directory).readLines());

        assertEquals("data path is not a regular file", exception.getMessage());
    }

    @Test
    void writeTemporaryFile_directoryPath_rejectsTarget() throws Exception {
        Path directory = temporaryDirectory.resolve("directory");
        Files.createDirectory(directory);

        IOException exception = assertThrows(IOException.class, () ->
                new StorageFileManager(directory).writeTemporaryFile(List.of("record")));

        assertEquals("data path is not a regular file", exception.getMessage());
        assertEquals(List.of(), findTemporaryFiles());
    }

    @Test
    void captureSnapshot_directoryPath_rejectsTarget() throws Exception {
        Path directory = temporaryDirectory.resolve("directory");
        Files.createDirectory(directory);

        IOException exception = assertThrows(IOException.class, () ->
                new StorageFileManager(directory).captureSnapshot());

        assertEquals("data path is not a readable regular file", exception.getMessage());
    }

    @Test
    void writeTemporaryFile_normalContentAndNullLine_manageLifecycle() throws Exception {
        Path dataFile = temporaryDirectory.resolve("data").resolve("tasks.txt");
        StorageFileManager manager = new StorageFileManager(dataFile);

        Path temporaryFile = manager.writeTemporaryFile(List.of("T | 0 | task"));
        assertEquals("T | 0 | task" + System.lineSeparator(),
                Files.readString(temporaryFile, StandardCharsets.UTF_8));
        manager.deleteTemporaryFile(temporaryFile);
        assertTrue(Files.notExists(temporaryFile));
        assertThrows(IOException.class, () -> manager.writeTemporaryFile(
                Collections.singletonList(null)));
    }

    @Test
    void writeTemporaryFile_oversizedContent_rejectsBeforeCreatingTemporaryFile() throws Exception {
        StorageFileManager manager = new StorageFileManager(
                temporaryDirectory.resolve("oversized.txt"));
        String oversizedRecord = "a".repeat((int) StorageFileManager.MAX_STORAGE_FILE_BYTES);

        assertThrows(IOException.class, () -> manager.writeTemporaryFile(List.of(oversizedRecord)));
        assertEquals(List.of(), findTemporaryFiles());
    }

    @Test
    void replaceDataFile_replacesTargetAndRemovesTemporaryFile() throws Exception {
        Path dataFile = temporaryDirectory.resolve("replace.txt");
        StorageFileManager manager = new StorageFileManager(dataFile);
        Path temporaryFile = manager.writeTemporaryFile(List.of("new contents"));

        manager.replaceDataFile(temporaryFile);

        assertEquals("new contents" + System.lineSeparator(), Files.readString(dataFile));
        assertTrue(Files.notExists(temporaryFile));
    }

    @Test
    void snapshots_compareDefensivelyAndRestorePreviousBytesOrAbsence() throws Exception {
        Path dataFile = temporaryDirectory.resolve("snapshot.txt");
        StorageFileManager manager = new StorageFileManager(dataFile);
        StorageFileManager.FileSnapshot absentSnapshot = manager.captureSnapshot();
        Files.writeString(dataFile, "original", StandardCharsets.UTF_8);
        StorageFileManager.FileSnapshot presentSnapshot = manager.captureSnapshot();
        byte[] copiedContents = presentSnapshot.contents();
        copiedContents[0] = 'X';

        assertEquals((byte) 'o', presentSnapshot.contents()[0]);
        assertTrue(manager.hasSameContents(presentSnapshot));
        Files.writeString(dataFile, "changed", StandardCharsets.UTF_8);
        assertFalse(manager.hasSameContents(presentSnapshot));
        manager.restoreSnapshot(presentSnapshot);
        assertEquals("original", Files.readString(dataFile));

        manager.restoreSnapshot(absentSnapshot);
        assertTrue(Files.notExists(dataFile));
    }

    @Test
    void createOwnedTemporaryFile_allocatesUniqueFilesInRequestedDirectory() throws Exception {
        Path firstFile = StorageFileManager.createOwnedTemporaryFile(
                temporaryDirectory, ".herta-test-", ".tmp");
        Path secondFile = StorageFileManager.createOwnedTemporaryFile(
                temporaryDirectory, ".herta-test-", ".tmp");

        assertNotEquals(firstFile, secondFile);
        assertEquals(temporaryDirectory, firstFile.getParent());
        assertEquals(temporaryDirectory, secondFile.getParent());
        Files.deleteIfExists(firstFile);
        Files.deleteIfExists(secondFile);
    }

    private List<Path> findTemporaryFiles() throws IOException {
        try (var paths = Files.list(temporaryDirectory)) {
            return paths.filter(path -> path.getFileName().toString().endsWith(".tmp")).toList();
        }
    }
}
