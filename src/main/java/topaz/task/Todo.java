package topaz.task;

/**
 * Represents a basic todo task.
 */
public class Todo extends Task {
    /**
     * Creates an incomplete todo task.
     *
     * @param description the task description
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns the todo task type.
     *
     * @return the todo task type
     */
    @Override
    public TaskType getTaskType() {
        return TaskType.TODO;
    }

    /**
     * Serializes this todo for storage.
     *
     * @return the todo in save-file format
     */
    @Override
    public String toFileString() {
        return "T | " + (isDone() ? "1" : "0") + " | " + getDescription();
    }
}
