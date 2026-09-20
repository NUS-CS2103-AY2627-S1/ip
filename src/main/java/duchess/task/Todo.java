package duchess.task;

/**
 * Represents a basic todo task.
 */
public class Todo extends Task {
    /**
     * Creates an unfinished todo task.
     *
     * @param description the todo description
     */
    public Todo(String description) {
        super(description, TaskType.TODO);
    }
}
