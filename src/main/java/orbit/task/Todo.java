package orbit.task;

/**
 * Represents a task without a specific date or time.
 */
public class Todo extends Task {
    /**
     * Creates a new todo task.
     *
     * @param description description of the task
     */
    public Todo(String description) {
        super(description, TaskType.TODO);
    }
}
