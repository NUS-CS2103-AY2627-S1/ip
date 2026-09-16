package anaconda.task;

/**
 * Represents a task without an attached date or time.
 */
public class ToDo extends Task {
    /**
     * Creates a ToDo with the given description.
     *
     * @param description Description of the task.
     */
    public ToDo(String description) {
        super(description);
    }

    /**
     * Formats the ToDo using its type, completion status, and description.
     *
     * @return Display representation of this ToDo.
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
