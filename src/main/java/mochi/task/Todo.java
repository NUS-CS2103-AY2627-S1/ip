package mochi.task;

/**
 * Represents a task without an associated date or time.
 */
public class Todo extends Task {
    /**
     * Creates an incomplete todo.
     *
     * @param description description of the todo
     */
    public Todo(String description) {
        super(description);
    }

    private Todo(Todo source) {
        super(source);
    }

    /** {@inheritDoc} */
    @Override
    public Todo copy() {
        return new Todo(this);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }

    /** {@inheritDoc} */
    @Override
    public String toFileString() {
        return "T" + STORAGE_SEPARATOR + super.toFileString();
    }
}
