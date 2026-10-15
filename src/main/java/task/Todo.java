package task;

/**
 * Represents a task without date or time information.
 */
public class Todo extends Task {

    /**
     * Creates an incomplete todo.
     *
     * @param description description of the todo.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns this todo in its user-facing format.
     *
     * @return formatted todo.
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }

    /**
     * Serializes this todo for persistent storage.
     *
     * @return todo in the application's file format.
     */
    @Override
    public String toFileFormat() {
        return "TODO | " + (isDone() ? "1" : "0") + " | " + getDescription();
    }
}
