package lumi.task;

/**
 * Represents a task without an attached date or time.
 */
public class Todo extends Task {
    /**
     * Creates an incomplete to-do task.
     *
     * @param description Description of the task.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns the to-do in the format used when displaying task lists.
     *
     * @return The task type, status, and description.
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
