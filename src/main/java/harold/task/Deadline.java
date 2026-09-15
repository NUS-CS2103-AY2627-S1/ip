package harold.task;

import java.time.LocalDate;

/**
 * Represents a task that must be completed by a specified date.
 */
public class Deadline extends Task {
    private final LocalDate by;

    /**
     * Creates an incomplete deadline with the given description and due date.
     *
     * @param description Description of the task.
     * @param by Date by which the task must be completed.
     */
    public Deadline(String description, LocalDate by) {
        super(description);
        this.by = by;
    }

    /**
     * Returns the deadline date.
     *
     * @return Deadline date.
     */
    public LocalDate getBy() {
        return by;
    }

    /**
     * Returns the icon used to identify deadlines.
     *
     * @return Deadline type icon.
     */
    @Override
    public String getTypeIcon() {
        return "D";
    }

    /**
     * Returns whether another deadline has the same description and due date.
     *
     * @param other Task to compare with this deadline.
     * @return {@code true} if both deadlines have the same identifying details.
     */
    @Override
    boolean hasSameDetails(Task other) {
        return super.hasSameDetails(other) && by.equals(((Deadline) other).by);
    }

    /**
     * Returns the deadline formatted with its status, description, and due date.
     *
     * @return User-facing deadline representation.
     */
    @Override
    public String toString() {
        return String.format("%s (by: %s)", super.toString(), TaskDate.format(by));
    }
}
