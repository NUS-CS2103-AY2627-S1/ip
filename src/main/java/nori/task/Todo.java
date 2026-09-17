package nori.task;

/**
 * Represents a task without an associated date or time.
 */
public class Todo extends Task {
    /**
     * Creates an incomplete todo task.
     *
     * @param description Description of the task.
     */
    public Todo(String description) {
        super(description);
    }

    @Override
    protected String getTypeIcon() {
        return "T";
    }

    /** {@inheritDoc} */
    @Override
    public String toStorageString() {
        return "T | " + (isDone() ? "1" : "0") + " | " + description;
    }
}
