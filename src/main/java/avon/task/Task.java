package avon.task;

/**
 * Represents a task and whether it has been completed.
 */
public abstract class Task {
    private final String description;
    private boolean isDone;

    /**
     * Creates an unfinished task with the given description.
     *
     * @param description the text describing the task.
     * @throws IllegalArgumentException if the description is null or blank.
     */
    public Task(String description) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("A task description cannot be blank.");
        }
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the status icon used when displaying this task.
     *
     * @return {@code X} for a completed task, or a space otherwise.
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Marks this task as completed.
     */
    public void markAsDone() {
        isDone = true;
    }

    /**
     * Marks this task as not completed.
     */
    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Returns the task description for persistence and searching.
     *
     * @return the text describing this task.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns whether this task has been completed.
     *
     * @return {@code true} if the task is completed.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns whether another task has the same type and task-specific details.
     * Completion status is intentionally ignored.
     *
     * @param other the task to compare with this task.
     * @return {@code true} if both tasks have the same type and details.
     */
    public boolean hasSameDetailsAs(Task other) {
        return other != null
                && getClass().equals(other.getClass())
                && description.equals(other.description);
    }

    /**
     * Returns this task in the format used by Avon’s task list.
     *
     * @return the status icon and description of this task.
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + getDescription();
    }
}
