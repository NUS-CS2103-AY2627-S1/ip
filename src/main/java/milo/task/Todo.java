package milo.task;

/**
 * Represents a task without an attached date or time.
 */
public class Todo extends Task {
    /**
     * Creates an incomplete todo with the given description.
     *
     * @param description description of the todo.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns the todo with its type and completion status.
     *
     * @return formatted todo.
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
