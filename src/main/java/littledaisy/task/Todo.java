package littledaisy.task;

/**
 * A task with nothing but a description, e.g. {@code borrow book}.
 *
 * <p>This is the worked example of the three task types. {@link Deadline} and
 * {@link Event} follow the same shape, but each carries extra information.
 */
public class Todo extends Task {
    /**
     * Creates a todo that is not done yet.
     *
     * @param description what the todo is about
     */
    public Todo(String description) {
        // A subclass cannot set up the inherited fields itself, so it hands
        // the description to the Task constructor. super(...) must be the
        // very first statement of a constructor.
        super(description);
    }

    /**
     * Returns this todo with its type icon in front, e.g. {@code [T][ ] borrow
     * book}.
     *
     * @return the type icon followed by the rendering inherited from
     *     {@link Task}
     */
    @Override
    public String toString() {
        // super.toString() is Task's version, which gives "[ ] borrow book".
        // Prefixing it keeps the status-box format in one place.
        return "[T]" + super.toString();
    }
}
