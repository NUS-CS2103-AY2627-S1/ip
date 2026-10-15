package sage.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import sage.core.TaskList;
import sage.exception.SageException;
import sage.task.Task;

/**
 * Loads task records and replaces saved data only after a complete new file has been written.
 */
public class Storage {
    private final Path filePath;
    private final String pathError;

    /**
     * Blocks saves after a failed load so an empty or partial list cannot overwrite existing data.
     */
    private boolean hasLoadFailed;

    /**
     * Creates storage for the given path, retaining invalid-path errors for a user-facing response.
     *
     * @param filePath The location of the serialized task data.
     */
    public Storage(String filePath) {
        Path resolvedPath = null;
        String error = "";
        try {
            if (filePath == null || filePath.isBlank()) {
                throw new InvalidPathException("", "Missing path");
            }
            resolvedPath = Path.of(filePath).toAbsolutePath();
        } catch (InvalidPathException | SecurityException exception) {
            error = "The data file path is invalid or inaccessible. Choose a writable data file and restart Sage.";
        }
        this.filePath = resolvedPath;
        pathError = error;
    }

    /**
     * Loads all records or returns an empty list when the data file has not been created yet.
     * A malformed or inaccessible file is left untouched and disables writes until a successful reload.
     *
     * @return The complete saved list.
     * @throws SageException If any record is invalid or the file cannot be read.
     */
    public List<Task> load() throws SageException {
        hasLoadFailed = true;
        requireValidPath();
        try {
            List<Task> loadedTasks = new ArrayList<>();
            if (!Files.notExists(filePath)) {
                for (String line : Files.readAllLines(filePath, StandardCharsets.UTF_8)) {
                    if (!line.isBlank()) {
                        loadedTasks.add(TaskCodec.parse(line));
                        if (loadedTasks.size() > TaskList.MAX_TASKS) {
                            throw new IllegalArgumentException("The saved list exceeds the supported capacity");
                        }
                    }
                }
            }
            hasLoadFailed = false;
            // Earlier releases allowed duplicates; preserve them while rejecting newly added duplicates.
            return loadedTasks;
        } catch (IOException | IllegalArgumentException | SecurityException exception) {
            throw new SageException("I couldn't read the saved data. Your file has been left untouched. "
                    + "Back it up, fix its contents or access permissions, then restart Sage. "
                    + "Changes are disabled to protect your data.", exception);
        }
    }

    /**
     * Writes a complete temporary file before replacing the destination.
     * Uses an atomic replacement when supported by the destination file system.
     *
     * @param tasks The tasks to persist.
     * @throws SageException If saving is blocked or the new data cannot be written.
     */
    public void save(List<Task> tasks) throws SageException {
        requireValidPath();
        if (hasLoadFailed) {
            throw new SageException("Changes are disabled because the saved data could not be read. "
                    + "Your original file is untouched. Fix it or its permissions, then restart Sage.");
        }
        List<String> records = tasks.stream().map(TaskCodec::serialize).toList();
        Path temporaryFile = null;
        try {
            Path directory = filePath.getParent();
            Files.createDirectories(directory);
            temporaryFile = Files.createTempFile(directory, ".sage-", ".tmp");
            Files.write(temporaryFile, records, StandardCharsets.UTF_8);
            replaceFile(temporaryFile);
        } catch (IOException | SecurityException exception) {
            throw new SageException("I couldn't save that change, so it has not been applied. "
                    + "Check the data folder's permissions and available disk space, then try again.", exception);
        } finally {
            deleteTemporaryFile(temporaryFile);
        }
    }

    /**
     * Replaces the saved file, falling back when atomic moves are unavailable.
     *
     * @param temporaryFile The complete new data file in the destination directory.
     * @throws IOException If the replacement cannot be completed.
     */
    private void replaceFile(Path temporaryFile) throws IOException {
        try {
            Files.move(temporaryFile, filePath,
                    StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException exception) {
            Files.move(temporaryFile, filePath, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * Removes a leftover temporary file without masking the original save result.
     *
     * @param temporaryFile The temporary path, or null if file creation failed.
     */
    private void deleteTemporaryFile(Path temporaryFile) {
        if (temporaryFile == null) {
            return;
        }
        try {
            Files.deleteIfExists(temporaryFile);
        } catch (IOException | SecurityException ignored) {
            // A failed cleanup must not turn a successful save into an apparent command failure.
        }
    }

    /**
     * Reports constructor path errors through the same checked error channel as other storage failures.
     *
     * @throws SageException If the supplied path cannot be used.
     */
    private void requireValidPath() throws SageException {
        if (!pathError.isEmpty()) {
            throw new SageException(pathError);
        }
    }
}
