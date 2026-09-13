package alfred.task;

/**
 * Represents a task without a date or time.
 */
public class Todo extends Task {
    /**
     * Creates an incomplete to-do task with the given description.
     *
     * @param description text describing the task
     */
    public Todo(String description) {
        super(TaskType.TODO, description);
    }
}
