package yokohama.task;

/**
 * Represents a task without a deadline or scheduled time.
 */
public class Task extends Todo {
    /**
     * Creates a task with the specified description and completion state.
     *
     * @param description Description of the task.
     * @param isDone Whether the task is complete.
     */
    public Task(String description, boolean isDone) {
        super(description, isDone);
    }

    @Override
    public String toDbString() {
        return String.format("T | %s | %s\n", getStorageCompletionValue(), getDescription());
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
