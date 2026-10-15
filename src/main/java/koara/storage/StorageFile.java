package koara.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

/**
 * Writes complete data files through a temporary file to avoid leaving partial saved data.
 */
final class StorageFile {
    private StorageFile() {
    }

    /**
     * Atomically replaces a data file when supported by the file system.
     *
     * @param targetPath Destination data file.
     * @param lines Complete lines to save.
     * @throws IOException If the temporary file cannot be written or moved.
     */
    static void writeLines(Path targetPath, List<String> lines) throws IOException {
        Path absoluteTarget = targetPath.toAbsolutePath();
        Path dataDirectory = absoluteTarget.getParent();
        assert dataDirectory != null : "Absolute data path must have a parent directory";
        Files.createDirectories(dataDirectory);
        Path temporaryFile = Files.createTempFile(
                dataDirectory, absoluteTarget.getFileName().toString(), ".tmp");
        try {
            Files.write(temporaryFile, lines, StandardCharsets.UTF_8);
            replaceTarget(temporaryFile, absoluteTarget);
        } finally {
            Files.deleteIfExists(temporaryFile);
        }
    }

    private static void replaceTarget(Path temporaryFile, Path targetPath) throws IOException {
        try {
            Files.move(temporaryFile, targetPath,
                    StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException exception) {
            Files.move(temporaryFile, targetPath, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
