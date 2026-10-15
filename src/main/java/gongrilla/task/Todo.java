package gongrilla.task;

/**
 * Represents a task without an attached date or time.
 */
public class Todo extends Task {
    /**
     * Creates an incomplete todo with the supplied description.
     *
     * @param name description of the todo.
     */
    public Todo(String name) {
        super(name);
    }

    /**
     * Converts this todo into one encoded storage record.
     *
     * @return serialized todo record.
     */
    @Override
    public String toDataString() {
        return "T2 | " + commonDataFields();
    }

    /**
     * Returns the todo's user-facing representation.
     *
     * @return formatted todo.
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
