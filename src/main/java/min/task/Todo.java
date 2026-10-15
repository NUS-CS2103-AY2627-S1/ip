package min.task;

/**
 * Represents a task without a date or time.
 */
public class Todo extends Task {

    /**
     * Creates an incomplete todo with the given description.
     *
     * @param description The todo description. Must not be null, blank, or contain the
     *                    storage field separator.
     */
    public Todo(String description) {
        super(description);
    }

    /** Returns this todo in the format used for persistent storage. */
    @Override
    public String toFileString() {
        return "T" + FILE_FIELD_SEPARATOR + getStatusValue() + FILE_FIELD_SEPARATOR
                + getDescription();
    }

    /** Returns this todo in a format suitable for display. */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
