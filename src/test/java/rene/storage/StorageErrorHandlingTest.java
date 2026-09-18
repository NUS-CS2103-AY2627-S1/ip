package rene.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import rene.exception.ReneException;

/**
 * Verifies that environment problems around the data file are reported
 * with messages that tell the user what went wrong.
 */
class StorageErrorHandlingTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    void loadTasks_pathIsADirectory_reportsFolderInsteadOfFile() {
        Path folderAsFile = temporaryDirectory.resolve("tasks-folder");
        try {
            Files.createDirectory(folderAsFile);
        } catch (IOException exception) {
            throw new IllegalStateException("Test setup should create a directory.", exception);
        }
        Storage storage = new Storage(folderAsFile);

        ReneException exception = assertThrows(ReneException.class, storage::loadTasks);

        assertEquals(
                folderAsFile + " is a folder, not a file, so I cannot read tasks from it.",
                exception.getMessage());
    }

    @Test
    void saveTasks_pathIsADirectory_reportsFolderInsteadOfFile() {
        Path folderAsFile = temporaryDirectory.resolve("tasks-folder");
        try {
            Files.createDirectory(folderAsFile);
        } catch (IOException exception) {
            throw new IllegalStateException("Test setup should create a directory.", exception);
        }
        Storage storage = new Storage(folderAsFile);

        ReneException exception = assertThrows(ReneException.class, () -> storage.saveTasks(List.of()));

        assertEquals(
                folderAsFile + " is a folder, not a file, so I cannot save tasks there.",
                exception.getMessage());
    }

    @Test
    void loadTasks_fileWithoutReadPermission_reportsPermissionProblem() throws IOException {
        Path dataFile = temporaryDirectory.resolve("locked.txt");
        Files.writeString(dataFile, "T | 0 | hidden task\n");
        try {
            if (!dataFile.toFile().setReadable(false) || Files.isReadable(dataFile)) {
                // Platforms such as Windows ignore these permission bits.
                Assumptions.abort("Permission bits are not enforced on this platform.");
            }
            Storage storage = new Storage(dataFile);

            ReneException exception = assertThrows(ReneException.class, storage::loadTasks);

            assertEquals("I do not have permission to read " + dataFile + ". "
                    + "Check the file's permissions and try again.", exception.getMessage());
        } finally {
            dataFile.toFile().setReadable(true);
        }
    }

    @Test
    void saveTasks_fileWithoutWritePermission_reportsPermissionProblem() throws IOException {
        Path dataFile = temporaryDirectory.resolve("locked-save.txt");
        Files.writeString(dataFile, "");
        try {
            if (!dataFile.toFile().setWritable(false) || Files.isWritable(dataFile)) {
                // Platforms such as Windows ignore these permission bits.
                Assumptions.abort("Permission bits are not enforced on this platform.");
            }
            Storage storage = new Storage(dataFile);

            ReneException exception = assertThrows(
                    ReneException.class, () -> storage.saveTasks(List.of()));

            assertEquals("I do not have permission to save tasks to " + dataFile + ". "
                    + "Check the file's permissions and try again.", exception.getMessage());
        } finally {
            dataFile.toFile().setWritable(true);
        }
    }
}
