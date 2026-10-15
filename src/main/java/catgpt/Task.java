package catgpt;

/**
 * Represents the common state and behavior of a CatGPT task.
 */
public abstract class Task {
    private final String description;
    private boolean isDone;

    /**
     * Creates a task with its description and completion state.
     *
     * @param description Task description.
     * @param isDone Whether the task has been completed.
     */
    protected Task(String description, boolean isDone) {
        this.description = description;
        this.isDone = isDone;
    }

    /**
     * Marks this task as completed.
     */
    public void markAsDone() {
        isDone = true;
    }

    /**
     * Marks this task as incomplete.
     */
    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Returns this task's category.
     *
     * @return The task type.
     */
    public abstract TaskType getType();

    /**
     * Returns whether this task is completed.
     *
     * @return {@code true} if the task is completed.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns this task's description.
     *
     * @return The task description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns task-type-specific details in their storage representation.
     *
     * @return An ISO deadline date or a display suffix.
     */
    public abstract String getStorageDetails();

    /**
     * Formats this task for display to the user.
     *
     * @return The formatted task.
     */
    @Override
    public String toString() {
        String status = isDone ? "X" : " ";
        return "[" + status + "] " + description;
    }
}
