package edith.task;

/**
 * Represents a task without an associated date or time.
 */
public class ToDo extends Task {
    /**
     * Creates a to-do task with the given description.
     *
     * @param description the task description
     */
    public ToDo(String description) {
        super(description);
    }

    /**
     * Returns this task with the to-do type icon.
     *
     * @return the type, status, and description of this task
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
