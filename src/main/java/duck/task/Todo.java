package duck.task;

/**
 * Represents a task without any date or time attached to it.
 */
public class Todo extends Task {
    /**
     * Creates a todo task with the given description.
     *
     * @param description Description of the task.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns whether another task is a todo with the same description.
     *
     * @param other Task to compare with this todo.
     * @return true if both todos have the same description
     */
    @Override
    public boolean hasSameDetails(Task other) {
        return other instanceof Todo && this.description.equals(other.description);
    }

    /**
     * Returns this todo in the user-visible task format.
     *
     * @return formatted todo with its type and completion status
     */
    @Override
    public String toString() {
        return TaskType.TODO.getTag() + super.toString();
    }

    /**
     * Formats this todo in escaped form for storage.
     *
     * @return storage record containing the type, status, and description
     */
    @Override
    public String formatForStorage() {
        return TaskType.TODO.getFileCode() + " | " + this.getFileStatus() + " | "
                + this.escapeFileField(this.description);
    }
}
