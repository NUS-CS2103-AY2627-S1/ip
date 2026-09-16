package tasque.task;

/**
 * A task without a deadline or time range.
 */
public class Todo extends Task {
    /**
     * Creates an incomplete todo task.
     *
     * @param description Description of the task.
     */
    public Todo(String description) {
        super(description, TaskType.TODO);
    }
}
