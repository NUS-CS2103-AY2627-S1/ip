package downtowngurl.task;

/**
 * Represents a task without any attached date or time.
 */
public class Todo extends Task {
    /**
     * Creates a todo task.
     *
     * @param description Details of the task.
     */
    public Todo(String description) {
        super(description, TaskType.TODO);
    }
}
