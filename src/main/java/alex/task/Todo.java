package alex.task;

/**
 * Represents a task without a date.
 */
public class Todo extends Task {
    /**
     * Creates a todo with the specified description.
     *
     * @param description description of the task.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
