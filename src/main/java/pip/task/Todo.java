package pip.task;


/**
 * Represents a task with no associated date or time.
 */
public class Todo extends Task {

    /**
     * Creates a new, incomplete todo with the given name.
     *
     * @param name The task's description.
     */
    public Todo(String name) {
        super(name);
    }

    /**
     * Returns this todo's display representation, prefixed with the
     * {@code [T]} type tag, e.g. {@code "[T][ ] read book"}.
     *
     * @return The formatted display string.
     */
    @Override
    public String toString() {
        return String.format("[T]") + super.toString();
    }
}
