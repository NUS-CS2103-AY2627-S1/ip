package bobby.task;

/**
 * A task without a date or time.
 */
public class Todo extends Task {
    /**
     * Creates a to-do task.
     *
     * @param description the task description
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns this to-do task in the standard list format.
     *
     * @return the formatted to-do task
     */
    @Override
    public String toString() {
        return "[T][" + getStatusIcon() + "] " + description;
    }
}
