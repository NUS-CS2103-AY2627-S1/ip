package harvey.task;
/**
 * A task with no date or time attached to it, e.g. {@code borrow book}.
 * Adds nothing to {@link Task} except the {@code [T]} type marker.
 */
public class Todo extends Task {
    /**
     * Creates a todo that is not done yet.
     *
     * @param description the text describing the task.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * {@inheritDoc}
     * The type marker {@code [T]} is placed in front of the status box,
     * giving e.g. {@code [T][ ] borrow book}.
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }

    /**
     * {@inheritDoc}
     * Prefixes the {@code T} type letter, giving e.g. {@code T | 0 | borrow book}.
     */
    @Override
    public String toFileFormat() {
        return "T | " + super.toFileFormat();
    }
}
