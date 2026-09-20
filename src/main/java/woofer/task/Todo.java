package woofer.task;

/**
 * A task without an associated date or time.
 */
public class Todo extends Task {
    /**
     * Creates a new todo task.
     *
     * @param description text describing the task.
     */
    public Todo(String description) {
        super(description, TaskType.TODO);
    }
}
