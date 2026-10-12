package braun.task;

/**
 * Represents a todo task without any specific date or time constraints.
 */
public class Todo extends Task {

    /**
     * Constructs a new {@code Todo} task with the specified description.
     *
     * @param description the textual description of the todo task.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns the formatted string representation of the todo task,
     * prefixed with the [T] identifier tag.
     *
     * @return formatted string such as "[T][ ] borrow book" or "[T][X] borrow book".
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }

    /**
     * Returns the formatted string representation of the todo task for file storage.
     *
     * @return a pipe-delimited string prefixed with the 'T' tag.
     */
    @Override
    public String toFileFormat() {
        return "T | " + super.toFileFormat();
    }
}
