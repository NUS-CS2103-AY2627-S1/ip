package minty.task;

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
     * Returns the todo in Minty's display format.
     *
     * @return todo type followed by its status and description.
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }

    /**
     * Returns the todo in the format used by the save file.
     *
     * @return serialized todo.
     */
    @Override
    public String toDataString() {
        return "T | " + (isDone ? "1" : "0") + " | " + escapeDataField(description);
    }
}
