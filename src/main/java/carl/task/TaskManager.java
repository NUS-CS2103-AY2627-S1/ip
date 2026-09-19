package carl.task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import carl.exceptions.CarlStorageException;
import carl.util.DateParser;

/**
 * Manages loading and saving tasks to the local storage disk.
 */
public class TaskManager {
    private static final String SAVE_FILE_PATH = "./save.txt";
    private static final int MAX_SAVED_TASK_NAME_LENGTH = 500;

    private final Path saveFilePath;
    private String startupWarning;

    /** Creates a task manager that uses Carl's default save-file location. */
    public TaskManager() {
        this(Path.of(SAVE_FILE_PATH));
    }

    /**
     * Creates a task manager using a specific path, primarily for isolated testing.
     *
     * @param saveFilePath path of the task save file.
     */
    public TaskManager(Path saveFilePath) {
        this.saveFilePath = Objects.requireNonNull(saveFilePath);
    }

    /**
     * Loads valid saved tasks and skips malformed or duplicate entries.
     *
     * @return valid tasks loaded from the file.
     */
    public List<Task> loadSave() {
        List<Task> tasks = new ArrayList<>();
        List<Integer> invalidLineNumbers = new ArrayList<>();
        startupWarning = null;

        try {
            loadSavedLines(tasks, invalidLineNumbers);
        } catch (IOException | SecurityException e) {
            setUnreadableFileWarning();
            return tasks;
        }

        setInvalidLinesWarning(invalidLineNumbers);
        return tasks;
    }

    /** Loads each non-blank save-file line and records lines that cannot be restored safely. */
    private void loadSavedLines(List<Task> tasks, List<Integer> invalidLineNumbers) throws IOException {
        List<String> lines = Files.readAllLines(saveFilePath, StandardCharsets.UTF_8);
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (!line.isBlank() && !tryAddSavedTask(line, tasks)) {
                invalidLineNumbers.add(i + 1);
            }
        }
    }

    /** Parses and adds one saved task, returning false for malformed or duplicate data. */
    private boolean tryAddSavedTask(String line, List<Task> tasks) {
        try {
            Task task = parseStringToTask(line);
            if (tasks.stream().anyMatch(existingTask -> existingTask.hasSameDetails(task))) {
                return false;
            }
            tasks.add(task);
            return true;
        } catch (IllegalArgumentException | DateTimeParseException e) {
            return false;
        }
    }

    /** Records a warning when the save file cannot be read. */
    private void setUnreadableFileWarning() {
        startupWarning = "Carl could not read `" + saveFilePath
                + "`. Check that the file exists and is readable. Starting with an empty task list.";
    }

    /** Records skipped line numbers while leaving valid loaded tasks available. */
    private void setInvalidLinesWarning(List<Integer> invalidLineNumbers) {
        if (!invalidLineNumbers.isEmpty()) {
            startupWarning = "Skipped invalid or duplicate saved task data on line(s): "
                    + invalidLineNumbers + ". Valid tasks were loaded normally.";
        }
    }

    /** Converts one validated save-file line into a task. */
    private Task parseStringToTask(String line) {
        String[] fields = splitSavedFields(line);
        TaskType type = TaskType.parsePrefix(fields[0].strip());
        validateFieldCount(type, fields);

        TaskStatus status = parseTaskStatus(fields[1]);
        String name = parseTaskName(fields[2]);
        return createTask(type, status, name, fields);
    }

    /** Splits a save-file line and rejects lines missing the common task fields. */
    private String[] splitSavedFields(String line) {
        String[] fields = line.split("\\s*\\|\\s*", -1);
        if (fields.length < 3) {
            throw new IllegalArgumentException("Missing saved task fields");
        }
        return fields;
    }

    /** Ensures the saved field count matches the encoded task type. */
    private void validateFieldCount(TaskType type, String[] fields) {
        int expectedFieldCount = switch (type) {
            case TODO -> 3;
            case DEADLINE -> 4;
            case EVENT -> 5;
        };
        if (fields.length != expectedFieldCount) {
            throw new IllegalArgumentException("Unexpected saved task fields");
        }
    }

    /** Converts a saved completion flag into its task status. */
    private TaskStatus parseTaskStatus(String field) {
        return switch (field.strip()) {
            case "0" -> TaskStatus.NOT_DONE;
            case "1" -> TaskStatus.DONE;
            default -> throw new IllegalArgumentException("Invalid task status");
        };
    }

    /** Validates and returns a saved task description. */
    private String parseTaskName(String field) {
        String name = field.strip();
        if (name.isEmpty() || name.length() > MAX_SAVED_TASK_NAME_LENGTH) {
            throw new IllegalArgumentException("Invalid task description");
        }
        return name;
    }

    /** Creates the task subtype represented by validated save-file fields. */
    private Task createTask(TaskType type, TaskStatus status, String name, String[] fields) {
        return switch (type) {
            case TODO -> buildTask(type, status, name, null, null);
            case DEADLINE -> buildTask(type, status, name, parseDateTime(fields[3]), null);
            case EVENT -> createEventTask(type, status, name, fields);
        };
    }

    /** Creates an event after validating that its saved time range is chronological. */
    private Task createEventTask(TaskType type, TaskStatus status, String name, String[] fields) {
        LocalDateTime from = parseDateTime(fields[3]);
        LocalDateTime to = parseDateTime(fields[4]);
        if (!from.isBefore(to)) {
            throw new IllegalArgumentException("Invalid event time range");
        }
        return buildTask(type, status, name, from, to);
    }

    /** Parses a saved date-time field using Carl's strict storage format. */
    private LocalDateTime parseDateTime(String field) {
        return DateParser.parseDateTime(field.strip());
    }

    /** Passes validated values to the task factory. */
    private Task buildTask(TaskType type, TaskStatus status, String name,
                           LocalDateTime from, LocalDateTime to) {
        return Task.createFromData(new Task.TaskData(type, status, name, from, to));
    }

    /**
     * Saves all tasks using a temporary file so an interrupted write cannot corrupt existing data.
     *
     * @param tasks task list to save.
     * @throws CarlStorageException If the destination cannot be written.
     */
    public void saveAll(TaskList tasks) throws CarlStorageException {
        Path absoluteSavePath = saveFilePath.toAbsolutePath();
        Path parentDirectory = absoluteSavePath.getParent();
        Path temporaryFile = null;

        try {
            if (parentDirectory != null) {
                Files.createDirectories(parentDirectory);
            }
            temporaryFile = Files.createTempFile(parentDirectory, ".carl-save-", ".tmp");
            Files.writeString(temporaryFile, tasks.toSaveString(), StandardCharsets.UTF_8);
            moveIntoPlace(temporaryFile, absoluteSavePath);
            temporaryFile = null;
        } catch (IOException | SecurityException e) {
            throw new CarlStorageException(
                    "Carl could not save your changes. Check access to `" + saveFilePath + "` and try again.", e);
        } finally {
            deleteTemporaryFile(temporaryFile);
        }
    }

    /** Removes an incomplete temporary save without masking the original storage result. */
    private void deleteTemporaryFile(Path temporaryFile) {
        if (temporaryFile == null) {
            return;
        }
        try {
            Files.deleteIfExists(temporaryFile);
        } catch (IOException | SecurityException ignored) {
            // The original save error is more useful than a temporary-file cleanup failure.
        }
    }

    /** Moves a completed temporary save into place, using an atomic move when supported. */
    private void moveIntoPlace(Path temporaryFile, Path destination) throws IOException {
        try {
            Files.move(temporaryFile, destination,
                    StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(temporaryFile, destination, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * Creates the save file when missing, then loads its valid contents.
     *
     * @return loaded tasks, or an empty list when the save file cannot be accessed.
     */
    public List<Task> createSave() {
        startupWarning = null;
        try {
            Path absoluteSavePath = saveFilePath.toAbsolutePath();
            Path parentDirectory = absoluteSavePath.getParent();
            if (parentDirectory != null) {
                Files.createDirectories(parentDirectory);
            }
            if (Files.notExists(absoluteSavePath)) {
                Files.createFile(absoluteSavePath);
            }
        } catch (IOException | SecurityException e) {
            startupWarning = "Carl could not create or access `" + saveFilePath
                    + "`. Check the folder permissions. Changes may not be saved.";
            return new ArrayList<>();
        }
        return loadSave();
    }

    /**
     * Returns a recoverable loading warning to show after startup.
     *
     * @return warning text, or {@code null} when storage loaded normally.
     */
    public String getStartupWarning() {
        return startupWarning;
    }
}
