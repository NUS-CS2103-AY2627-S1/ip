package zabud.tasks;

/**
 * Represents a task without a date or time constraint.
 */
public class Todo extends Task {
    /**
     * Creates a to-do task.
     *
     * @param description the text describing the task.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns the Duke code for to-do tasks.
     *
     * @return {@code "T"}.
     */
    @Override
    protected String getTaskType() {
        return "T";
    }
}
