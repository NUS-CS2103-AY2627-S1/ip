package stephen.task;

/**
 * Represents a task without an attached date or time.
 */
public class Todo extends Task {
    /**
     * Creates an incomplete todo.
     *
     * @param description description of the todo
     */
    public Todo(String description) {
        super(TaskType.TODO, description);
    }
}
