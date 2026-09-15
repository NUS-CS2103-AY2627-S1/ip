package duke.task;

/**
 * Represents a todo task that only contains a description.
 */
public class ToDo extends Task {

    /**
     * Creates a todo task with the given description.
     *
     * @param description Description of the todo.
     */
    public ToDo(String description) {
        super(description);
    }

    /**
     * Returns a user-facing representation of a todo task.
     *
     * @return A display string prefixed with {@code [T]}.
     */
    @Override
    public String toString() {
        return "[T] " + super.toString();
    }
}
