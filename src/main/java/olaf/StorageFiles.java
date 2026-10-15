package olaf;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.List;

/**
 * Performs file operations used by storage, providing a small boundary for simulated I/O failures in tests.
 */
class StorageFiles {
    /** Reads the saved snapshot as UTF-8 records. */
    List<String> readRecords(Path path) throws IOException {
        return Files.readAllLines(path, StandardCharsets.UTF_8);
    }

    /** Writes and closes the temporary snapshot before it can replace the saved file. */
    void writeRecords(Path path, List<String> records) throws IOException {
        Files.write(path, records, StandardCharsets.UTF_8,
                StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
    }

    /** Atomically installs a complete snapshot, failing if the filesystem cannot support this operation. */
    void replace(Path temporaryFile, Path destination) throws IOException {
        Files.move(temporaryFile, destination,
                StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
    }

    /** Deletes an unused temporary file after a failed save. */
    void deleteIfExists(Path path) throws IOException {
        Files.deleteIfExists(path);
    }
}
