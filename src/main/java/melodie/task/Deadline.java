package melodie.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that must be completed by a specific date and time.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY_DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd uuuu, h:mm a", Locale.ENGLISH);
    private static final String IDENTIFIER = "[D]";
    private final LocalDateTime dueDateTime;

    /**
     * Creates a deadline task with the specified description and due date.
     *
     * @param description Description of the deadline task.
     * @param dueDateTime Date and time by which the task is due.
     */
    public Deadline(String description, LocalDateTime dueDateTime) {
        super(description);
        this.dueDateTime = dueDateTime;
    }

    /**
     * Creates a deadline from the ISO date-time format used in the storage file.
     *
     * @param description Description of the deadline task.
     * @param dueDateTime Due date and time in ISO format.
     */
    public Deadline(String description, String dueDateTime) {
        this(description, LocalDateTime.parse(dueDateTime));
    }

    public LocalDateTime getDueDateTime() {
        return this.dueDateTime;
    }

    @Override
    public String toStorageString() {
        return "D | " + super.toStorageString()
                + " | " + this.dueDateTime.toString();
    }

    @Override
    public String toString() {
        return IDENTIFIER + super.toString()
                + " (by: " + this.dueDateTime.format(DISPLAY_DATE_TIME_FORMATTER) + ")";
    }
}
