package oreo;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;

import oreo.exception.OreoException;

/**
 * Handles loading task data from and saving task data to a file.
 */
public class Storage {
    /** Location of the task data file. */
    private final Path filePath;

    /** Creates the storage file and any missing parent directories. */
    public Storage(String filePath) {
        try {
            this.filePath = Path.of(filePath);
            Path parent = this.filePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            if (Files.exists(this.filePath) && !Files.isRegularFile(this.filePath)) {
                throw new OreoException("The task data location is not a regular file.");
            }
            if (Files.notExists(this.filePath)) {
                Files.createFile(this.filePath);
            }
        } catch (InvalidPathException | SecurityException | IOException e) {
            throw new OreoException("Unable to create or access the task data file.");
        }
    }

    /** Loads UTF-8 task data from disk. */
    public String load() {
        try {
            return Files.readString(filePath, StandardCharsets.UTF_8);
        } catch (IOException | SecurityException e) {
            throw new OreoException("Unable to load saved tasks.");
        }
    }

    /** Saves UTF-8 task data without exposing a partially written file. */
    public void save(String content) {
        try {
            Path temporaryDirectory = filePath.toAbsolutePath().getParent();
            Path temporaryFile = Files.createTempFile(temporaryDirectory, filePath.getFileName().toString(), ".tmp");
            try {
                Files.writeString(temporaryFile, content, StandardCharsets.UTF_8,
                        StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
                replaceFile(temporaryFile);
            } finally {
                Files.deleteIfExists(temporaryFile);
            }
        } catch (IOException | SecurityException e) {
            throw new OreoException("Unable to save tasks.");
        }
    }

    /** Replaces the data file atomically where the file system supports it. */
    private void replaceFile(Path temporaryFile) throws IOException {
        try {
            Files.move(temporaryFile, filePath, StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(temporaryFile, filePath, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}


