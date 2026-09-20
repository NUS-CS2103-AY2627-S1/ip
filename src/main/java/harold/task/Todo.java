package harold.task;

/**
 * Represents a task without an attached date or time.
 */
public class Todo extends Task {
    /**
     * Creates an incomplete todo with the given description.
     *
     * @param description Description of the task.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns the icon used to identify todos.
     *
     * @return Todo type icon.
     */
    @Override
    public String getTypeIcon() {
        return "T";
    }
}
