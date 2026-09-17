package yuki.task;

/**
 * Represents a task without a deadline or scheduled time.
 */
public class ToDo extends Task {
    /**
     * Creates a to-do task.
     *
     * @param description The text describing the task.
     */
    public ToDo(String description) {
        super(description);
    }

    /**
     * Returns this to-do task in the format used in the task list.
     *
     * @return The formatted to-do task.
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
