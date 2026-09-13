package nelson.task;

/**
 * Represents a task without date or time information.
 */
public class Todo extends Task {
    /**
     * Creates an incomplete ToDo task.
     *
     * @param description the text describing the task
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns the ToDo type, status, and description.
     *
     * @return the formatted ToDo task
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
