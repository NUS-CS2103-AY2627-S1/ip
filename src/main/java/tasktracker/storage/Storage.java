package tasktracker.storage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import tasktracker.exception.TaskTrackerException;
import tasktracker.task.Deadline;
import tasktracker.task.Event;
import tasktracker.task.Fixed;
import tasktracker.task.Task;
import tasktracker.task.TaskDateTime;
import tasktracker.task.ToDo;
import tasktracker.ui.Message;

/**
 * Handles the loading and saving of task data to and from a local file.
 * Features atomic writes and resilient line-by-line loading that skips corrupted entries.
 * <p>
 * Note: Gemini AI was used here, especially for the save and load methods.
 * Prompt "I want to save all the user input and list as a log file."
 * Prompt "This is one sample format for the file, which we can follow."
 * Prompt "How do we convert the saved file back when loading?"
 * Add code only after an intermediate level of understanding achieved.
 */
public class Storage {
    private static final String DELIMITER_REGEX = " \\| ";
    private static final String STATUS_DONE = "1";
    private static final String STATUS_NOT_DONE = "0";
    private static final String TYPE_TODO = "T";
    private static final String TYPE_DEADLINE = "D";
    private static final String TYPE_EVENT = "E";
    private static final String TYPE_FIXED = "F";
    private static final String TEMP_FILE_PREFIX = "tasks_";
    private static final String TEMP_FILE_SUFFIX = ".tmp";

    private final String filePath;
    private final List<String> loadWarnings = new ArrayList<>();

    /**
     * Constructs a {@code Storage} instance with the specified file path.
     *
     * @param filePath The path where tasks are saved.
     */
    public Storage(String filePath) {
        assert filePath != null && !filePath.trim().isEmpty()
                : "File path for Storage must not be null or blank.";
        this.filePath = filePath;
    }

    /**
     * Returns an unmodifiable list of warning messages generated during the most recent load.
     *
     * @return List of warnings detailing skipped corrupted lines.
     */
    public List<String> getLoadWarnings() {
        return Collections.unmodifiableList(loadWarnings);
    }

    /**
     * Saves the provided list of tasks to the storage file atomically.
     *
     * @param tasks The list of tasks to be saved.
     * @throws TaskTrackerException If an I/O error occurs while writing to the file.
     */
    public void save(List<Task> tasks) throws TaskTrackerException {
        assert tasks != null : "Task list to save must not be null.";
        assert filePath != null && !filePath.trim().isEmpty() : "File path invariant violated.";
        Path targetPath = Paths.get(filePath);
        Path parentDir = targetPath.getParent();

        try {
            ensureDirectoryExists(parentDir);
            Path tempPath = writeTasksToTempFile(parentDir, tasks);
            replaceTargetFile(tempPath, targetPath);
        } catch (IOException e) {
            throw new TaskTrackerException(Message.ERR_FILE_SAVE + e.getMessage());
        }
    }

    /**
     * Ensures that the parent directory for the storage file exists.
     *
     * @param parentDir Directory path to verify or create.
     * @throws IOException If directory creation fails.
     */
    private void ensureDirectoryExists(Path parentDir) throws IOException {
        if (parentDir != null) {
            Files.createDirectories(parentDir);
        }
    }

    /**
     * Writes serialized task entries into a temporary file prior to atomic move.
     *
     * @param parentDir Directory in which to create the temporary file.
     * @param tasks List of tasks to write.
     * @return Path to the generated temporary file.
     * @throws IOException If file creation or writing fails.
     */
    private Path writeTasksToTempFile(Path parentDir, List<Task> tasks) throws IOException {
        Path tempPath = (parentDir != null)
                ? Files.createTempFile(parentDir, TEMP_FILE_PREFIX, TEMP_FILE_SUFFIX)
                : Files.createTempFile(TEMP_FILE_PREFIX, TEMP_FILE_SUFFIX);

        try (BufferedWriter writer = Files.newBufferedWriter(tempPath)) {
            for (Task task : tasks) {
                assert task != null : "Cannot serialize a null task to file.";
                writer.write(task.toFileFormat());
                writer.newLine();
            }
        }
        return tempPath;
    }

    /**
     * Atomically replaces the target save file with the temporary file.
     *
     * @param source Temporary source file path.
     * @param target Final destination file path.
     * @throws IOException If moving or replacing the file fails.
     */
    private void replaceTargetFile(Path source, Path target) throws IOException {
        try {
            Files.move(source, target,
                    StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * Loads tasks from the storage file upon application startup.
     * Skips corrupted lines so valid tasks remain accessible.
     *
     * @return A list of tasks successfully parsed from the file.
     * @throws TaskTrackerException If an unrecoverable I/O error occurs.
     */
    public List<Task> load() throws TaskTrackerException {
        List<Task> loadedTasks = new ArrayList<>();
        loadWarnings.clear();
        File file = new File(filePath);

        if (!file.exists()) {
            return loadedTasks;
        }

        try (Scanner scanner = new Scanner(file)) {
            int lineNumber = 0;
            while (scanner.hasNextLine()) {
                lineNumber++;
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) {
                    continue;
                }
                try {
                    loadedTasks.add(parseTaskFromLine(line));
                } catch (TaskTrackerException e) {
                    loadWarnings.add("Line " + lineNumber + ": " + line);
                }
            }
        } catch (IOException e) {
            throw new TaskTrackerException(Message.ERR_FILE_LOAD + e.getMessage());
        }
        return loadedTasks;
    }

    /**
     * Parses a raw line from the save file into a {@code Task} instance.
     *
     * @param line Raw pipe-delimited storage record.
     * @return Reconstructed task instance.
     * @throws TaskTrackerException If required fields are missing or corrupted.
     */
    private Task parseTaskFromLine(String line) throws TaskTrackerException {
        String[] parts = line.split(DELIMITER_REGEX);
        if (parts.length < 3) {
            throw new TaskTrackerException(Message.ERR_FILE_CORRUPT + line);
        }

        String type = parts[0].trim();
        String status = parts[1].trim();
        String description = parts[2].trim();

        if (description.isEmpty()) {
            throw new TaskTrackerException(Message.ERR_FILE_CORRUPT + line);
        }

        boolean isDone = parseTaskStatus(status, line);
        Task task = instantiateTask(type, description, parts, line);

        if (isDone) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Parses the completion status token from a saved record.
     *
     * @param status Status string ({@code "0"} or {@code "1"}).
     * @param line Full record used for error context.
     * @return {@code true} if completed, {@code false} otherwise.
     * @throws TaskTrackerException If status code is invalid.
     */
    private boolean parseTaskStatus(String status, String line) throws TaskTrackerException {
        if (!status.equals(STATUS_NOT_DONE) && !status.equals(STATUS_DONE)) {
            throw new TaskTrackerException(Message.ERR_FILE_CORRUPT + line);
        }
        return status.equals(STATUS_DONE);
    }

    /**
     * Instantiates the appropriate concrete {@code Task} subclass based on the type tag.
     *
     * @param type Task type identifier.
     * @param description Textual description of the task.
     * @param parts Split parts of the raw file record.
     * @param line Original record string for error messages.
     * @return Concrete task instance.
     * @throws TaskTrackerException If the type tag is unknown or attributes fail parsing.
     */
    private Task instantiateTask(String type, String description, String[] parts, String line)
            throws TaskTrackerException {
        switch (type) {
            case TYPE_TODO -> {
                return new ToDo(description);
            }
            case TYPE_DEADLINE -> {
                return createDeadline(description, parts, line);
            }
            case TYPE_EVENT -> {
                return createEvent(description, parts, line);
            }
            case TYPE_FIXED -> {
                return createFixedDurationTask(description, parts, line);
            }
            default -> throw new TaskTrackerException(Message.ERR_FILE_UNKNOWN + type);
        }
    }

    /**
     * Reconstructs a {@code Deadline} task from parsed storage tokens.
     *
     * @param description Textual description of the task.
     * @param parts Split record elements.
     * @param line Full record string for error diagnostics.
     * @return Parsed {@code Deadline} instance.
     * @throws TaskTrackerException If timestamp tokens are missing or invalid.
     */
    private Deadline createDeadline(String description, String[] parts, String line)
            throws TaskTrackerException {
        if (parts.length < 4) {
            throw new TaskTrackerException(Message.ERR_FILE_DEADLINE + line);
        }
        try {
            return new Deadline(description, new TaskDateTime(parts[3].trim()));
        } catch (TaskTrackerException | DateTimeParseException | IllegalArgumentException e) {
            throw new TaskTrackerException(Message.ERR_FILE_CORRUPT + line);
        }
    }

    /**
     * Reconstructs an {@code Event} task from parsed storage tokens.
     *
     * @param description Textual description of the task.
     * @param parts Split record elements.
     * @param line Full record string for error diagnostics.
     * @return Parsed {@code Event} instance.
     * @throws TaskTrackerException If time boundaries are missing or invalid.
     */
    private Event createEvent(String description, String[] parts, String line)
            throws TaskTrackerException {
        if (parts.length < 5) {
            throw new TaskTrackerException(Message.ERR_FILE_EVENT + line);
        }
        try {
            return new Event(description, new TaskDateTime(parts[3].trim()),
                    new TaskDateTime(parts[4].trim()));
        } catch (TaskTrackerException | DateTimeParseException | IllegalArgumentException e) {
            throw new TaskTrackerException(Message.ERR_FILE_CORRUPT + line);
        }
    }

    /**
     * Reconstructs a {@code Fixed} duration task from parsed storage tokens.
     *
     * @param description Textual description of the task.
     * @param parts Split record elements.
     * @param line Full record string for error diagnostics.
     * @return Parsed {@code Fixed} instance.
     * @throws TaskTrackerException If duration field is missing or empty.
     */
    private Fixed createFixedDurationTask(String description, String[] parts, String line)
            throws TaskTrackerException {
        if (parts.length < 4) {
            throw new TaskTrackerException(Message.ERR_FILE_FIXED + line);
        }
        String duration = parts[3].trim();
        if (duration.isEmpty()) {
            throw new TaskTrackerException(Message.ERR_FILE_CORRUPT + line);
        }
        return new Fixed(description, duration);
    }
}
