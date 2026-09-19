package benji;

/**
 * Represents a simple to-do task.
 *
 * <p>A to-do task contains a description and completion status.</p>
 */
public class Todo extends Task {

    /**
     * Creates a new to-do task with the given description.
     *
     * @param description the description of the to-do task.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns a string representation of this to-do task.
     *
     * @return the task representation prefixed with "[T]".
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
