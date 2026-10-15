package clearblue.task;

/**
 * Represents a task that has no date or time attached to it.
 */
public class Todo extends Task {
    /**
     * Creates an incomplete todo with the given description.
     *
     * @param description description of the todo
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns this todo's display text, e.g. {@code "[T][X] read book"}.
     *
     * @return display text for this todo
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
