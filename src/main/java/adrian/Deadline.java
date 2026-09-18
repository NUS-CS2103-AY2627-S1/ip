package adrian;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a task that must be completed by a specific date and time.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter OUTPUT_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy, h:mm a");

    /** Date and time by which this task must be completed. */
    private final LocalDateTime dueDateTime;

    /**
     * Creates an incomplete deadline with the given description and due date.
     *
     * @param description description of the task.
     * @param dueDateTime date and time by which the task must be completed.
     */
    public Deadline(String description, LocalDateTime dueDateTime) {
        super(description, TaskType.DEADLINE);
        this.dueDateTime = dueDateTime;
    }

    /**
     * Returns a display-friendly representation containing the due date and time.
     *
     * @return formatted deadline description.
     */
    @Override
    public String toString() {
        return super.toString()
                + " (by: " + dueDateTime.format(OUTPUT_FORMAT) + ")";
    }

    /**
     * Returns the deadline in the format used for persistent storage.
     *
     * @return serialized deadline data.
     */
    @Override
    public String toDataString() {
        return "D | " + super.toDataString()
                + " | " + dueDateTime;
    }
}
