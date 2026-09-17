package orbit;

/**
 * A task without an attached date or time.
 */
public class Todo extends Task {
    /**
     * Creates a todo task.
     *
     * @param description Task description.
     */
    public Todo(String description) {
        super(description);
    }

    @Override
    protected TaskType getType() {
        return TaskType.TODO;
    }
}
