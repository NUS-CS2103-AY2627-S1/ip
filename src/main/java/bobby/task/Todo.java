package bobby.task;

/**
 * Represents a task without any date or time attached.
 */
public class Todo extends Task {
    /**
     * Creates a todo task.
     *
     * @param description text that describes the todo.
     */
    public Todo(String description) {
        super(description, TaskType.TODO);
    }
}
