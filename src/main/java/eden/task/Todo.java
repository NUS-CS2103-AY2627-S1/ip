package eden.task;

/**
 * Represents a task without a date or time.
 */
public class Todo extends Task {
    /**
     * Creates an unmarked todo task.
     *
     * @param description task description.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toDataString() {
        return "T | " + (isMarked() ? "1" : "0") + " | "
                + escapeDataField(getDescription());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
