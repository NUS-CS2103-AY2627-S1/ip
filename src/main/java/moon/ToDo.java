package moon;

/**
 * Represents a task without a date or time.
 */
public class ToDo extends Task {
    /**
     * Creates a to-do task.
     *
     * @param description the text describing the task
     */
    public ToDo(String description) {
        super(description);
    }

    /**
     * Returns this task in Moon's display format.
     *
     * @return the formatted to-do task
     */
    @Override
    public String toString() {
        return "[T][" + getStatusIcon() + "] " + description;
    }
}
