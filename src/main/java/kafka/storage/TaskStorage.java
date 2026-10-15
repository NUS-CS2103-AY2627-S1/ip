package kafka.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

import kafka.exception.CorruptedTaskDataException;
import kafka.exception.KafkaException;
import kafka.task.Deadline;
import kafka.task.Event;
import kafka.task.Task;
import kafka.task.TaskList;
import kafka.task.Todo;

/**
 * Saves and loads tasks using a line-based text file.
 * Each line uses the format {@code type | status | task details}.
 */
public class TaskStorage {
    /** Default save-file location relative to the project directory. */
    private static final Path DEFAULT_PATH = Path.of("data", "kafka.txt");
    private static final String TYPE_TODO = "T";
    private static final String TYPE_DEADLINE = "D";
    private static final String TYPE_EVENT = "E";
    private static final String STATUS_DONE = "1";
    private static final String STATUS_NOT_DONE = "0";
    private static final int FIELD_TYPE = 0;
    private static final int FIELD_STATUS = 1;
    private static final int FIELD_DESCRIPTION = 2;
    private static final int FIELD_DEADLINE = 3;
    private static final int FIELD_EVENT_START = 3;
    private static final int FIELD_EVENT_END = 4;
    private static final int FIELD_COUNT_TODO = 3;
    private static final int FIELD_COUNT_DEADLINE = 4;
    private static final int FIELD_COUNT_EVENT = 5;
    private static final String READ_ERROR_PREFIX = "Could not read tasks from ";
    private static final String SAVE_ERROR_PREFIX = "Could not save tasks to ";
    private static final String MALFORMED_DATA_ERROR_PREFIX =
            "Malformed task data on line ";

    private final Path filePath;

    /**
     * Creates storage backed by {@code data/kafka.txt}.
     */
    public TaskStorage() {
        this(DEFAULT_PATH);
    }

    /**
     * Creates storage backed by a specified file.
     *
     * @param filePath Task data file to read and write.
     */
    public TaskStorage(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Returns the absolute location of the task data file.
     *
     * @return Normalized absolute path to the task data file.
     */
    public Path getFilePath() {
        return filePath.toAbsolutePath().normalize();
    }

    /**
     * Loads all valid task records in their saved order.
     * A missing file represents a user who has not saved any tasks yet.
     *
     * @return The tasks stored in the data file, or an empty list if it is absent.
     * @throws CorruptedTaskDataException If a saved line is malformed.
     * @throws KafkaException If the file cannot be read.
     */
    public TaskList load() throws KafkaException {
        TaskList tasks = new TaskList();
        if (Files.notExists(filePath)) {
            return tasks;
        }

        try {
            List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                if (!line.isBlank()) {
                    tasks.addTask(parseTask(line, i + 1));
                }
            }
            return tasks;
        } catch (IOException exception) {
            throw new KafkaException(READ_ERROR_PREFIX + filePath, exception);
        }
    }

    /**
     * Writes the current tasks to the data file in their list order.
     * Writes a temporary file first, then atomically replaces the previous file so
     * a failed write cannot leave partially written task data behind.
     *
     * @param tasks Tasks to persist.
     * @throws KafkaException If the directory or data file cannot be written.
     */
    public void save(TaskList tasks) throws KafkaException {
        Path temporaryFile = null;
        try {
            Path destination = getFilePath();
            Path parentDirectory = destination.getParent();
            Files.createDirectories(parentDirectory);

            List<String> lines = tasks.getTasks().stream()
                    .map(Task::toDataString)
                    .toList();
            temporaryFile = Files.createTempFile(parentDirectory, "kafka-", ".tmp");
            Files.write(temporaryFile, lines, StandardCharsets.UTF_8);
            Files.move(temporaryFile, destination,
                    StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException exception) {
            deleteTemporaryFile(temporaryFile, exception);
            throw new KafkaException(SAVE_ERROR_PREFIX + filePath, exception);
        }
    }

    /**
     * Removes an incomplete save while keeping the original failure as the primary cause.
     */
    private void deleteTemporaryFile(Path temporaryFile, IOException saveException) {
        if (temporaryFile == null) {
            return;
        }
        try {
            Files.deleteIfExists(temporaryFile);
        } catch (IOException cleanupException) {
            saveException.addSuppressed(cleanupException);
        }
    }

    /**
     * Converts one saved record into its concrete task subtype.
     *
     * @param line One complete record from the task data file.
     * @param lineNumber One-based source line used in corruption messages.
     * @return Reconstructed todo, deadline, or event.
     * @throws CorruptedTaskDataException If the record cannot be trusted.
     */
    private Task parseTask(String line, int lineNumber) throws CorruptedTaskDataException {
        assert lineNumber >= 1 : "Storage line numbers must be one-based";
        assert !line.isBlank() : "Blank storage lines must be skipped before parsing";
        String[] fields = parseFields(line, lineNumber);
        Task task = createTask(fields, lineNumber);
        restoreStatus(task, fields[FIELD_STATUS], lineNumber);
        return task;
    }

    /**
     * Splits a record and checks fields shared by every task type.
     *
     * @param line One complete record from the task data file.
     * @param lineNumber One-based source line used in corruption messages.
     * @return Validated fields from the record.
     * @throws CorruptedTaskDataException If required common fields are missing.
     */
    private String[] parseFields(String line, int lineNumber)
            throws CorruptedTaskDataException {
        String[] fields = line.split("\\|", -1);
        if (fields.length < FIELD_COUNT_TODO) {
            throw createMalformedLineException(lineNumber);
        }
        for (int i = 0; i < fields.length; i++) {
            if (i == FIELD_DESCRIPTION) {
                boolean hasFollowingField = i < fields.length - 1;
                fields[i] = removeDescriptionPadding(fields[i], hasFollowingField);
            } else {
                fields[i] = fields[i].strip();
            }
        }

        requireNonBlank(fields[FIELD_DESCRIPTION], lineNumber);
        return fields;
    }

    /**
     * Removes separator padding while preserving whitespace that belongs to the description.
     * The final field has no trailing separator padding.
     */
    private String removeDescriptionPadding(String description, boolean hasFollowingField) {
        int start = description.startsWith(" ") ? 1 : 0;
        int end = description.length();
        if (hasFollowingField && end > start && description.endsWith(" ")) {
            end--;
        }
        return description.substring(start, end);
    }

    /**
     * Creates the concrete task represented by validated storage fields.
     *
     * @param fields Fields parsed from one storage record.
     * @param lineNumber One-based source line used in corruption messages.
     * @return Reconstructed todo, deadline, or event.
     * @throws CorruptedTaskDataException If the type-specific fields are invalid.
     */
    private Task createTask(String[] fields, int lineNumber)
            throws CorruptedTaskDataException {
        assert fields.length >= FIELD_COUNT_TODO
                : "Storage fields must pass common-field validation before task creation";
        return switch (fields[FIELD_TYPE]) {
            case TYPE_TODO -> {
                requireFieldCount(fields, FIELD_COUNT_TODO, lineNumber);
                yield new Todo(fields[FIELD_DESCRIPTION]);
            }
            case TYPE_DEADLINE -> {
                requireFieldCount(fields, FIELD_COUNT_DEADLINE, lineNumber);
                requireNonBlank(fields[FIELD_DEADLINE], lineNumber);
                yield new Deadline(fields[FIELD_DESCRIPTION], fields[FIELD_DEADLINE]);
            }
            case TYPE_EVENT -> {
                requireFieldCount(fields, FIELD_COUNT_EVENT, lineNumber);
                requireNonBlank(fields[FIELD_EVENT_START], lineNumber);
                requireNonBlank(fields[FIELD_EVENT_END], lineNumber);
                yield new Event(fields[FIELD_DESCRIPTION], fields[FIELD_EVENT_START], fields[FIELD_EVENT_END]);
            }
            default -> throw createMalformedLineException(lineNumber);
        };
    }

    /**
     * Restores a task's saved completion state.
     *
     * @param task Task whose state should be restored.
     * @param status Saved completion marker.
     * @param lineNumber One-based source line used in corruption messages.
     * @throws CorruptedTaskDataException If the completion marker is invalid.
     */
    private void restoreStatus(Task task, String status, int lineNumber)
            throws CorruptedTaskDataException {
        if (STATUS_NOT_DONE.equals(status)) {
            return;
        }

        if (STATUS_DONE.equals(status)) {
            task.mark();
            return;
        }

        throw createMalformedLineException(lineNumber);
    }

    /**
     * Checks that a record has exactly the fields required by its task type.
     *
     * @param fields Fields parsed from the record.
     * @param expectedCount Required field count.
     * @param lineNumber One-based source line used in corruption messages.
     * @throws CorruptedTaskDataException If the record has an unexpected number of fields.
     */
    private void requireFieldCount(String[] fields, int expectedCount, int lineNumber)
            throws CorruptedTaskDataException {
        if (fields.length != expectedCount) {
            throw createMalformedLineException(lineNumber);
        }
    }

    /**
     * Checks that a required saved field contains actual text.
     *
     * @param field Saved value to inspect.
     * @param lineNumber One-based source line used in corruption messages.
     * @throws CorruptedTaskDataException If the field is blank.
     */
    private void requireNonBlank(String field, int lineNumber)
            throws CorruptedTaskDataException {
        if (field.isBlank()) {
            throw createMalformedLineException(lineNumber);
        }
    }

    /**
     * Builds a consistent exception for one malformed storage line.
     *
     * @param lineNumber One-based line containing corrupted data.
     * @return Exception identifying the malformed line and file.
     */
    private CorruptedTaskDataException createMalformedLineException(int lineNumber) {
        return new CorruptedTaskDataException(
                MALFORMED_DATA_ERROR_PREFIX + lineNumber + " of " + filePath);
    }
}
