package kong.task;

/**
 * Represents a task without an associated date.
 */
public class Todo extends Task {
    /**
     * Creates an incomplete todo.
     *
     * @param desc task description
     */
    public Todo(String desc) {
        super(desc);
    }

    /**
     * Creates a todo with an explicit completion status.
     *
     * @param desc task description
     * @param isDone whether the todo is complete
     */
    public Todo(String desc, boolean isDone) {
        super(desc, isDone);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return String.format("[T]%s", super.toString());
    }

    /** {@inheritDoc} */
    @Override
    public String toFileString() {
        return String.format("T | %s | %s", this.getDoneStatus(), this.getDescription());
    }
}
