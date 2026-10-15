package duke.task;

/**
 * Represents a task without any date or time attached to it.
 */
public class ToDo extends Task {
    /**
     * Creates a new todo task.
     */
    public ToDo(String description) {
        super(description);
    }

    /**
     * Returns this todo in the format shown to the user.
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
