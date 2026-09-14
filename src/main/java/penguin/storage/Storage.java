package penguin.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import penguin.exception.PenguinException;

/**
 * Handles loading and saving Penguin data in a file.
 */
public class Storage {
    /** Path of the file used to store task data. */
    private final Path filePath;

    /**
     * Creates storage using the given file path.
     *
     * @param taskFilePath path of the task data file.
     */
    public Storage(String taskFilePath) {
        this.filePath = Path.of(taskFilePath);
    }

    /**
     * Writes task data to the storage file, creating its parent directory if necessary.
     *
     * @param taskLines lines to write to the storage file.
     * @throws PenguinException if the file cannot be written.
     */
    public void saveTaskLines(List<String> taskLines) throws PenguinException {
        try {
            Path parent = filePath.getParent();

            if (parent != null && Files.notExists(parent)) {
                Files.createDirectories(parent);
            }

            Path temporaryPath = Files.createTempFile(parent == null ? Path.of(".") : parent,
                    "penguin-storage-", ".tmp");
            try {
                Files.write(temporaryPath, taskLines, StandardCharsets.UTF_8);
                replaceStorageFile(temporaryPath);
            } finally {
                Files.deleteIfExists(temporaryPath);
            }
        } catch (IOException e) {
            throw new PenguinException("Unable to save tasks: " + e.getMessage());
        }
    }

    /**
     * Replaces the storage file atomically when the filesystem supports it.
     *
     * @param temporaryPath temporary file containing the new task data.
     * @throws IOException if the replacement cannot be completed.
     */
    private void replaceStorageFile(Path temporaryPath) throws IOException {
        try {
            Files.move(temporaryPath, filePath, StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(temporaryPath, filePath, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * Reads all saved task lines from the storage file.
     *
     * @return saved task lines, or an empty list if the file does not exist.
     * @throws PenguinException if the file cannot be read.
     */
    public List<String> loadTaskLines() throws PenguinException {
        try {
            if (Files.notExists(filePath)) {
                return new ArrayList<>();
            }

            return new ArrayList<>(Files.readAllLines(filePath, StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new PenguinException("Unable to load tasks: " + e.getMessage());
        }
    }
}
