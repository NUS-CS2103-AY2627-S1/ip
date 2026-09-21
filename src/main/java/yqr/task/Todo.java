package yqr.task;

/**
 * Represents a task without an attached date or time.
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
     * Returns a displayable version of this todo task.
     *
     * @return todo type icon, status icon, and description.
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
