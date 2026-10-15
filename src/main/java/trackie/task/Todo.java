package trackie.task;

/**
 * Represents a task without an attached date or time.
 */
public class Todo extends Task {
    /**
     * Creates an incomplete todo with the given description.
     *
     * @param description description of the todo
     */
    public Todo(String description) {
        super(description, TaskType.TODO);
    }
}
