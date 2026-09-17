package lion;

/**
 * Represents a task with a description and no date or time details.
 */
public class Todo extends Task {
    /**
     * Creates an incomplete todo task.
     *
     * @param description description of the task.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns the one-letter icon used to identify todo tasks.
     *
     * @return {@code T}.
     */
    @Override
    public String getTypeIcon() {
        return "T";
    }
}
