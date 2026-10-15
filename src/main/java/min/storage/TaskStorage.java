package min.storage;

import java.nio.file.Path;
import java.time.LocalDate;

import min.task.Deadline;
import min.task.Event;
import min.task.Task;
import min.task.Todo;

/**
 * Saves and loads Min tasks on the hard disk.
 */
public class TaskStorage extends Storage<Task> {
    private static final Path DEFAULT_FILE_PATH = Path.of("data", "min.txt");
    private static final String FIELD_SEPARATOR_REGEX = " \\| ";
    private static final String TYPE_TODO = "T";
    private static final String TYPE_DEADLINE = "D";
    private static final String TYPE_EVENT = "E";
    private static final String STATUS_INCOMPLETE = "0";
    private static final String STATUS_COMPLETE = "1";
    private static final String MESSAGE_DAMAGED_RECORD = "Damaged task record.";
    private static final String MESSAGE_INVALID_STATUS = "Invalid task status.";
    private static final String MESSAGE_BLANK_DESCRIPTION =
            "Task description cannot be blank.";
    private static final String MESSAGE_BLANK_EVENT_TIME = "Event times cannot be blank.";

    private static final int FIELD_TYPE_INDEX = 0;
    private static final int FIELD_STATUS_INDEX = 1;
    private static final int FIELD_DESCRIPTION_INDEX = 2;
    private static final int FIELD_DEADLINE_DATE_INDEX = 3;
    private static final int FIELD_EVENT_START_INDEX = 3;
    private static final int FIELD_EVENT_END_INDEX = 4;
    private static final int FIELD_COUNT_TODO = 3;
    private static final int FIELD_COUNT_DEADLINE = 4;
    private static final int FIELD_COUNT_EVENT = 5;

    /** Creates storage that uses Min's default task data file. */
    public TaskStorage() {
        this(DEFAULT_FILE_PATH);
    }

    TaskStorage(Path filePath) {
        super(filePath);
    }

    @Override
    protected String encode(Task task) {
        return task.toFileString();
    }

    /**
     * {@inheritDoc}
     *
     * @throws IllegalArgumentException If the line is not a valid saved task record.
     * @throws java.time.format.DateTimeParseException If a saved deadline date is invalid.
     */
    @Override
    protected Task decode(String line) {
        String[] fields = line.split(FIELD_SEPARATOR_REGEX, -1);
        Task task = switch (fields[FIELD_TYPE_INDEX]) {
            case TYPE_TODO -> {
                validateTaskFields(fields, FIELD_COUNT_TODO);
                yield new Todo(fields[FIELD_DESCRIPTION_INDEX]);
            }
            case TYPE_DEADLINE -> {
                validateTaskFields(fields, FIELD_COUNT_DEADLINE);
                yield new Deadline(fields[FIELD_DESCRIPTION_INDEX],
                        LocalDate.parse(fields[FIELD_DEADLINE_DATE_INDEX]));
            }
            case TYPE_EVENT -> {
                validateTaskFields(fields, FIELD_COUNT_EVENT);
                validateNonBlank(fields[FIELD_EVENT_START_INDEX], MESSAGE_BLANK_EVENT_TIME);
                validateNonBlank(fields[FIELD_EVENT_END_INDEX], MESSAGE_BLANK_EVENT_TIME);
                yield new Event(fields[FIELD_DESCRIPTION_INDEX],
                        fields[FIELD_EVENT_START_INDEX], fields[FIELD_EVENT_END_INDEX]);
            }
            default -> throw new IllegalArgumentException("Unknown task type.");
        };

        if (fields[FIELD_STATUS_INDEX].equals(STATUS_COMPLETE)) {
            task.markAsDone();
        }
        return task;
    }

    /** Validates the fields shared by every saved task type. */
    private static void validateTaskFields(String[] fields, int expectedFieldCount) {
        validateFieldCount(fields, expectedFieldCount);
        validateStatus(fields[FIELD_STATUS_INDEX]);
        validateNonBlank(fields[FIELD_DESCRIPTION_INDEX], MESSAGE_BLANK_DESCRIPTION);
    }

    /** Validates that a saved task has exactly the expected number of fields. */
    private static void validateFieldCount(String[] fields, int expectedFieldCount) {
        if (fields.length != expectedFieldCount) {
            throw new IllegalArgumentException(MESSAGE_DAMAGED_RECORD);
        }
    }

    /** Validates that a saved completion status is either incomplete or complete. */
    private static void validateStatus(String status) {
        if (!status.equals(STATUS_INCOMPLETE) && !status.equals(STATUS_COMPLETE)) {
            throw new IllegalArgumentException(MESSAGE_INVALID_STATUS);
        }
    }

    /** Validates that a required saved text field contains non-whitespace text. */
    private static void validateNonBlank(String text, String errorMessage) {
        if (text.isBlank()) {
            throw new IllegalArgumentException(errorMessage);
        }
    }
}
