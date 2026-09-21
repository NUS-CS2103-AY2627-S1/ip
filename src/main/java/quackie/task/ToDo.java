package quackie.task;

/**
 * A task without an attached date or time.
 */
public class ToDo extends Task {
    /**
     * Creates a not-done ToDo task.
     *
     * @param description the text describing the task
     */
    public ToDo(String description) {
        super(description);
    }

    @Override
    protected String getTypeIcon() {
        return "[T]";
    }
}
