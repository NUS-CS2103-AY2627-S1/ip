package moon;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that must be completed by a specified time.
 */
public class Deadline extends Task {
    /** The format used when showing a deadline to the user. */
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);

    /** The date by which this task must be completed. */
    private final LocalDate by;

    /**
     * Creates a deadline task.
     *
     * @param description the text describing the task
     * @param by the date by which the task must be completed
     */
    public Deadline(String description, LocalDate by) {
        super(description);
        this.by = by;
    }

    /**
     * Returns whether this deadline has the same description and date as another deadline.
     *
     * @param other the task to compare with
     * @return whether both deadlines have the same details
     */
    @Override
    public boolean hasSameDetailsAs(Task other) {
        return other instanceof Deadline deadline
                && description.equals(deadline.description)
                && by.equals(deadline.by);
    }

    /**
     * Returns this task in Moon's display format.
     *
     * @return the formatted deadline task
     */
    @Override
    public String toString() {
        return "[D][" + getStatusIcon() + "] " + description
                + " (by: " + by.format(DISPLAY_FORMAT) + ")";
    }

    /**
     * Returns this deadline in the format used by the data file.
     *
     * @return a line that can be saved to Moon's data file
     */
    @Override
    public String toSaveFormat() {
        return "D | " + (isDone ? "1" : "0") + " | " + description + " | " + by;
    }
}
