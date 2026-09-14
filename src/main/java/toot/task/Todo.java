package toot.task;

/**
 * Represents a task without an attached date or time.
 */
public class Todo extends Task {
    /**
     * Creates a not-done todo with the given description.
     *
     * @param description Description of the todo.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns the todo with its task-type and completion markers.
     *
     * @return Formatted todo.
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
