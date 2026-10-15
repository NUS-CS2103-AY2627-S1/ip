package beemo.task;

/**
 * Represents a task without an attached date or time.
 */
public class Todo extends Task {
    /**
     * Creates an incomplete todo with the specified description.
     *
     * @param description Description of the todo.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns the todo's type, status, and description for display.
     *
     * @return Display form of the todo.
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
