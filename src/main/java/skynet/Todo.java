package skynet;

/**
 * Represents a to-do with a description.
 */
public class Todo extends Task {

    /**
     * Creates a to-do task.
     *
     * @param description the description of the to-do
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Gets the copy of the task.
     *
     * @return the copied task
     */
    @Override
    public Task copy() {
        Todo copy = new Todo(getDescription());
        if (isDone()) {
            copy.markAsDone();
        }
        return copy;
    }

    /**
     * Returns a string representation of the to-do.
     *
     * @return the to-do represented as a string
     */
    @Override
    public String toString() {
        return "[T][" + getStatusIcon() + "] " + getDescription();
    }
}
