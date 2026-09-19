package nori.task;

/**
 * Represents a task with no associated date or time.
 */
public class Todo extends Task {

    /**
     * Creates an incomplete to-do with the given description.
     *
     * @param description the to-do description.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns this to-do prefixed with its {@code [T]} type icon.
     *
     * @return the task rendering behind a {@code [T]} type icon, for
     *         example {@code [T][ ] borrow book}.
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
