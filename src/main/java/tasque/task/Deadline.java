package tasque.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * A task that is due on a specified date.
 */
public class Deadline extends Task {
    protected LocalDate by;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d yyyy");

    /**
     * Creates a deadline from an ISO-8601 date string.
     *
     * @param description Description of the task.
     * @param by Due date in ISO-8601 format.
     * @throws java.time.format.DateTimeParseException If {@code by} is not a valid ISO-8601 date.
     */
    public Deadline(String description, String by) {
        super(description, TaskType.DEADLINE);
        this.by = LocalDate.parse(by);
    }

    @Override
    public boolean hasSameDetails(Task other) {
        return other instanceof Deadline deadline && super.hasSameDetails(other)
                && this.by.equals(deadline.by);
    }

    /**
     * Returns this deadline in the task storage format with its due date.
     *
     * @return Storage record including the deadline's due date.
     */
    @Override
    public String toStorageString() {
        return super.toStorageString() + " | " + this.by;
    }

    /**
     * Returns this deadline in the user-facing display format.
     *
     * @return Task display text including the formatted due date.
     */
    @Override
    public String toString() {
        return super.toString() + " (by: " + this.by.format(this.formatter) + ")";
    }
}
