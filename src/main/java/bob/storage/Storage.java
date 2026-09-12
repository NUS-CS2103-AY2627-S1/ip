package bob.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import bob.parser.DateTimeParser;
import bob.task.Deadline;
import bob.task.Event;
import bob.task.Task;
import bob.task.TaskList;
import bob.task.TaskStatus;
import bob.task.TaskType;
import bob.task.ToDo;

/**
 * Loads and saves tasks in Bob's pipe-delimited text format.
 */
public class Storage {
    /** Separator between fields in a stored task record. */
    public static final String FIELD_SEPARATOR = "|";

    private final Path filePath;

    private record TaskRecord(TaskType type, TaskStatus status) {
    }

    /**
     * Creates storage using Bob's default data file.
     */
    public Storage() {
        this(Path.of("data", "bob.txt").toString());
    }

    /**
     * Creates storage using the supplied data file path.
     *
     * @param filePath path of the task data file.
     */
    public Storage(String filePath) {
        this.filePath = Path.of(filePath);
    }

    /**
     * Converts a task into its pipe-delimited storage representation.
     *
     * @param task task to serialize.
     * @return one storage record for the task.
     * @throws IllegalArgumentException if the task has an unsupported type.
     */
    private String serializeTask(Task task) {
        String type = task.getType().getSymbol();
        String status = task.getStatus().getStorageCode();

        switch (task.getType()) {
            case TODO:
                return String.join(FIELD_SEPARATOR, type, status, task.getDescription());
            case DEADLINE:
                assert task instanceof Deadline : "DEADLINE tasks must be Deadline instances";
                Deadline deadline = (Deadline) task;
                return String.join(FIELD_SEPARATOR, type, status, deadline.getDescription(),
                                                DateTimeParser.formatForStorage(deadline.getBy()));
            case EVENT:
                assert task instanceof Event : "EVENT tasks must be Event instances";
                Event event = (Event) task;
                return String.join(FIELD_SEPARATOR, type, status, event.getDescription(),
                                                DateTimeParser.formatForStorage(event.getFrom()),
                                                DateTimeParser.formatForStorage(event.getTo()));
            default:
                throw new IllegalArgumentException("Unsupported task type: " + task.getType());
        }
    }

    /**
     * Saves all tasks, replacing the previous file contents.
     *
     * @param tasks tasks to save.
     * @throws IOException if the data directory or file cannot be written.
     */
    public void save(List<Task> tasks) throws IOException {
        try {
            Path parentDirectory = filePath.getParent();
            if (parentDirectory != null) {
                Files.createDirectories(parentDirectory);
            }
            List<String> lines = tasks.stream().map(this::serializeTask).toList();

            Files.write(filePath, lines, StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new IOException("Could not save tasks to " + filePath + ".", exception);
        }
    }

    /**
     * Converts one stored record into a task.
     *
     * @param parts fields from a pipe-delimited storage record.
     * @param lineNumber zero-based line number used in error messages.
     * @return task represented by the record.
     * @throws IOException if the stored record is malformed.
     */
    public Task deserializeTask(String[] parts, int lineNumber) throws IOException {
        TaskRecord record = validateRecord(parts, lineNumber);
        Task task = createTask(parts, record.type(), lineNumber);

        if (record.status() == TaskStatus.DONE) {
            task.markAsDone();
        }

        return task;
    }

    /**
     * Validates the common fields in a stored task record.
     *
     * @param parts fields from a pipe-delimited storage record.
     * @param lineNumber zero-based line number used in error messages.
     * @return validated task type and status.
     * @throws IOException if the record has an invalid type, status, or field
     *     count.
     */
    private TaskRecord validateRecord(String[] parts, int lineNumber) throws IOException {
        if (parts.length == 0 || parts[0].isBlank()) {
            throw corruptedFile(lineNumber, "missing task type");
        }

        if (parts.length < 2) {
            throw corruptedFile(lineNumber, "missing task status");
        }

        TaskType type;
        try {
            type = TaskType.fromSymbol(parts[0]);
        } catch (IllegalArgumentException exception) {
            throw corruptedFile(lineNumber, "unknown task type '" + parts[0] + "'");
        }

        TaskStatus status;
        try {
            status = TaskStatus.fromStorageCode(parts[1]);
        } catch (IllegalArgumentException exception) {
            throw corruptedFile(lineNumber, "invalid task status");
        }

        if (parts.length != type.getStorageFieldCount()) {
            throw corruptedFile(lineNumber, "expected " + type.getStorageFieldCount() + " fields but found "
                                            + parts.length);
        }

        return new TaskRecord(type, status);
    }

    /**
     * Constructs a task from the type-specific fields in a storage record.
     *
     * @param parts fields from a pipe-delimited storage record.
     * @param type validated task type.
     * @param lineNumber zero-based line number used in error messages.
     * @return task represented by the record.
     * @throws IOException if a date field is malformed.
     */
    private Task createTask(String[] parts, TaskType type, int lineNumber) throws IOException {
        switch (type) {
            case TODO:
                requireStoredDescription(parts[2], lineNumber);
                return new ToDo(parts[2]);
            case DEADLINE:
                try {
                    requireStoredDescription(parts[2], lineNumber);
                    return new Deadline(parts[2], DateTimeParser.parseStorage(parts[3]));
                } catch (IllegalArgumentException exception) {
                    throw corruptedFile(lineNumber, "invalid deadline date");
                }
            case EVENT:
                try {
                    requireStoredDescription(parts[2], lineNumber);
                    return new Event(parts[2], DateTimeParser.parseStorage(parts[3]),
                                                    DateTimeParser.parseStorage(parts[4]));
                } catch (IllegalArgumentException exception) {
                    throw corruptedFile(lineNumber, "invalid event date or range");
                }
            default:
                throw corruptedFile(lineNumber, "unsupported task type '" + type + "'");
        }
    }

    /** Validates the description field read from persistent storage. */
    private void requireStoredDescription(String description, int lineNumber) throws IOException {
        if (description == null || description.isBlank() || description.contains(FIELD_SEPARATOR)) {
            throw corruptedFile(lineNumber, "missing or invalid task description");
        }
    }

    /**
     * Loads tasks from the storage file.
     *
     * @return tasks reconstructed from storage, or an empty list if no file
     *     exists.
     * @throws IOException if the file cannot be read or contains malformed
     *     data.
     */
    public List<Task> load() throws IOException {
        List<Task> tasks = new ArrayList<>();

        // A new installation does not have a data file yet.
        if (Files.notExists(filePath)) {
            return tasks;
        }

        List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);

        for (int lineNumber = 0; lineNumber < lines.size(); lineNumber++) {
            String line = lines.get(lineNumber);
            String[] parts = line.split("\\s*\\|\\s*", -1);

            Task task = deserializeTask(parts, lineNumber);
            if (new TaskList(tasks).containsEquivalentTask(task)) {
                throw corruptedFile(lineNumber, "duplicate task");
            }
            tasks.add(task);
        }

        return tasks;
    }

    /**
     * Creates a consistent error for a malformed storage record.
     *
     * @param lineNumber zero-based line number containing the error.
     * @param reason explanation of the malformed data.
     * @return exception containing the line and reason.
     */
    private IOException corruptedFile(int lineNumber, String reason) {
        return new IOException("Could not load saved tasks: corrupted data on line " + (lineNumber + 1) + " (" + reason
                                        + ").");
    }

}
