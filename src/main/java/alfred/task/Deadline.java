package alfred.task;

import java.time.LocalDateTime;

/**
 * Represents a task that must be completed by a specified time.
 */
public class Deadline extends Task {
    private final LocalDateTime by;

    /**
     * Creates an incomplete deadline with its description and due time.
     *
     * @param description text describing the task
     * @param by due date and time
     */
    public Deadline(String description, LocalDateTime by) {
        super(TaskType.DEADLINE, description);
        this.by = by;
    }

    /**
     * Returns the due date or time for storage purposes.
     *
     * @return the due date and time
     */
    public LocalDateTime getBy() {
        return by;
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasSameIdentityAs(Task other) {
        return other instanceof Deadline deadline && super.hasSameIdentityAs(other) && by.equals(deadline.by);
    }

    /**
     * Returns this deadline with its type, completion status, and due time.
     *
     * @return the formatted deadline task
     */
    @Override
    public String toString() {
        return super.toString() + " (by: " + TaskDateTime.format(by) + ")";
    }
}
