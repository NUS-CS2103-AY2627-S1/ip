package groot.task;

/**
 * Represents a task without an attached date or time.
 */
public class Todo extends Task {

    /**
     * Creates an incomplete todo task.
     *
     * @param description Description of the task.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns the todo in the format used by the local data file.
     *
     * @return Pipe-separated todo data.
     */
    @Override
    public String toDataString() {
        return "T | " + super.toDataString();
    }

    /**
     * Returns the todo in its display format.
     *
     * @return Formatted todo text.
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
