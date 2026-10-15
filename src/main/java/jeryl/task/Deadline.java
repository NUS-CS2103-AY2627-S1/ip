package jeryl.task;

import java.time.LocalDate;

/**
 * A task that needs to be done before a specific date.
 */
public class Deadline extends Task {
    protected LocalDate by;

    /**
     * Creates a not-yet-done deadline task due on the given date, with no
     * priority set.
     */
    public Deadline(String description, LocalDate by) {
        super(description);
        this.by = by;
    }

    /**
     * Creates a not-yet-done deadline task due on the given date, with the
     * given priority.
     */
    public Deadline(String description, LocalDate by, Priority priority) {
        super(description, priority);
        this.by = by;
    }

    @Override
    public String toString() {
        return getPriorityTag() + "[D]" + super.toString() + " (by: " + by.format(DateFormats.DISPLAY) + ")";
    }

    @Override
    public String toFileString() {
        return withPriority("D | " + super.toFileString() + " | " + by);
    }
}
