package verity.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Writes complete UTF-8 file contents before replacing the destination file.
 */
final class AtomicFileWriter {
    private static final String TEMPORARY_FILE_PREFIX = "verity-";
    private static final String TEMPORARY_FILE_SUFFIX = ".tmp";

    private AtomicFileWriter() {
    }

    static void write(Path dataFilePath, String contents) throws IOException {
        Path parentDirectory = dataFilePath.getParent();
        if (parentDirectory != null) {
            Files.createDirectories(parentDirectory);
        }

        Path temporaryDirectory = parentDirectory == null
                ? Path.of(".")
                : parentDirectory;
        Path temporaryFile = Files.createTempFile(
                temporaryDirectory,
                TEMPORARY_FILE_PREFIX,
                TEMPORARY_FILE_SUFFIX);
        try {
            Files.writeString(temporaryFile, contents, StandardCharsets.UTF_8);
            replaceDestination(temporaryFile, dataFilePath);
        } finally {
            Files.deleteIfExists(temporaryFile);
        }
    }

    private static void replaceDestination(Path temporaryFile,
            Path dataFilePath) throws IOException {
        try {
            Files.move(
                    temporaryFile,
                    dataFilePath,
                    StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException exception) {
            Files.move(
                    temporaryFile,
                    dataFilePath,
                    StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
