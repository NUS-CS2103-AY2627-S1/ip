package sherlock.task;

/**
 * Represents a task without a date or time.
 */
public class Todo extends Task {
    /**
     * Creates an incomplete task without a date or time.
     *
     * @param description text describing the task
     */
    public Todo(String description) {
        super(description);
    }

    @Override
    protected String getTypeIcon() {
        return "T";
    }
}
