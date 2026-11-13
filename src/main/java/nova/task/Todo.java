package nova.task;

/**
 * Represents a task without a date.
 */
public class Todo extends Task {
    /**
     * Creates a todo task.
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

    @Override
    public String toDataString() {
        return "T | " + (isDone() ? "1" : "0") + " | " + getDescription();
    }
}
