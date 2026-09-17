package baemax;

/**
 * A task without an attached date or time.
 */
public class Todo extends Task {
    /**
     * Creates a pending todo task.
     *
     * @param description the todo description
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Formats the todo with its type and completion status.
     *
     * @return the formatted todo
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }

    /**
     * Prefixes the shared fields with the todo type tag {@code T}.
     *
     * @return the save-file representation of this todo
     */
    @Override
    public String toFileFormat() {
        return "T | " + super.toFileFormat();
    }

    /**
     * Returns an independent copy of this todo.
     *
     * @return a new todo with the same description and done state
     */
    @Override
    public Task copy() {
        Todo copy = new Todo(getDescription());
        copyDoneStatusInto(copy);
        return copy;
    }
}
