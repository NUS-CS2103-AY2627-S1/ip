package mochi.task;

/**
 * Represents a task without an associated date or time.
 */
public class Todo extends Task {
    /**
     * Creates a todo with the given description.
     *
     * @param description Description of the todo.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns this todo in its user-facing display format.
     *
     * @return Formatted todo.
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
