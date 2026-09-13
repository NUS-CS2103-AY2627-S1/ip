package odysseus;

/**
 * A task without a date or time.
 */
public class Todo extends Task {
    /**
     * Creates an incomplete to-do task.
     *
     * @param description text describing the task.
     */
    public Todo(String description) {
        super(description);
    }

    @Override
    protected String getTypeMarker() {
        return "T";
    }
}
