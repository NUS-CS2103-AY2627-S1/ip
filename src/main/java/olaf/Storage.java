package olaf;

import java.io.IOException;
import java.nio.charset.MalformedInputException;
import java.nio.file.AccessDeniedException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads and saves Olaf's tasks in a UTF-8 text file.
 */
final class Storage {
    private final Path filePath;
    private final StorageFiles files;
    private final TaskCodec taskCodec = new TaskCodec();

    Storage(Path filePath) {
        this(filePath, new StorageFiles());
    }

    /** Creates storage with injectable I/O operations for deterministic failure tests. */
    Storage(Path filePath, StorageFiles files) {
        this.filePath = filePath.toAbsolutePath().normalize();
        this.files = files;
    }

    /**
     * Loads all tasks, treating a missing data file as an empty task list.
     *
     * @return loaded tasks in their stored order
     * @throws StorageException if an existing file cannot be read or parsed
     */
    TaskList load() throws StorageException {
        if (Files.notExists(filePath)) {
            return new TaskList();
        }

        try {
            if (Files.isDirectory(filePath)) {
                throw new IOException("The data path is a directory, not a file");
            }
            List<String> records = files.readRecords(filePath);
            List<Task> tasks = new ArrayList<>();
            for (int index = 0; index < records.size(); index++) {
                tasks.add(taskCodec.decode(records.get(index), index + 1));
            }
            return new TaskList(tasks);
        } catch (IOException | StorageException exception) {
            throw new StorageException("Unable to load task data from " + filePath + ": "
                    + describeFailure(exception) + ". Check that the path is a readable UTF-8 file. "
                    + "Repair the indicated record or restore a backup, then restart Olaf. "
                    + "The file has not been changed.", exception);
        }
    }

    /**
     * Saves to a temporary sibling before atomically replacing the previous snapshot.
     * A failed write or replacement never truncates the saved file.
     *
     * @param tasks task list to save.
     * @throws StorageException if the directory or data file cannot be written
     */
    void save(TaskList tasks) throws StorageException {
        List<String> records = tasks.getTasks().stream().map(taskCodec::encode).toList();
        Path temporaryFile = null;
        try {
            Path parentDirectory = filePath.getParent();
            Files.createDirectories(parentDirectory);
            temporaryFile = Files.createTempFile(parentDirectory, "olaf-", ".tmp");
            files.writeRecords(temporaryFile, records);
            files.replace(temporaryFile, filePath);
        } catch (IOException exception) {
            StorageException failure = new StorageException("Unable to save task data to " + filePath + ": "
                    + describeFailure(exception) + ". The change was not saved; your tasks are unchanged. "
                    + "Check the storage location, permissions, and free space, then retry the command.", exception);
            removeTemporaryFile(temporaryFile, failure);
            throw failure;
        }
    }

    /** Keeps a cleanup error from hiding the failure that prevented the save. */
    private void removeTemporaryFile(Path temporaryFile, StorageException failure) {
        if (temporaryFile == null) {
            return;
        }
        try {
            files.deleteIfExists(temporaryFile);
        } catch (IOException exception) {
            failure.addSuppressed(exception);
        }
    }

    private String describeFailure(Exception exception) {
        if (exception instanceof AtomicMoveNotSupportedException) {
            return "atomic file replacement is not supported at this storage location";
        }
        if (exception instanceof AccessDeniedException) {
            return "access to the file or directory was denied";
        }
        if (exception instanceof MalformedInputException) {
            return "the file is not valid UTF-8";
        }
        String message = exception.getMessage();
        if (message == null) {
            return exception.getClass().getSimpleName();
        }
        return message.endsWith(".") ? message.substring(0, message.length() - 1) : message;
    }
}
