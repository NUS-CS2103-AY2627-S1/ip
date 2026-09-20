package bkxss;

/**
 * A task without a date or time associated with it.
 */
public class Todo extends Task {
    /**
     * Creates an incomplete todo with the given description.
     *
     * @param description text that describes the todo
     */
    public Todo(String description) {
        super(description);
    }

    /** Returns the display text for this todo task. */
    @Override
    public String toString() {
        return "[T][" + getStatusIcon() + "] " + description;
    }
}
