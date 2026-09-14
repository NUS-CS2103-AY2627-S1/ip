package kaya.task;

/**
 * Represents a task without an associated date or time.
 */
public class Todo extends Task {
    /**
     * Creates a todo with the given description.
     *
     * @param description the description of the todo
     */
    public Todo(String description) {
        super(description);
    }

    /** {@inheritDoc} */
    @Override
    public Task copy() {
        return copyStatusTo(new Todo(getDescription()));
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
