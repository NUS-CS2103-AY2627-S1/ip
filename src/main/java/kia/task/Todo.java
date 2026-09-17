package kia.task;

/**
 * A task without an associated date or time.
 */
public class Todo extends Task {
    /**
     * Creates an incomplete to-do task.
     *
     * @param description the task description
     */
    public Todo(String description) {
        super(description, TaskType.TODO);
    }

    /**
     * Formats this to-do with its type, completion state, and description.
     *
     * @return the user-facing to-do representation
     */
    @Override
    public String toString() {
        return "[" + getTypeIcon() + "]" + super.toString();
    }

    /**
     * Serializes this to-do using the base task persistence format.
     *
     * @return a pipe-delimited to-do record
     */
    @Override
    public String toStorageString() {
        return super.toStorageString();
    }
}
