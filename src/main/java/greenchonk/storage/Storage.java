package greenchonk.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import greenchonk.exception.StorageException;
import greenchonk.task.Task;
import greenchonk.task.TaskList;

/**
 * Loads tasks from a data file and saves task-list changes to that file.
 */
public class Storage {
    private final Path dataFile;
    private final TaskCodec taskCodec;
    private boolean hasCorruptedData;

    /**
     * Creates storage backed by the specified file.
     *
     * @param filePath the path of the task data file.
     */
    public Storage(String filePath) {
        dataFile = Path.of(filePath).toAbsolutePath();
        taskCodec = new TaskCodec();
    }

    /**
     * Loads saved tasks, creating the data directory and file on first use.
     *
     * @return the tasks restored from disk.
     * @throws StorageException if the data file cannot be read or contains invalid data.
     */
    public List<Task> load() throws StorageException {
        List<Task> tasks = new ArrayList<>();
        try {
            Files.createDirectories(dataFile.getParent());
            if (Files.notExists(dataFile)) {
                Files.createFile(dataFile);
            }

            List<String> lines = Files.readAllLines(dataFile, StandardCharsets.UTF_8);
            for (int lineNumber = 1; lineNumber <= lines.size(); lineNumber++) {
                String line = lines.get(lineNumber - 1);
                if (!line.isBlank()) {
                    try {
                        tasks.add(taskCodec.decode(line, lineNumber));
                    } catch (StorageException exception) {
                        hasCorruptedData = true;
                        throw exception;
                    }
                }
            }
            hasCorruptedData = false;
        } catch (IOException | SecurityException exception) {
            throw new StorageException("I couldn't load the task list: " + exception.getMessage(),
                    exception);
        }
        return tasks;
    }

    /**
     * Replaces the data file contents with the current task list.
     *
     * @param tasks the tasks to persist.
     * @throws StorageException if the tasks cannot be encoded or written.
     */
    public void save(TaskList tasks) throws StorageException {
        if (hasCorruptedData) {
            throw new StorageException("I won't overwrite the data file because it contains "
                    + "an invalid task. Fix or move the file, then restart Green Chonk.");
        }

        List<String> lines = new ArrayList<>();
        for (int index = 0; index < tasks.size(); index++) {
            lines.add(taskCodec.encode(tasks.get(index)));
        }

        Path temporaryFile = null;
        try {
            Files.createDirectories(dataFile.getParent());
            temporaryFile = Files.createTempFile(dataFile.getParent(),
                    getTemporaryFilePrefix(), ".tmp");
            Files.write(temporaryFile, lines, StandardCharsets.UTF_8);
            replaceDataFile(temporaryFile);
        } catch (IOException | SecurityException exception) {
            deleteTemporaryFile(temporaryFile, exception);
            throw new StorageException("I couldn't save the task list: " + exception.getMessage(),
                    exception);
        }
    }

    /**
     * Replaces the data file with a fully written temporary file.
     * Falls back to a regular replacement when atomic moves are unavailable.
     *
     * @param temporaryFile the complete temporary data file.
     * @throws IOException if neither replacement strategy succeeds.
     */
    private void replaceDataFile(Path temporaryFile) throws IOException {
        assert temporaryFile != null : "Temporary file must be created before replacement";
        assert temporaryFile.getParent().equals(dataFile.getParent())
                : "Temporary file must be created beside the data file";

        try {
            Files.move(temporaryFile, dataFile,
                    StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException exception) {
            Files.move(temporaryFile, dataFile, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * Returns a valid prefix for temporary files created beside the data file.
     *
     * @return the data file name padded to at least three characters.
     */
    private String getTemporaryFilePrefix() {
        String fileName = dataFile.getFileName().toString();
        return fileName.length() >= 3 ? fileName : (fileName + "___").substring(0, 3);
    }

    /**
     * Removes a temporary file after a failed save without hiding the original failure.
     *
     * @param temporaryFile the temporary file to remove, or null if none was created.
     * @param originalException the failure that interrupted the save.
     */
    private static void deleteTemporaryFile(Path temporaryFile, Exception originalException) {
        if (temporaryFile == null) {
            return;
        }
        try {
            Files.deleteIfExists(temporaryFile);
        } catch (IOException | SecurityException cleanupException) {
            originalException.addSuppressed(cleanupException);
        }
    }
}
