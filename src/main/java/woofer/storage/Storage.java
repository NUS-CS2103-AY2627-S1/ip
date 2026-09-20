package woofer.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

import woofer.task.Deadline;
import woofer.task.Event;
import woofer.task.Task;
import woofer.task.TaskList;
import woofer.task.Todo;

/**
 * Stores and loads Woofer tasks from a local data file.
 */
public class Storage {
    private static final Path DEFAULT_FILE_PATH = Path.of("data", "woofer.txt");
    private static final String FIELD_SEPARATOR_REGEX = "\\s*\\|\\s*";
    private static final String FIELD_SEPARATOR = " | ";
    private static final String DONE_STATUS = "1";
    private static final String NOT_DONE_STATUS = "0";

    private final Path filePath;
    /** Prevents a failed load from being followed by an overwrite of the unreadable original. */
    private boolean savingBlocked;

    /**
     * Creates storage using {@code ./data/woofer.txt} as the data file.
     */
    public Storage() {
        this(DEFAULT_FILE_PATH);
    }

    /**
     * Creates storage using the specified data file.
     *
     * @param filePath path of the data file.
     */
    public Storage(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads all records, treating only a missing file as a new task list.
     *
     * <p>If any record is invalid, saving is blocked to protect the original file. Repair the
     * file and restart Woofer to resume saving.</p>
     *
     * @return the loaded tasks.
     * @throws IOException when reading fails, a record is invalid, or the task limit is exceeded.
     */
    public TaskList load() throws IOException {
        savingBlocked = true;
        TaskList taskList = new TaskList();
        List<String> lines;
        try {
            lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
        } catch (NoSuchFileException exception) {
            savingBlocked = false;
            return taskList;
        }
        for (int index = 0; index < lines.size(); index++) {
            String line = lines.get(index);
            if (line.isBlank()) {
                continue;
            }
            try {
                Task task = parseTask(line);
                if (!taskList.addTask(task)) {
                    throw new IllegalArgumentException("Saved task limit exceeded.");
                }
            } catch (IllegalArgumentException exception) {
                throw new IOException("Invalid saved task on line " + (index + 1) + ".", exception);
            }
        }
        savingBlocked = false;
        return taskList;
    }

    /**
     * Writes a complete temporary file before atomically replacing the saved file.
     *
     * <p>If atomic replacement is unsupported or fails, the original is left untouched.</p>
     *
     * @param taskList tasks to save.
     * @throws IOException when saving is blocked or the file cannot be replaced safely.
     */
    public void save(TaskList taskList) throws IOException {
        assert taskList != null : "Task list to save must not be null";
        if (savingBlocked) {
            throw new IOException("Saving is disabled because the original file could not be loaded.");
        }
        Path target = filePath.toAbsolutePath();
        Path parentDirectory = target.getParent();
        Files.createDirectories(parentDirectory);
        List<String> lines = taskList.getTasks().stream()
                .map(this::serializeTask)
                .toList();
        Path temporaryFile = Files.createTempFile(parentDirectory, "woofer-", ".tmp");
        try {
            Files.write(temporaryFile, lines, StandardCharsets.UTF_8);
            Files.move(temporaryFile, target, StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING);
        } finally {
            Files.deleteIfExists(temporaryFile);
        }
    }

    /**
     * Converts one saved record into a task.
     *
     * @param line saved task record.
    * @return the task represented by the record.
     * @throws IllegalArgumentException when the record is malformed.
     */
    private Task parseTask(String line) {
        assert line != null && !line.isBlank() : "Saved task line must not be blank";
        String[] fields = line.split(FIELD_SEPARATOR_REGEX, -1);
        if (fields.length < 2) {
            throw new IllegalArgumentException("A task record has too few fields.");
        }

        boolean isDone = parseStatus(fields[1]);
        Task task = switch (fields[0]) {
            case "T" -> {
                requireFieldCount(fields, 3);
                yield new Todo(requireField(fields, 2));
            }
            case "D" -> {
                requireFieldCount(fields, 4);
                yield new Deadline(requireField(fields, 2), parseDate(requireField(fields, 3)));
            }
            case "E" -> {
                requireFieldCount(fields, 5);
                yield new Event(
                        requireField(fields, 2),
                        parseDate(requireField(fields, 3)),
                        parseDate(requireField(fields, 4)));
            }
            default -> throw new IllegalArgumentException("Unknown task type: " + fields[0]);
        };

        if (isDone) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Parses an ISO local date stored in a task record.
     *
     * @param date saved date.
     * @return the parsed date.
     * @throws IllegalArgumentException when the date is invalid.
     */
    private LocalDate parseDate(String date) {
        try {
            return LocalDate.parse(date);
        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException("Invalid date in task record.", exception);
        }
    }

    /**
     * Returns the completion status represented by a saved status field.
     *
     * @param status saved completion status.
     * @return {@code true} when the task is done.
     * @throws IllegalArgumentException when the status is invalid.
     */
    private boolean parseStatus(String status) {
        if (DONE_STATUS.equals(status)) {
            return true;
        }
        if (NOT_DONE_STATUS.equals(status)) {
            return false;
        }
        throw new IllegalArgumentException("Unknown task completion status: " + status);
    }

    /**
     * Checks that a saved record has the expected number of fields.
     *
     * @param fields fields in the saved record.
     * @param expectedCount expected number of fields.
     * @throws IllegalArgumentException when the field count is incorrect.
     */
    private void requireFieldCount(String[] fields, int expectedCount) {
        if (fields.length != expectedCount) {
            throw new IllegalArgumentException("A task record has an incorrect number of fields.");
        }
    }

    /**
     * Returns a required non-empty field from a saved record.
     *
     * @param fields fields in the saved record.
     * @param fieldIndex index of the required field.
     * @return the required field.
     * @throws IllegalArgumentException when the field is empty.
     */
    private String requireField(String[] fields, int fieldIndex) {
        String field = fields[fieldIndex].trim();
        if (field.isBlank()) {
            throw new IllegalArgumentException("A required task field cannot be empty.");
        }
        return field;
    }

    /**
     * Converts a task into a pipe-delimited saved record.
     *
     * @param task task to serialize.
    * @return the serialized task record.
     */
    private String serializeTask(Task task) {
        assert task != null : "Task to serialize must not be null";
        String status = task.isDone() ? DONE_STATUS : NOT_DONE_STATUS;
        if (task instanceof Deadline deadline) {
            return String.join(
                    FIELD_SEPARATOR,
                    "D",
                    status,
                    task.getDescription(),
                    deadline.getBy().toString());
        }
        if (task instanceof Event event) {
            return String.join(
                    FIELD_SEPARATOR,
                    "E",
                    status,
                    task.getDescription(),
                    event.getFrom().toString(),
                    event.getTo().toString());
        }
        return String.join(FIELD_SEPARATOR, "T", status, task.getDescription());
    }
}
