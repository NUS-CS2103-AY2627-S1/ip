package plana.task;

/**
 * Represents a ToDo task without an attached date or time.
 */
public class ToDo extends Task {

    /**
     * Creates an incomplete ToDo with the given description.
     *
     * @param description the task description.
     */
    public ToDo(String description) {
        super(description);
    }

    /**
     * Returns the ToDo in the format used when displaying the task list.
     *
     * @return the ToDo type, completion status, and description
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
