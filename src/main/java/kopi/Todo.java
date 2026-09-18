package kopi;

/**
 * Represents a task without a date or time.
 */
public class Todo extends Task {
    /** Creates an incomplete todo with the given description. */
    public Todo(String description) {
        super(description);
    }

    /** {@inheritDoc} */
    @Override
    public String toDataString() {
        return "T | " + (isDone ? "1" : "0") + " | " + description;
    }

    /** Returns the todo in its user-facing display format. */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
