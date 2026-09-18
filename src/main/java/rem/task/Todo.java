package rem.task;

/**
 * Represents a task without an associated date or time.
 */
public class Todo extends Task {
    /**
     * Creates an incomplete to-do task.
     *
     * @param description Description of the task.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns this to-do's type, completion status, description, and optional note.
     *
     * @return Display representation of this to-do.
     */
    @Override
    public String toString() {
        return formatWithNote("[T]" + getTaskDetails());
    }
}
