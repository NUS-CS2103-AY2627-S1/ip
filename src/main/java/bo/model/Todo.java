package bo.model;

/**
 * A task without an associated date or time.
 */
public class Todo extends Task {

    /**
     * Creates an incomplete todo task.
     *
     * @param description the text describing the task.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns the icon used for todo tasks.
     *
     * @return {@code T}.
     */
    @Override
    public String getTypeIcon() {
        return "T";
    }
}
