package tony.task;

/**
 * Represents a task without an associated date or time.
 */
public class Todo extends Task {
    /**
     * Creates an incomplete to-do task.
     *
     * @param description the text describing the task.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns the to-do task category.
     *
     * @return the to-do task type
     */
    @Override
    protected TaskType getTaskType() {
        return TaskType.TODO;
    }
}
