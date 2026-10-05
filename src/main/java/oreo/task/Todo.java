package oreo.task;

/**
 * Represents a task without a date or time.
 */
public class Todo extends Task {
    /**
     * Creates a to-do task.
     *
     * @param description text describing the task
     */
    public Todo(String description) {
        super(description);
    }

    @Override
    protected String getTypeIcon() {
        return "T";
    }

    @Override
    protected String getDetails() {
        return "";
    }

    @Override
    protected String toStorageString() {
        return this.getTypeIcon() + " | " + this.getStatusIcon() + " | " + this.getDescription()
                + getTagsStorageRepresentation();
    }
}
