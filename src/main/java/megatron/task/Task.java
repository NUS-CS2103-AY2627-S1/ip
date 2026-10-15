package megatron.task;
/**
 * Represents one task in Megatron's in-memory task list.
 */
public class Task {
    protected final String description;
    private boolean isDone;

    /**
     * Creates a task that is initially incomplete.
     *
     * @param description the text describing the task
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Marks this task as complete.
     */
    public void markAsDone() {
        isDone = true;
    }

    /**
     * Marks this task as incomplete.
     */
    public void unmark() {
        isDone = false;
    }

    /**
     * Returns the symbol used to show this task's completion status.
     *
     * @return {@code X} when complete; otherwise a blank space
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Returns this task's description.
     *
     * @return the task description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the task text shown in a task list.
     *
     * @return this task's status and description
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
