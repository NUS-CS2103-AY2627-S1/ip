package lynn;

/** Represents a task without a date or time. */
public class Todo extends Task {
    /**
     * Creates a todo with the given description.
     *
     * @param description the task description
     */
    public Todo(String description) {
        super(description, TaskType.TODO);
    }
}
