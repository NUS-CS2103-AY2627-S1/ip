package soar.storage;

import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import soar.exception.StorageException;
import soar.task.Deadline;
import soar.task.Event;
import soar.task.Task;
import soar.task.ToDo;

/**
 * Loads and saves the task list using a local data file.
 */
public class Storage {
    private static final int TYPE_FIELD_INDEX = 0;
    private static final int STATUS_FIELD_INDEX = 1;
    private static final int DESCRIPTION_FIELD_INDEX = 2;
    private static final int DATE_OR_START_FIELD_INDEX = 3;
    private static final int END_FIELD_INDEX = 4;

    private static final String INCOMPLETE_STATUS = "0";
    private static final String COMPLETE_STATUS = "1";
    private static final String ISO_DATE_TIME_SEPARATOR = "T";

    /** Default data-file location, relative to the project root. */
    private static final Path DEFAULT_DATA_FILE = Path.of("data", "soar.txt");

    /** Data file used by this storage instance. */
    private final Path dataFile;

    /** Supported task records and their required number of fields. */
    private enum StoredTaskType {
        TODO("T", 3),
        DEADLINE("D", 4),
        EVENT("E", 5);

        private final String code;
        private final int fieldCount;

        StoredTaskType(String code, int fieldCount) {
            this.code = code;
            this.fieldCount = fieldCount;
        }

        /** Returns the task type represented by a storage code. */
        private static StoredTaskType fromCode(String code) throws StorageException {
            for (StoredTaskType taskType : values()) {
                if (taskType.code.equals(code)) {
                    return taskType;
                }
            }
            throw new StorageException("unknown task type '" + code + "'");
        }
    }

    /**
     * Creates storage that uses the application's default data file.
     */
    public Storage() {
        this(DEFAULT_DATA_FILE);
    }

    /**
     * Creates storage that uses a specific data file.
     *
     * @param dataFile Relative file from which tasks are loaded and to which they are saved.
     */
    public Storage(Path dataFile) {
        if (dataFile == null || dataFile.getFileName() == null) {
            throw new IllegalArgumentException("The data file path must name a file");
        }
        if (dataFile.isAbsolute()) {
            throw new IllegalArgumentException("The data file path must be relative to the project folder");
        }
        this.dataFile = dataFile.normalize();
    }

    /**
     * Loads tasks from the data file in their saved order.
     *
     * @return tasks stored in the data file, or an empty list if it does not exist
     * @throws IOException if the data file cannot be read
     * @throws StorageException if a record in the data file is invalid
     */
    public List<Task> load() throws IOException, StorageException {
        ArrayList<Task> tasks = new ArrayList<>();
        if (!Files.exists(dataFile)) {
            return tasks;
        }

        List<String> lines = Files.readAllLines(dataFile);
        for (int i = 0; i < lines.size(); i++) {
            try {
                tasks.add(parseTask(lines.get(i)));
            } catch (StorageException e) {
                throw new StorageException("Invalid task data on line " + (i + 1) + ": " + e.getMessage());
            }
        }
        return tasks;
    }

    /**
     * Replaces the data file with a snapshot of the current task list.
     *
     * @param tasks Tasks to save in their current order.
     * @throws IOException if the data directory or file cannot be written
     */
    public void save(List<Task> tasks) throws IOException {
        Path parent = dataFile.getParent();
        Path saveDirectory = parent == null ? Path.of(".") : parent;
        Files.createDirectories(saveDirectory);

        String temporaryPrefix = dataFile.getFileName().toString();
        if (temporaryPrefix.length() < 3) {
            temporaryPrefix = (temporaryPrefix + "___").substring(0, 3);
        }
        Path temporaryFile = Files.createTempFile(saveDirectory, temporaryPrefix, ".tmp");
        try {
            Files.write(temporaryFile, tasks.stream().map(Task::toDataString).toList());
            try {
                Files.move(temporaryFile, dataFile, StandardCopyOption.ATOMIC_MOVE,
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temporaryFile, dataFile, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(temporaryFile);
        }
    }

    /**
     * Escapes characters that have structural meaning in the line-based format.
     *
     * @param value Task text to encode.
     * @return text safe to store as one field on one line
     */
    public static String escapeField(String value) {
        return value.replace("\\", "\\\\")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("|", "\\|");
    }

    /**
     * Converts one valid storage line back into its concrete task type.
     *
     * @param line Serialized task from the data file.
     * @return reconstructed task
     * @throws StorageException if the line has an invalid type, status, or fields
     */
    private Task parseTask(String line) throws StorageException {
        if (line.isBlank()) {
            throw new StorageException("blank records are not allowed");
        }

        List<String> fields = splitFields(line);
        validateCompletionStatus(fields);
        StoredTaskType taskType = StoredTaskType.fromCode(fields.get(TYPE_FIELD_INDEX));
        validateTaskFields(fields, taskType);

        Task task = createTask(fields, taskType);
        if (fields.get(STATUS_FIELD_INDEX).equals(COMPLETE_STATUS)) {
            task.markAsDone();
        }
        return task;
    }

    /** Rejects a missing or unsupported completion-status field. */
    private void validateCompletionStatus(List<String> fields) throws StorageException {
        if (fields.size() <= STATUS_FIELD_INDEX) {
            throw new StorageException("completion status must be 0 or 1");
        }

        String status = fields.get(STATUS_FIELD_INDEX);
        if (!status.equals(INCOMPLETE_STATUS) && !status.equals(COMPLETE_STATUS)) {
            throw new StorageException("completion status must be 0 or 1");
        }
    }

    /** Rejects a record with the wrong field count or blank task text. */
    private void validateTaskFields(List<String> fields, StoredTaskType taskType)
            throws StorageException {
        if (fields.size() != taskType.fieldCount) {
            throw new StorageException("task type " + taskType.code + " requires "
                    + taskType.fieldCount + " fields but found " + fields.size());
        }
        boolean hasBlankTextField = fields.subList(2, fields.size()).stream()
                .anyMatch(String::isBlank);
        if (hasBlankTextField) {
            throw new StorageException("task text fields must not be empty");
        }
    }

    /** Builds a concrete task from validated storage fields. */
    private Task createTask(List<String> fields, StoredTaskType taskType) throws StorageException {
        try {
            return switch (taskType) {
                case TODO -> new ToDo(fields.get(DESCRIPTION_FIELD_INDEX));
                case DEADLINE -> createDeadline(fields);
                case EVENT -> new Event(fields.get(DESCRIPTION_FIELD_INDEX),
                        fields.get(DATE_OR_START_FIELD_INDEX), fields.get(END_FIELD_INDEX));
                default -> throw new IllegalArgumentException("Unsupported task type: " + taskType);
            };
        } catch (DateTimeParseException e) {
            throw new StorageException("deadline date must use yyyy-MM-dd or ISO date-time format");
        }
    }

    /** Builds a deadline while preserving whether its stored value contains a time. */
    private Deadline createDeadline(List<String> fields) {
        String description = fields.get(DESCRIPTION_FIELD_INDEX);
        String storedDeadline = fields.get(DATE_OR_START_FIELD_INDEX);
        if (storedDeadline.contains(ISO_DATE_TIME_SEPARATOR)) {
            return new Deadline(description, LocalDateTime.parse(storedDeadline));
        }
        return new Deadline(description, LocalDate.parse(storedDeadline));
    }

    /**
     * Splits a record at unescaped {@code |} delimiters and restores escaped text.
     *
     * @param line Serialized task record.
     * @return decoded fields
     * @throws StorageException if an escape sequence is incomplete or unsupported
     */
    private List<String> splitFields(String line) throws StorageException {
        ArrayList<String> fields = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        for (int i = 0; i < line.length(); i++) {
            char current = line.charAt(i);
            if (current == '\\') {
                if (i + 1 >= line.length()) {
                    throw new StorageException("incomplete escape sequence");
                }
                char escaped = line.charAt(++i);
                if (escaped == 'n') {
                    field.append('\n');
                } else if (escaped == 'r') {
                    field.append('\r');
                } else if (escaped == '\\' || escaped == '|') {
                    field.append(escaped);
                } else {
                    throw new StorageException("unsupported escape sequence \\" + escaped + "'");
                }
            } else if (current == '|' && i > 0 && i + 1 < line.length()
                    && line.charAt(i - 1) == ' ' && line.charAt(i + 1) == ' ') {
                field.setLength(Math.max(0, field.length() - 1));
                fields.add(field.toString());
                field.setLength(0);
                i++;
            } else {
                field.append(current);
            }
        }
        fields.add(field.toString());
        return fields;
    }
}
