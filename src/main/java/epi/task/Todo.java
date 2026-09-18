package epi.task;

/** Represents a task without an associated date or time. */
public class Todo extends Task {
    /**
     * Creates an incomplete todo task.
     *
     * @param description Task description, preserved without trimming.
     * @throws IllegalArgumentException If the description is null, blank, or contains
     *     a pipe character, a newline, or a carriage return.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Copies a todo's description and completion state.
     *
     * @param original Non-null todo to copy.
     */
    private Todo(Todo original) {
        super(original);
    }

    /**
     * Returns an independent todo snapshot, preserving its completion state.
     *
     * @return A new todo with the same description and completion state.
     */
    @Override
    public Todo copy() {
        return new Todo(this);
    }

    /**
     * Returns the serialized todo representation.
     *
     * @return A {@code T} record containing the completion status and description.
     */
    @Override
    public String toFileFormat() {
        return "T | " + super.toFileFormat();
    }

    /**
     * Returns the user-facing todo representation.
     *
     * @return The {@code [T]} prefix followed by the status marker and description.
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
