package martin.task;

import java.time.LocalDateTime;

import martin.exception.IllegalCommandException;
import martin.util.DateTimeUtil;
import martin.util.StringParserUtil;

/**
 * Represents a task that must be completed by a specified date or time.
 */
public class Deadline extends Task {
    /**
     * The delimiter used to separate the description from the deadline in the
     * command input.
     */
    public static final String BY_DELIMITER = "/by";
    protected final LocalDateTime by;

    /**
     * Constructs a {@code Deadline} task with a description and a deadline
     * date/time.
     * Priority defaults to {@code LOW}.
     *
     * @param description The description of the task.
     * @param deadline    The date and time by which the task should be completed.
     */
    public Deadline(String description, LocalDateTime deadline) {
        this(description, deadline, Priority.LOW);
    }

    /**
     * Constructs a {@code Deadline} task with a description, deadline, and
     * priority.
     *
     * @param description The description of the task.
     * @param deadline    The date and time by which the task should be completed.
     * @param priority    The priority of the task.
     */
    public Deadline(String description, LocalDateTime deadline, Priority priority) {
        super(description, TaskType.DEADLINE, priority);
        this.by = deadline;
    }

    /**
     * Returns this deadline with its completion status and formatted due date.
     *
     * @return The display representation of this deadline.
     */
    @Override
    public String toString() {
        return String.format("[%s]%s (by: %s)", this.getTaskType().getStorageCode(), super.toString(),
                DateTimeUtil.formatDisplay(this.by));
    }

    /**
     * Creates a {@code Deadline} from a command containing a {@code /by} delimiter.
     *
     * @param input The command string entered by the user.
     * @return The created {@code Deadline} task.
     * @throws IllegalCommandException If the description or date is missing or
     *                                 invalid.
     */
    public static Deadline parseDeadlineFromInputString(String input) {
        BaseTaskDetails taskDetails = parsePriority(input.substring("deadline".length()).trim());
        String details = taskDetails.details();
        int byIndex = StringParserUtil.requireIndex(details, BY_DELIMITER,
                "A deadline needs a non-empty /by date.");
        if (byIndex != details.lastIndexOf(BY_DELIMITER)) {
            throw new IllegalCommandException("A deadline can have only one /by date.");
        }
        String description = StringParserUtil.requireValue(details.substring(0, byIndex),
                "A deadline needs a non-empty description.");
        String deadlineText = StringParserUtil.requireValue(details.substring(byIndex + BY_DELIMITER.length()),
                "A deadline needs a non-empty /by date.");
        LocalDateTime deadline = DateTimeUtil.parse(deadlineText);
        return new Deadline(description, deadline, taskDetails.priority());
    }

    /**
     * Returns this deadline in the persistent storage format.
     *
     * @return The storage representation of this deadline.
     */
    @Override
    public String toDataFormat() {
        return String.format("%s | %d | %s | %s | %s", this.getTaskType().getStorageCode(), this.isDone ? 1 : 0,
                this.description, DateTimeUtil.formatStorage(this.by), this.getPriority().getStorageCode());
    }

}
