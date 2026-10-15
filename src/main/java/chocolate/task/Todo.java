package chocolate.task;

/**
 * Represents a task without a date or time.
 */
public class Todo extends Task {
    /**
     * Creates a todo with the specified description.
     *
     * @param description Description of the task.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns the display form of this todo.
     *
     * @return Todo type, status, and description.
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
