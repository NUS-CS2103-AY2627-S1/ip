package james.storage;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

import james.exception.UserInputException;
import james.task.Task;
import james.task.TaskList;

/**
 * Handles reading tasks from and writing tasks to the file system.
 */
public class Storage {
    private static final String INVALID_TASK_WARNING_PREFIX = "Warning: Skipping invalid saved task at line ";
    private static final String READ_ERROR_WARNING_PREFIX = "Warning: Error reading saved tasks file.";

    private final Path filePath;
    // Prevents a partial or failed load from replacing the original data.
    private boolean hasLoadErrors;
    private boolean wasSaveLocked;
    private final ArrayList<String> loadWarnings = new ArrayList<>();

    /**
     * Constructs a Storage instance with the given file path.
     *
     * @param filePath Path to the persistent storage file.
     */
    public Storage(String filePath) {
        this.filePath = Path.of(filePath);
    }

    /**
     * Loads tasks from the persistent storage file on disk into memory.
     * If the file does not exist, an empty list is returned.
     * Corrupted lines are skipped gracefully with a warning.
     *
     * @return An ArrayList containing the loaded tasks.
     */
    public ArrayList<Task> load() {
        ArrayList<Task> loadedTasks = new ArrayList<>();
        hasLoadErrors = false;
        loadWarnings.clear();
        try {
            if (Files.notExists(filePath)) {
                return loadedTasks;
            }
            try (BufferedReader reader = Files.newBufferedReader(filePath)) {
                String line;
                int lineNumber = 0;
                while ((line = reader.readLine()) != null) {
                    lineNumber++;
                    if (line.isBlank()) {
                        continue;
                    }
                    try {
                        Task task = Task.fromFileString(line);
                        if (loadedTasks.stream().anyMatch(existing -> existing.hasSameDetails(task))) {
                            throw new UserInputException("Duplicate saved task.");
                        }
                        loadedTasks.add(task);
                    } catch (UserInputException e) {
                        hasLoadErrors = true;
                        loadWarnings.add(INVALID_TASK_WARNING_PREFIX + lineNumber + ".");
                    }
                }
            }
        } catch (IOException | SecurityException e) {
            hasLoadErrors = true;
            loadWarnings.add(READ_ERROR_WARNING_PREFIX);
        }
        return loadedTasks;
    }

    /**
     * Returns load diagnostics and recovery guidance for the user interface to display.
     *
     * @return Warning text without a trailing newline, or an empty string after a successful load.
     */
    public String getLoadWarning() {
        if (!hasLoadErrors) {
            return "";
        }
        return String.join("\n", loadWarnings) +
                "\nSome saved tasks could not be loaded. Changes are disabled.\n" +
                "Repair the saved file or restore read access, then restart James.";
    }

    /**
     * Reports whether saving is blocked to protect incompletely loaded data.
     *
     * @return True if the storage file needs repair or its access needs restoring.
     */
    public boolean hasLoadErrors() {
        return hasLoadErrors;
    }

    /**
     * Reports whether the latest save failed because another writer held the storage lock.
     *
     * @return True if saving can be retried after the other writer releases its lock.
     */
    public boolean wasSaveLocked() {
        return wasSaveLocked;
    }

    /**
     * Saves all current tasks in the task list to the persistent storage file.
     * Automatically creates any necessary parent directories.
     *
     * @param taskList The TaskList whose tasks are to be saved.
     * @return True if the complete task list was saved successfully.
     */
    public boolean save(TaskList taskList) {
        wasSaveLocked = false;
        if (hasLoadErrors) {
            return false;
        }
        try {
            Path destination = filePath.toAbsolutePath();
            Files.createDirectories(destination.getParent());
            Path lockPath = destination.resolveSibling(destination.getFileName() + ".lock");
            // Keep the lock file: deleting it could let writers lock different files at the same path.
            try (FileChannel channel = FileChannel.open(lockPath,
                    StandardOpenOption.CREATE, StandardOpenOption.WRITE);
                    FileLock lock = channel.tryLock()) {
                if (lock == null) {
                    wasSaveLocked = true;
                    return false;
                }
                return writeTasks(taskList, destination);
            }
        } catch (OverlappingFileLockException e) {
            wasSaveLocked = true;
            return false;
        } catch (IOException | SecurityException e) {
            return false;
        }
    }

    /**
     * Replaces the saved tasks atomically while the caller holds the writer lock.
     *
     * @param taskList Complete task list to save.
     * @param destination Absolute path of the storage file.
     * @return True if the complete task list was saved successfully.
     */
    private boolean writeTasks(TaskList taskList, Path destination) {
        Path temporaryFile = null;
        try {
            temporaryFile = Files.createTempFile(destination.getParent(), "james-", ".tmp");
            StringBuilder contents = new StringBuilder();
            for (Task task : taskList.getTasks()) {
                contents.append(task.toFileString()).append(System.lineSeparator());
            }
            Files.writeString(temporaryFile, contents);
            // Replace only after the complete new contents have been written.
            Files.move(temporaryFile, destination, StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (IOException | SecurityException e) {
            return false;
        } finally {
            if (temporaryFile != null) {
                try {
                    Files.deleteIfExists(temporaryFile);
                } catch (IOException | SecurityException e) {
                    // A leftover temporary file does not change the saved task list.
                }
            }
        }
    }
}
