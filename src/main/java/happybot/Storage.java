package happybot;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import happybot.task.Deadline;
import happybot.task.Event;
import happybot.task.Task;
import happybot.task.ToDo;

/**
 * Saves HappyBot tasks to a text file and loads them back on startup.
 */
public class Storage {
    /** Markers written as the first field of a saved task line. */
    private static final String TASK_TYPE_TODO = "T";
    private static final String TASK_TYPE_DEADLINE = "D";
    private static final String TASK_TYPE_EVENT = "E";

    /** Number of fields in the older storage format for each task type. */
    private static final int LEGACY_FIELD_COUNT_TODO = 3;
    private static final int LEGACY_FIELD_COUNT_DEADLINE = 4;
    private static final int LEGACY_FIELD_COUNT_EVENT = 5;

    /** Positions of the fields in a saved task line. */
    private static final int TASK_TYPE_FIELD_INDEX = 0;
    private static final int COMPLETION_STATUS_FIELD_INDEX = 1;
    private static final int DESCRIPTION_FIELD_INDEX = 2;
    private static final int FIRST_DATE_TIME_FIELD_INDEX = 3;
    private static final int SECOND_DATE_TIME_FIELD_INDEX = 4;

    /** Text placed between the fields of a saved task line. */
    private static final String FIELD_SEPARATOR = " | ";

    /** Regular expression matching FIELD_SEPARATOR, because String.split() takes a regex. */
    private static final String FIELD_SEPARATOR_REGEX = " \\| ";

    /** Completion field value written for a task that is done. */
    private static final String COMPLETION_STATUS_DONE = "1";

    /** Completion field value written for a task that is not done. */
    private static final String COMPLETION_STATUS_NOT_DONE = "0";

    /** Completion date used when no date was recorded for a task. */
    private static final String UNKNOWN_COMPLETION_DATE = "-";

    /** Suffix used for the companion file that coordinates HappyBot sessions. */
    private static final String LOCK_FILE_SUFFIX = ".lock";

    private final Path filePath;
    private final Path lockFilePath;

    /** Number of unreadable lines skipped by the most recent call to loadTasks(). */
    private int skippedLineCount;

    /** Channel that keeps this session's data-file lock alive. */
    private FileChannel lockChannel;

    /** Exclusive lock held while this HappyBot session owns the data file. */
    private FileLock dataFileLock;

    /**
     * Creates storage that reads and writes the specified file.
     *
     * @param filePath The location of the task data file.
     */
    public Storage(Path filePath) {
        this.filePath = filePath;
        this.lockFilePath = filePath.resolveSibling(filePath.getFileName() + LOCK_FILE_SUFFIX);
    }

    /**
     * Returns the number of unreadable lines skipped by the most recent call to loadTasks().
     *
     * @return The number of skipped lines.
     */
    public int getSkippedLineCount() {
        return skippedLineCount;
    }

    /**
     * Returns the location of the task data file.
     *
     * @return The data file path.
     */
    public Path getFilePath() {
        return filePath;
    }

    /**
     * Tries to acquire this process's exclusive lock for the task data file.
     *
     * <p>The companion lock file may remain after a session ends, but an operating-system lock
     * on it is released automatically when its process exits. Its mere presence therefore does
     * not prevent a later HappyBot session from opening the task data file.
     *
     * @return True if this Storage now owns the data file; false if another HappyBot owns it.
     * @throws IOException If the companion lock file cannot be opened.
     */
    public boolean tryAcquireDataFileLock() throws IOException {
        if (dataFileLock != null && dataFileLock.isValid()) {
            return true;
        }

        Path parentDirectory = lockFilePath.getParent();
        if (parentDirectory != null) {
            Files.createDirectories(parentDirectory);
        }

        lockChannel = FileChannel.open(lockFilePath, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        try {
            dataFileLock = lockChannel.tryLock();
            if (dataFileLock == null) {
                closeLockChannelQuietly();
                return false;
            }

            return true;
        } catch (OverlappingFileLockException e) {
            closeLockChannelQuietly();
            return false;
        } catch (IOException e) {
            closeLockChannelQuietly();
            throw e;
        }
    }

    /**
     * Releases this session's data-file lock.
     *
     * @throws IOException If the lock or its channel cannot be closed.
     */
    public void releaseDataFileLock() throws IOException {
        IOException failure = null;

        if (dataFileLock != null) {
            try {
                dataFileLock.release();
            } catch (IOException e) {
                failure = e;
            } finally {
                dataFileLock = null;
            }
        }

        if (lockChannel != null) {
            try {
                lockChannel.close();
            } catch (IOException e) {
                if (failure == null) {
                    failure = e;
                }
            } finally {
                lockChannel = null;
            }
        }

        if (failure != null) {
            throw failure;
        }
    }

    /**
     * Saves all tasks to the data file, replacing any previous contents.
     *
     * @param tasks The tasks to save.
     * @throws IOException If the data file cannot be written.
     */
    public void saveTasks(List<Task> tasks) throws IOException {
        // A data file named without a directory has no parent to create.
        Path parentDirectory = filePath.getParent();
        if (parentDirectory != null) {
            Files.createDirectories(parentDirectory);
        }

        List<String> taskLines = new ArrayList<>();
        for (Task task : tasks) {
            taskLines.add(formatTask(task));
        }

        Files.write(filePath, taskLines, StandardCharsets.UTF_8);
    }

    /**
     * Loads the previously saved tasks from the data file.
     *
     * <p>Lines that do not follow the storage format, including lines holding a date that
     * cannot be read, are skipped and counted rather than abandoning the tasks that were read
     * successfully.
     *
     * @return A modifiable list of the tasks that were read successfully.
     * @throws IOException If the data file cannot be read.
     */
    public List<Task> loadTasks() throws IOException {
        skippedLineCount = 0;
        List<Task> tasks = new ArrayList<>();

        if (Files.notExists(filePath)) {
            return tasks;
        }

        List<String> taskLines = Files.readAllLines(filePath, StandardCharsets.UTF_8);

        for (String taskLine : taskLines) {
            if (taskLine.isBlank()) {
                continue;
            }

            try {
                Task parsedTask = parseTask(taskLine);
                if (containsTaskWithSameDetails(tasks, parsedTask)) {
                    throw new IllegalArgumentException("Duplicate task in data file: " + taskLine);
                }

                tasks.add(parsedTask);
            } catch (IllegalArgumentException | DateTimeParseException e) {
                skippedLineCount++;
            }
        }

        return tasks;
    }

    /**
     * Converts one task into a line of the storage format.
     *
     * @param task The task to format.
     * @return The formatted task line.
     */
    private String formatTask(Task task) {
        String completionStatus = task.isDone() ? COMPLETION_STATUS_DONE : COMPLETION_STATUS_NOT_DONE;
        String completionDate = task.getCompletionDate() == null
                ? UNKNOWN_COMPLETION_DATE
                : task.getCompletionDate().toString();

        if (task instanceof ToDo) {
            return String.join(FIELD_SEPARATOR, TASK_TYPE_TODO, completionStatus,
                    task.getDescription(), completionDate);
        }

        if (task instanceof Deadline deadline) {
            return String.join(FIELD_SEPARATOR, TASK_TYPE_DEADLINE, completionStatus,
                    deadline.getDescription(), deadline.getDueDateTime().toString(), completionDate);
        }

        if (task instanceof Event event) {
            return String.join(FIELD_SEPARATOR, TASK_TYPE_EVENT, completionStatus,
                    event.getDescription(), event.getStartTime().toString(),
                    event.getEndTime().toString(), completionDate);
        }

        throw new IllegalArgumentException("Unsupported task type.");
    }

    /**
     * Converts one line of the storage format back into a task.
     *
     * @param taskLine The saved line to parse.
     * @return The task described by the line.
     * @throws IllegalArgumentException If the line does not follow the storage format.
     * @throws DateTimeParseException If a date field is not in its required ISO form.
     */
    private Task parseTask(String taskLine) {
        // Keeping trailing empty fields lets validation reject a blank completion-date field.
        String[] fields = taskLine.split(FIELD_SEPARATOR_REGEX, -1);
        checkFieldsAreFilled(fields, taskLine);

        String taskType = fields[TASK_TYPE_FIELD_INDEX];
        int legacyFieldCount = getLegacyFieldCount(taskType, taskLine);
        checkFieldCount(fields, legacyFieldCount, taskLine);
        Task task = createTask(fields, taskLine);

        boolean isDone = parseCompletionStatus(fields[COMPLETION_STATUS_FIELD_INDEX], taskLine);
        LocalDate completionDate = parseCompletionDate(fields, legacyFieldCount, isDone, taskLine);
        if (isDone) {
            task.markAsDone(completionDate);
        }

        return task;
    }

    /**
     * Creates a task from the type-specific fields of a saved line.
     *
     * @param fields The fields parsed from the line.
     * @param taskLine The original line, included in the error message.
     * @return The task described by the type-specific fields.
     * @throws IllegalArgumentException If the line has an unsupported type or invalid task details.
     * @throws DateTimeParseException If a date field is not a date and time in ISO form.
     */
    private Task createTask(String[] fields, String taskLine) {
        String taskType = fields[TASK_TYPE_FIELD_INDEX];

        return switch (taskType) {
            case TASK_TYPE_TODO:
                yield new ToDo(fields[DESCRIPTION_FIELD_INDEX]);
            case TASK_TYPE_DEADLINE:
                yield new Deadline(fields[DESCRIPTION_FIELD_INDEX],
                        LocalDateTime.parse(fields[FIRST_DATE_TIME_FIELD_INDEX]));
            case TASK_TYPE_EVENT:
                LocalDateTime startTime = LocalDateTime.parse(fields[FIRST_DATE_TIME_FIELD_INDEX]);
                LocalDateTime endTime = LocalDateTime.parse(fields[SECOND_DATE_TIME_FIELD_INDEX]);
                yield new Event(startTime, endTime, fields[DESCRIPTION_FIELD_INDEX]);
            default:
                throw new IllegalArgumentException("Unsupported task type in data file: " + taskLine);
        };
    }

    /**
     * Returns the field count used by a task type before completion dates were added.
     *
     * @param taskType The task-type field read from the line.
     * @param taskLine The original line, included in an invalid-data error message.
     * @return The number of fields used by the older version of this task type.
     * @throws IllegalArgumentException If the line holds an unsupported task type.
     */
    private int getLegacyFieldCount(String taskType, String taskLine) {
        return switch (taskType) {
            case TASK_TYPE_TODO -> LEGACY_FIELD_COUNT_TODO;
            case TASK_TYPE_DEADLINE -> LEGACY_FIELD_COUNT_DEADLINE;
            case TASK_TYPE_EVENT -> LEGACY_FIELD_COUNT_EVENT;
            default -> throw new IllegalArgumentException("Unsupported task type in data file: " + taskLine);
        };
    }

    /**
     * Verifies that a saved line uses the legacy field count or that count plus a completion date.
     *
     * @param fields The fields parsed from the line.
     * @param legacyFieldCount The number of fields the older format uses for this task type.
     * @param taskLine The original line, included in the error message.
     * @throws IllegalArgumentException If the line holds the wrong number of fields.
     */
    private void checkFieldCount(String[] fields, int legacyFieldCount, String taskLine) {
        if (fields.length != legacyFieldCount && fields.length != legacyFieldCount + 1) {
            throw new IllegalArgumentException("Wrong number of fields in data file: " + taskLine);
        }
    }

    /**
     * Verifies that a saved line has at least a type and a completion status, and no blank field.
     *
     * @param fields The fields parsed from the line.
     * @param taskLine The original line, included in the error message.
     * @throws IllegalArgumentException If a field is missing or blank.
     */
    private void checkFieldsAreFilled(String[] fields, String taskLine) {
        if (fields.length < LEGACY_FIELD_COUNT_TODO) {
            throw new IllegalArgumentException("Incomplete task in data file: " + taskLine);
        }

        for (String field : fields) {
            if (field.isBlank()) {
                throw new IllegalArgumentException("Blank field in data file: " + taskLine);
            }
        }
    }

    /**
     * Returns the completion date in an extended storage line, if one was recorded.
     *
     * @param fields The fields parsed from the saved line.
     * @param legacyFieldCount The field count used before completion dates were added.
     * @param isDone Whether the task is marked as completed.
     * @param taskLine The original line, included in an invalid-data error message.
     * @return The recorded completion date, or null for legacy or unknown dates.
     * @throws IllegalArgumentException If an incomplete task has a completion date.
     * @throws DateTimeParseException If a non-placeholder completion date is unreadable.
     */
    private LocalDate parseCompletionDate(String[] fields, int legacyFieldCount, boolean isDone,
                                          String taskLine) {
        if (fields.length == legacyFieldCount) {
            return null;
        }

        String completionDateText = fields[legacyFieldCount];
        if (completionDateText.equals(UNKNOWN_COMPLETION_DATE)) {
            return null;
        }

        if (!isDone) {
            throw new IllegalArgumentException("Incomplete task has a completion date: " + taskLine);
        }

        return LocalDate.parse(completionDateText);
    }

    /**
     * Converts the completion field of a saved line into a completion state.
     *
     * @param completionStatus The completion field read from the line.
     * @param taskLine The original line, included in the error message.
     * @return True if the field marks the task as done.
     * @throws IllegalArgumentException If the field is neither the done nor the not-done value.
     */
    private boolean parseCompletionStatus(String completionStatus, String taskLine) {
        if (completionStatus.equals(COMPLETION_STATUS_DONE)) {
            return true;
        }

        if (completionStatus.equals(COMPLETION_STATUS_NOT_DONE)) {
            return false;
        }

        throw new IllegalArgumentException("Unknown completion status in data file: " + taskLine);
    }

    /**
     * Returns whether the supplied tasks already contain one with the same user-facing details.
     */
    private static boolean containsTaskWithSameDetails(List<Task> tasks, Task taskToCheck) {
        for (Task task : tasks) {
            if (task.hasSameDetails(taskToCheck)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Closes a failed lock-acquisition attempt without hiding the original failure.
     */
    private void closeLockChannelQuietly() {
        if (lockChannel == null) {
            return;
        }

        try {
            lockChannel.close();
        } catch (IOException e) {
            // A failed lock attempt has no usable channel to preserve.
        } finally {
            lockChannel = null;
        }
    }
}
