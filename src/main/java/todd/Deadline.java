package todd;

import java.time.LocalDateTime;

/** Represents a task that must be completed by a specific date or time. */
public class Deadline extends Task {
    protected LocalDateTime by;

    /** Creates an incomplete deadline with the specified description and due date-time. */
    public Deadline(String description, LocalDateTime by) {
        super(description);
        this.by = by;
    }

    /** Returns the date and time by which this deadline should be completed. */
    public LocalDateTime getBy() {
        return by;
    }

    /** Returns a display-ready representation of this deadline. */
    @Override
    public String toString() {
        return "[D][" + getStatusIcon() + "] " + getDescription()
                + " (by: " + DateTimeUtil.format(by) + ")";
    }
}
