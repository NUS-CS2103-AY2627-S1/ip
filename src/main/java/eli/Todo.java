package eli;

/**
 * Represents a task with no date or time attached to it.
 */
public class Todo extends Task {
    /**
     * Creates a to-do task.
     *
     * @param description the task description
     */
    public Todo(String description) {
        super(description, TaskType.TODO);
    }
}
