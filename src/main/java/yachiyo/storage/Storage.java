package yachiyo.storage;

import static yachiyo.exception.ErrorCategory.SYSTEM_ERROR;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import yachiyo.exception.YachiyoException;
import yachiyo.task.Deadline;
import yachiyo.task.Event;
import yachiyo.task.Task;
import yachiyo.task.ToDo;

/**
 * Loads and saves Yachiyo's task data on the hard disk.
 */
public class Storage {
    private static final String TASK_FIELD_DELIMITER_PATTERN = " \\| ";
    private static final String TASK_TYPE_TODO = "TODO";
    private static final String TASK_TYPE_DEADLINE = "DEADLINE";
    private static final String TASK_TYPE_EVENT = "EVENT";
    private static final String COMPLETION_STATUS_INCOMPLETE = "0";
    private static final String COMPLETION_STATUS_COMPLETE = "1";

    private static final int TASK_TYPE_FIELD_INDEX = 0;
    private static final int COMPLETION_STATUS_FIELD_INDEX = 1;
    private static final int DESCRIPTION_FIELD_INDEX = 2;
    private static final int DEADLINE_DATE_TIME_FIELD_INDEX = 3;
    private static final int EVENT_START_DATE_TIME_FIELD_INDEX = 3;
    private static final int EVENT_END_DATE_TIME_FIELD_INDEX = 4;
    private static final int TODO_FIELD_COUNT = 3;
    private static final int DEADLINE_FIELD_COUNT = 4;
    private static final int EVENT_FIELD_COUNT = 5;

    private final Path filePath;

    /**
     * Creates a storage manager that writes to the specified file path.
     *
     * @param filePath path of the file used to store tasks.
     */
    public Storage(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads all tasks from the data file. A missing file represents an empty task list.
     *
     * @return tasks reconstructed from the data file.
     * @throws YachiyoException if the data file cannot be read or contains invalid task data.
     */
    public List<Task> loadTasks() throws YachiyoException {
        if (Files.notExists(filePath)) {
            return new ArrayList<>();
        }

        try {
            List<Task> tasks = new ArrayList<>();
            for (String line : Files.readAllLines(filePath, StandardCharsets.UTF_8)) {
                if (!line.isBlank()) {
                    tasks.add(parseTask(line));
                }
            }
            return tasks;
        } catch (IOException e) {
            throw new YachiyoException(SYSTEM_ERROR,
                    "Oh no! I can't seem to load your tasks from the data file.");
        }
    }

    /**
     * Reconstructs one task from its saved file representation.
     *
     * @param line saved task data.
     * @return reconstructed task.
     * @throws YachiyoException if the saved task data is malformed.
     */
    private Task parseTask(String line) throws YachiyoException {
        String[] taskParts = line.split(TASK_FIELD_DELIMITER_PATTERN, -1);
        if (taskParts.length < TODO_FIELD_COUNT) {
            throw invalidDataException();
        }

        Task task = createTaskFromParts(taskParts);
        restoreCompletionStatus(task, taskParts[COMPLETION_STATUS_FIELD_INDEX]);
        return task;
    }

    /**
     * Creates a task from the type-specific fields in a saved record.
     *
     * @param taskParts saved task fields.
     * @return reconstructed task with its type-specific fields populated.
     * @throws YachiyoException if the task type or its fields are invalid.
     */
    private Task createTaskFromParts(String[] taskParts) throws YachiyoException {
        return switch (taskParts[TASK_TYPE_FIELD_INDEX]) {
            case TASK_TYPE_TODO -> {
                validateTaskParts(taskParts, TODO_FIELD_COUNT);
                yield new ToDo(taskParts[DESCRIPTION_FIELD_INDEX]);
            }
            case TASK_TYPE_DEADLINE -> {
                validateTaskParts(taskParts, DEADLINE_FIELD_COUNT);
                yield new Deadline(taskParts[DESCRIPTION_FIELD_INDEX],
                        parseDateTime(taskParts[DEADLINE_DATE_TIME_FIELD_INDEX]));
            }
            case TASK_TYPE_EVENT -> {
                validateTaskParts(taskParts, EVENT_FIELD_COUNT);
                LocalDateTime from = parseDateTime(taskParts[EVENT_START_DATE_TIME_FIELD_INDEX]);
                LocalDateTime to = parseDateTime(taskParts[EVENT_END_DATE_TIME_FIELD_INDEX]);
                if (!to.isAfter(from)) {
                    throw invalidDataException();
                }
                yield new Event(taskParts[DESCRIPTION_FIELD_INDEX], from, to);
            }
            default -> throw invalidDataException();
        };
    }

    /**
     * Restores a task's completion state from its saved status field.
     *
     * @param task task whose completion state should be restored.
     * @param completionStatus saved completion status.
     * @throws YachiyoException if the completion status is invalid.
     */
    private void restoreCompletionStatus(Task task, String completionStatus) throws YachiyoException {
        if (completionStatus.equals(COMPLETION_STATUS_COMPLETE)) {
            task.markAsDone();
        } else if (!completionStatus.equals(COMPLETION_STATUS_INCOMPLETE)) {
            throw invalidDataException();
        }
    }

    /**
     * Parses a date-time stored in the ISO yyyy-MM-ddTHH:mm format.
     *
     * @param dateTimeText stored date-time text.
     * @return parsed date-time.
     * @throws YachiyoException if the stored date-time is invalid.
     */
    private LocalDateTime parseDateTime(String dateTimeText) throws YachiyoException {
        try {
            return LocalDateTime.parse(dateTimeText);
        } catch (DateTimeParseException e) {
            throw invalidDataException();
        }
    }

    /**
     * Checks that a saved task has the expected number of non-empty fields.
     *
     * @param taskParts saved task fields.
     * @param expectedPartCount number of fields required for the task type.
     * @throws YachiyoException if a field is missing or empty.
     */
    private void validateTaskParts(String[] taskParts, int expectedPartCount) throws YachiyoException {
        if (taskParts.length != expectedPartCount) {
            throw invalidDataException();
        }
        for (String taskPart : taskParts) {
            if (taskPart.isBlank()) {
                throw invalidDataException();
            }
        }
        if (!Task.isValidDescription(taskParts[DESCRIPTION_FIELD_INDEX])) {
            throw invalidDataException();
        }
    }

    private YachiyoException invalidDataException() {
        return new YachiyoException(SYSTEM_ERROR,
                "Oh no! Some task data in the file isn't in the expected format.");
    }

    /**
     * Overwrites the data file with the current task list, creating its parent directory if needed.
     *
     * @param tasks current tasks to save.
     * @throws YachiyoException if the tasks cannot be written to the file.
     */
    public void saveTasks(List<Task> tasks) throws YachiyoException {
        validateTasksForSaving(tasks);

        Path temporaryFile = null;
        try {
            Path absoluteFilePath = filePath.toAbsolutePath();
            Path parentDirectory = absoluteFilePath.getParent();
            if (parentDirectory == null) {
                throw new IOException("Data file must have a parent directory");
            }
            Files.createDirectories(parentDirectory);

            List<String> taskLines = tasks.stream()
                    .map(Task::toFileFormat)
                    .toList();
            temporaryFile = Files.createTempFile(parentDirectory, ".yachiyo-", ".tmp");
            Files.write(temporaryFile, taskLines, StandardCharsets.UTF_8);
            Files.move(temporaryFile, absoluteFilePath,
                    StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException | SecurityException e) {
            throw new YachiyoException(SYSTEM_ERROR,
                    "Oh no! I can't seem to save your tasks to the data file.");
        } finally {
            deleteTemporaryFile(temporaryFile);
        }
    }

    /**
     * Checks that every task can be represented safely in the storage format.
     *
     * @param tasks tasks that are about to be saved.
     * @throws YachiyoException if a task contains unsupported data.
     */
    private void validateTasksForSaving(List<Task> tasks) throws YachiyoException {
        if (tasks.stream().anyMatch(task -> !Task.isValidDescription(task.getDescription()))) {
            throw new YachiyoException(SYSTEM_ERROR,
                    "Oh no! A task contains characters that can't be stored safely."
            );
        }
    }

    /**
     * Removes an unfinished temporary save without masking the original save result.
     *
     * @param temporaryFile temporary file to remove, or {@code null} if none was created.
     */
    private void deleteTemporaryFile(Path temporaryFile) {
        if (temporaryFile == null) {
            return;
        }

        try {
            Files.deleteIfExists(temporaryFile);
        } catch (IOException | SecurityException ignored) {
            // Preserve the original save result if cleanup is not permitted.
        }
    }
}
