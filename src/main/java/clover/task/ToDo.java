package clover.task;

/**
 * Represents a task without an associated date.
 */
public class ToDo extends Task {
    /**
     * Creates a todo task with the supplied description.
     */
    public ToDo(String description) {
        super(description);
    }

    /**
     * Creates a todo that is optionally linked to a tutoree.
     */
    public ToDo(String description, String tutoreeName) {
        super(description, tutoreeName);
    }

    /**
     * Returns this todo in Clover's display format.
     */
    @Override
    public String toString() {
        return "[T] " + super.toString();
    }
}
