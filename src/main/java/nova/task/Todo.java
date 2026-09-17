package nova.task;

/**
 * Represents a task without an associated date or time.
 */
public class Todo extends Task {
    /**
     * Creates an incomplete todo with the given description.
     *
     * @param description description of the todo.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getTypeIcon() {
        return "T";
    }
}
