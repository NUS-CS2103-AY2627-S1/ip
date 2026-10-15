package alexa;

/** A task without a date or time. */
public class Todo extends Task {
    /**
     * Creates a to-do task.
     *
     * @param description The task description.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns this to-do in the line format used for persistence.
     *
     * @return the saved representation of this to-do
     */
    @Override
    public String toStorageString() {
        return "T | " + (isDone() ? "1" : "0") + " | " + getDescription();
    }

    /**
     * Returns this to-do in Alexa's user-facing display format.
     *
     * @return the formatted to-do
     */
    @Override
    public String toString() {
        return "[T][" + getStatusIcon() + "] " + getDescription();
    }
}