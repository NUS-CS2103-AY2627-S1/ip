package bob.task;

/**
 * Represents a task that has no associated date or time.
 */
public class Todo extends Task {
    /**
     * Creates an incomplete to-do task.
     *
     * @param description description of the task
     */
    public Todo(String description) {
        this(description, Priority.NONE);
    }

    /**
     * Creates an incomplete task with an explicit priority.
     *
     * @param description description of the task
     * @param priority priority tag, or NONE for no tag
     */
    public Todo(String description, Priority priority) {
        super(description, priority);
    }

    /**
     * Serializes this to-do with its task type.
     *
     * @return one to-do record suitable for storage
     */
    @Override
    public String toStorageString() {
        return "T | " + super.toStorageString() + getStoragePriority();
    }

    /**
     * Formats this to-do for display with its task type.
     *
     * @return display form of this to-do
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
