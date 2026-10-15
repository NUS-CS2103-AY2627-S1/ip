package sage.task;

/**
 * Represents a simple to-do task.
 */
public class Todo extends Task {
    /**
     * Creates a to-do task with the given description.
     *
     * @param description the task description.
     */
    public Todo(String description) {
        super(description, TaskType.TODO);
    }
}
