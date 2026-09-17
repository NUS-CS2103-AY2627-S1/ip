package baemax;

/**
 * A task that should be completed by a specified date (and optionally a time).
 */
public class Deadline extends Task {
    /** When this task is due. */
    private final TaskDate by;

    /**
     * Creates a pending deadline task.
     *
     * @param description the deadline description
     * @param by when the task is due
     */
    public Deadline(String description, TaskDate by) {
        super(description);
        this.by = by;
    }

    /**
     * Formats the deadline with its type, completion status, and due date.
     *
     * @return the formatted deadline
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by + ")";
    }

    /**
     * Prefixes the shared fields with the deadline type tag {@code D} and
     * appends the due date in ISO form so it can be read back.
     *
     * @return the save-file representation of this deadline
     */
    @Override
    public String toFileFormat() {
        return "D | " + super.toFileFormat() + " | " + by.toStorageString();
    }

    /**
     * Returns an independent copy of this deadline.
     *
     * @return a new deadline with the same description, due date, and done state
     */
    @Override
    public Task copy() {
        Deadline copy = new Deadline(getDescription(), by);
        copyDoneStatusInto(copy);
        return copy;
    }
}
