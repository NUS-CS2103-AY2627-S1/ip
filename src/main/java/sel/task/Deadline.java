package sel.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that must be completed by a specific date and time.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY_FORMAT =
        DateTimeFormatter.ofPattern("MMM d yyy, h:mma", Locale.ENGLISH);

    protected LocalDateTime ddl;

    /**
     * Creates a new, not-done deadline task.
     *
     * @param description text describing the task.
     * @param ddl the date and time by which the task must be completed.
     */
    public Deadline(String description, LocalDateTime ddl) {
        super(description);
        this.ddl = ddl;
    }

    /**
     * Returns the date and time by which this task must be completed.
     *
     * @return the deadline's date and time.
     */
    public LocalDateTime getDdl() {
        return this.ddl;
    }

    /**
     * Checks whether another task is a deadline with the same description
     * and the same due date/time.
     *
     * @param other the task to compare against, possibly {@code null}.
     * @return {@code true} if both deadlines describe the same thing.
     */
    @Override
    public boolean hasSameDetailsAs(Task other) {
        // The superclass already checked that other is a Deadline, so the
        // cast below cannot fail.
        return super.hasSameDetailsAs(other) && this.ddl.equals(((Deadline) other).ddl);
    }

    /**
     * Returns the display form of this deadline, e.g.
     * {@code "[D][ ] return book(by:Dec 2 2019, 6:00PM)"}.
     *
     * @return the formatted string representation of this deadline.
     */
    @Override
    public String toString() {
        return "[D][" + this.getStatusIcon() + "] " + this.description + "(by:" + this.ddl.format(DISPLAY_FORMAT) + ")";
    }
}
