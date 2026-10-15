package gooble.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Handles reading and writing Gooble's task storage file.
 */
public class Storage {
    private final Path storagePath;

    /**
     * Creates storage backed by the given file.
     *
     * @param filePath path to the task file
     */
    public Storage(Path filePath) {
        storagePath = filePath;
    }

    /** Loads all stored records, returning an empty list when unavailable. */
    public List<String> load() {
        try {
            if (!Files.isRegularFile(storagePath)) {
                return Collections.emptyList();
            }
            return Files.readAllLines(storagePath, StandardCharsets.UTF_8);
        } catch (IOException | SecurityException e) {
            reportWarning("Unable to load tasks from disk.");
            return Collections.emptyList();
        }
    }

    /** Atomically replaces the storage file with the supplied records. */
    public void save(List<String> records) {
        Path temporaryPath = storagePath.resolveSibling(storagePath.getFileName() + ".tmp");
        try {
            createParentDirectories();
            Files.write(temporaryPath, new ArrayList<>(records), StandardCharsets.UTF_8);
            moveIntoPlace(temporaryPath);
        } catch (IOException | SecurityException e) {
            deleteTemporaryFile(temporaryPath);
            reportWarning("Unable to save tasks to disk.");
        }
    }

    private void createParentDirectories() throws IOException {
        Path parent = storagePath.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
    }

    private void moveIntoPlace(Path temporaryPath) throws IOException {
        try {
            Files.move(temporaryPath, storagePath, StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(temporaryPath, storagePath, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private void deleteTemporaryFile(Path temporaryPath) {
        try {
            Files.deleteIfExists(temporaryPath);
        } catch (IOException | SecurityException ignored) {
            // The original save error is more useful to the caller.
        }
    }

    /** Writes a storage warning without exposing filesystem details to callers. */
    private void reportWarning(String message) {
        System.err.println("Warning: " + message);
    }
}
