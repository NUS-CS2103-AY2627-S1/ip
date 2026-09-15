package bob.task;

/**
 * Represents a task without an associated date or time.
 */
public class ToDo extends Task {
    /**
     * Creates a to-do task.
     *
     * @param description Description of the task.
     */
    public ToDo(String description) {
        super(description);
    }

    /**
     * Returns a display representation identifying this task as a to-do.
     *
     * @return Display representation of this to-do task.
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
