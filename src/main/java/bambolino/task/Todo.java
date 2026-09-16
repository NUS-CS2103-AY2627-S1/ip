package bambolino.task;

/**
 * A task without a date or time attached to it.
 */
public class Todo extends Task {
    /**
     * Creates an incomplete to-do task.
     *
     * @param description The task description.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns this to-do task in the list-display format.
     *
     * @return The task type, status icon, and description.
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
