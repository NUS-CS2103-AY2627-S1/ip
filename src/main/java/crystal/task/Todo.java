package crystal.task;

/**
 * Represents a task without a specific date or time.
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
     * Returns this todo in the format stored on disk.
     *
     * @return pipe-separated todo data.
     */
    @Override
    public String toDataString() {
        return "T | " + super.toDataString();
    }

    /**
     * Returns this task with its todo type icon.
     *
     * @return formatted todo details.
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
