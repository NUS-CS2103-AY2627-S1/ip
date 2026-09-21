package janet.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that must be completed before a specified date or time.
 */
public class Deadline extends Task {
    /** Formatter used to present deadline dates in a readable form. */
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd uuuu", Locale.ENGLISH);

    /** The date by which this task should be completed. */
    private LocalDate deadline;

    /**
     * Creates a new incomplete deadline task.
     *
     * @param description the text describing the task.
     * @param deadline the date by which the task is due.
     */
    public Deadline(String description, LocalDate deadline) {
        super(description);
        this.deadline = deadline;
    }

    /**
     * Returns the deadline date.
     *
     * @return the deadline date
     */
    public LocalDate getDeadline() {
        return deadline;
    }

    /**
     * Returns the icon identifying this task as a deadline.
     *
     * @return the deadline type icon
     */
    @Override
    public String getTypeIcon() {
        return "[D]";
    }

    /**
     * Returns the task description together with its deadline.
     *
     * @return the description and deadline
     */
    @Override
    public String getDescription() {
        return description + " (by: " + deadline.format(DISPLAY_FORMAT) + ")";
    }
}
