package ruby.task;

/**
 * Represents a task without an associated date or time.
 */
public class Todo extends Task {
    /**
     * Creates a todo with the specified description.
     *
     * @param description Description of the todo.
     */
    public Todo(String description) {
        super(description);
    }

    @Override
    public String toString() {
        return "[" + TaskDataFormat.TODO_TYPE + "]"
                + super.toString();
    }

    @Override
    public String toDataString() {
        return TaskDataFormat.TODO_TYPE
                + TaskDataFormat.PART_SEPARATOR
                + super.toDataString();
    }
}
