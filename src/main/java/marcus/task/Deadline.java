package marcus.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that must be completed by a specified time.
 */
public class Deadline extends Task {
    /** Formats a deadline date for the user-facing task list. */
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd uuuu", Locale.ENGLISH);

    private final LocalDate by;

    /**
     * Creates an incomplete deadline task.
     *
     * @param description description of the task.
     * @param by date by which the task should be completed.
     */
    public Deadline(String description, LocalDate by) {
        super(description);
        this.by = by;
    }

    /**
     * Reports whether this deadline is due on the supplied date.
     *
     * @param date date to check.
     * @return whether this task is due on {@code date}.
     */
    @Override
    public boolean occursOn(LocalDate date) {
        return by.equals(date);
    }

    /**
     * Returns this deadline in its user-facing display format.
     *
     * @return deadline type, status, description, and formatted due date.
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by.format(DISPLAY_DATE_FORMAT) + ")";
    }

    @Override
    public String toStringWithoutTags() {
        return "[D]" + super.toStringWithoutTags() + " (by: " + by.format(DISPLAY_DATE_FORMAT) + ")";
    }

    /**
     * Returns this deadline in the format used for persistence.
     *
     * @return deadline type, completion state, description, and ISO date.
     */
    @Override
    public String toFileString() {
        return "D | " + (isDone ? "1" : "0") + " | " + description + " | " + by
                + getTagsFileSuffix();
    }
}
