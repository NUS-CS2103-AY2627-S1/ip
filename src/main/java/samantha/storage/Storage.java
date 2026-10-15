package samantha.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import samantha.exception.CorruptedTaskFileException;
import samantha.exception.InputException;
import samantha.exception.SamanthaException;
import samantha.exception.TaskFileReadException;
import samantha.exception.TaskFileWriteException;
import samantha.exception.TaskValidationException;
import samantha.model.Deadline;
import samantha.model.Event;
import samantha.model.Task;
import samantha.model.Todo;

/**
 * Handles persistence of Samantha's task list.
 */
public class Storage {
    private static final Path DEFAULT_FILE = Path.of("data", "samantha.txt");
    private static final String TODO_TYPE = "T";
    private static final String DEADLINE_TYPE = "D";
    private static final String EVENT_TYPE = "E";
    private static final String EVENT_TIME_SEPARATOR = "to";
    private static final int INCOMPLETE_STATUS = 0;
    private static final int COMPLETE_STATUS = 1;
    private static final int TASK_TYPE_INDEX = 0;
    private static final int STATUS_INDEX = 1;
    private static final int TASK_NAME_INDEX = 2;
    private static final int FIRST_DETAIL_INDEX = 3;
    private static final int SECOND_DETAIL_INDEX = 4;
    private static final int MINIMUM_TASK_FIELDS = 3;
    private static final int TODO_FIELD_COUNT = 3;
    private static final int DEADLINE_FIELD_COUNT = 4;
    private static final int COMBINED_EVENT_FIELD_COUNT = 4;
    private static final int SPLIT_EVENT_FIELD_COUNT = 5;
    private final Path file;

    /**
     * Creates storage using Samantha's default data-file location.
     */
    public Storage() {
        this(DEFAULT_FILE);
    }

    /**
     * Creates storage backed by the supplied file.
     *
     * <p>This constructor is useful when a caller needs an isolated storage
     * location, such as for automated tests.</p>
     *
     * @param file location of the task data file
     */
    public Storage(Path file) {
        assert file != null : "Storage must have a data-file path";
        this.file = file;
    }

    /**
     * Loads all tasks from the data file.
     *
     * <p>A missing file represents a first run and therefore produces an empty
     * task list. Any existing malformed record is reported as a corrupted file.
     *
     * @return the tasks read from disk
     * @throws TaskFileReadException if the file cannot be read
     * @throws CorruptedTaskFileException if an existing record is malformed
     */
    public ArrayList<Task> load() throws TaskFileReadException, CorruptedTaskFileException {
        ArrayList<Task> tasks = new ArrayList<>();
        if (!Files.exists(file)) {
            return tasks;
        }

        List<String> lines;
        try {
            lines = Files.readAllLines(file);
        } catch (IOException e) {
            throw new TaskFileReadException(e);
        }
        for (int lineNumber = 0; lineNumber < lines.size(); lineNumber++) {
            String line = lines.get(lineNumber);
            String[] parts = line.split("\\s*\\|\\s*", -1);
            try {
                tasks.add(parseTask(parts));
            } catch (SamanthaException | IllegalArgumentException e) {
                throw new CorruptedTaskFileException(lineNumber + 1, e);
            }
        }
        return tasks;
    }

    /**
     * Reconstructs one task from the fields in a saved data-file record.
     *
     * @param parts pipe-delimited record fields
     * @return the reconstructed task, with its saved completion state
     * @throws TaskValidationException if a task field violates the task model's rules
     * @throws InputException if a persisted date or time value is invalid
     * @throws IllegalArgumentException if the record structure is malformed
     */
    private Task parseTask(String[] parts) throws TaskValidationException, InputException {
        if (parts.length < MINIMUM_TASK_FIELDS
                || parts[TASK_TYPE_INDEX].isBlank()
                || parts[STATUS_INDEX].isBlank()) {
            throw new IllegalArgumentException("Malformed task record");
        }

        boolean isDone = parseCompletionStatus(parts[STATUS_INDEX]);
        Task task = createTask(parts);
        if (isDone) {
            task.markDone();
        }
        assert task != null : "A valid task record must reconstruct a task";
        return task;
    }

    /**
     * Parses the completion status from a saved task record.
     *
     * @param statusText raw completion status
     * @return {@code true} when the saved task is complete
     * @throws IllegalArgumentException if the status is not supported
     */
    private boolean parseCompletionStatus(String statusText) {
        int status;
        try {
            status = Integer.parseInt(statusText.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Malformed task status", e);
        }

        if (status == INCOMPLETE_STATUS) {
            return false;
        }
        if (status == COMPLETE_STATUS) {
            return true;
        }
        throw new IllegalArgumentException("Malformed task status");
    }

    /**
     * Creates a task from a validated saved record.
     *
     * @param parts pipe-delimited record fields
     * @return the reconstructed task
     * @throws TaskValidationException if a task field violates the task model's rules
     * @throws InputException if a persisted date or time value is invalid
     * @throws IllegalArgumentException if the task type or record structure is malformed
     */
    private Task createTask(String[] parts) throws TaskValidationException, InputException {
        return switch (parts[TASK_TYPE_INDEX].trim()) {
            case TODO_TYPE -> parseTodo(parts);
            case DEADLINE_TYPE -> parseDeadline(parts);
            case EVENT_TYPE -> parseEvent(parts);
            default -> throw new IllegalArgumentException("Unknown task type");
        };
    }

    /**
     * Reconstructs a todo task from a saved record.
     *
     * @param parts pipe-delimited record fields
     * @return the reconstructed todo
     * @throws TaskValidationException if the description is invalid
     * @throws IllegalArgumentException if the record has the wrong number of fields
     */
    private Task parseTodo(String[] parts) throws TaskValidationException {
        requirePartCount(parts, TODO_FIELD_COUNT);
        return new Todo(requireText(parts[TASK_NAME_INDEX]));
    }

    /**
     * Reconstructs a deadline task from a saved record.
     *
     * @param parts pipe-delimited record fields
     * @return the reconstructed deadline
     * @throws TaskValidationException if the description is invalid
     * @throws InputException if the deadline is invalid
     * @throws IllegalArgumentException if the record has the wrong number of fields
     */
    private Task parseDeadline(String[] parts) throws TaskValidationException, InputException {
        requirePartCount(parts, DEADLINE_FIELD_COUNT);
        return new Deadline(requireText(parts[TASK_NAME_INDEX]),
                requireText(parts[FIRST_DETAIL_INDEX]));
    }

    /**
     * Reconstructs an event task from either supported saved-record layout.
     *
     * @param parts pipe-delimited record fields
     * @return the reconstructed event
     * @throws TaskValidationException if the description or schedule is invalid
     * @throws InputException if an event date or time is invalid
     * @throws IllegalArgumentException if the record structure is malformed
     */
    private Task parseEvent(String[] parts) throws TaskValidationException, InputException {
        if (parts.length == COMBINED_EVENT_FIELD_COUNT) {
            return parseCombinedEvent(parts);
        }
        if (parts.length == SPLIT_EVENT_FIELD_COUNT) {
            return new Event(requireText(parts[TASK_NAME_INDEX]),
                    requireText(parts[FIRST_DETAIL_INDEX]), requireText(parts[SECOND_DETAIL_INDEX]));
        }
        throw new IllegalArgumentException("Wrong number of fields");
    }

    /**
     * Reconstructs an event from the current combined-schedule record layout.
     *
     * @param parts pipe-delimited record fields
     * @return the reconstructed event
     * @throws TaskValidationException if the description or schedule is invalid
     * @throws InputException if an event date or time is invalid
     * @throws IllegalArgumentException if the schedule is malformed
     */
    private Task parseCombinedEvent(String[] parts)
            throws TaskValidationException, InputException {
        String schedule = requireText(parts[FIRST_DETAIL_INDEX]);
        String[] times = schedule.split("\\s+" + EVENT_TIME_SEPARATOR + "\\s+", 2);
        if (times.length != 2) {
            throw new IllegalArgumentException("Malformed event schedule");
        }
        return new Event(requireText(parts[TASK_NAME_INDEX]), requireText(times[0]), requireText(times[1]));
    }

    /**
     * Checks that a saved record has the required number of fields.
     *
     * @param parts record fields to validate
     * @param expected required field count
     * @throws IllegalArgumentException if the field count differs from {@code expected}
     */
    private void requirePartCount(String[] parts, int expected) {
        if (parts.length != expected) {
            throw new IllegalArgumentException("Wrong number of fields");
        }
    }

    /**
     * Returns trimmed record text after ensuring that it is not blank.
     *
     * @param value raw record field
     * @return non-blank trimmed text
     * @throws IllegalArgumentException if the field is blank
     */
    private String requireText(String value) {
        String text = value.trim();
        if (text.isEmpty()) {
            throw new IllegalArgumentException("Task fields cannot be empty");
        }
        return text;
    }

    /**
     * Saves the current task list, creating the relative data directory when
     * necessary.
     *
     * @param tasks tasks to serialize
     * @throws TaskFileWriteException if the file cannot be created or written
     */
    public void save(List<Task> tasks) throws TaskFileWriteException {
        assert tasks != null : "Tasks to save must not be null";
        assert tasks.stream().noneMatch(task -> task == null)
                : "Tasks to save must not contain null tasks";
        try {
            Path parent = file.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }

            Files.write(file, tasks.stream()
                    .map(Task::toFileString)
                    .toList());
        } catch (IOException e) {
            throw new TaskFileWriteException(e);
        }
    }
}
