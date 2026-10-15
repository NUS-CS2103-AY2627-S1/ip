package nova.task;

/**
 * Represents a task without an attached date or time.
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
     * Returns this todo in the data-file format.
     *
     * @return serialized todo
     */
    @Override
    public String toDataString() {
        return "V2 | T | " + getDataStatus() + " | " + encodeDataField(description);
    }

    /**
     * Returns this todo in its display format.
     *
     * @return formatted todo
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
