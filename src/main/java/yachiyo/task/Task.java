package yachiyo.task;

import java.time.LocalDate;

/**
 * Represents a task with a description and completion status.
 */
public abstract class Task {
    private final String description;
    private boolean isCompleted;

    /**
     * Creates an incomplete task with the specified description.
     *
     * @param description Description of the task.
     */
    public Task(String description) {
        assert isValidDescription(description)
                : "Task description must be non-blank and exclude reserved characters";

        this.description = description;
        this.isCompleted = false;
    }

    /**
     * Checks whether a description is non-blank and safe for the task storage format.
     *
     * @param description description to validate.
     * @return true if the description can be used by a task.
     */
    public static boolean isValidDescription(String description) {
        return description != null
                && !description.isBlank()
                && !description.contains("|");
    }

    /**
     * Returns the description of this task.
     *
     * @return Task description.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Returns whether this task is completed.
     *
     * @return True if this task is completed; false otherwise.
     */
    public boolean isCompleted() {
        return this.isCompleted;
    }

    /**
     * Returns a copy of this task with the specified description.
     * The task type, completion status, and type-specific details are preserved.
     *
     * @param description replacement description.
     * @return task containing the replacement description.
     */
    public abstract Task withDescription(String description);

    /**
     * Returns a copy of this task with the specified completion status.
     * The task type, description, and type-specific details are preserved.
     *
     * @param isCompleted replacement completion status.
     * @return task containing the replacement completion status.
     */
    public Task withCompletionStatus(boolean isCompleted) {
        Task copiedTask = withDescription(description);
        if (isCompleted) {
            copiedTask.markAsDone();
        } else {
            copiedTask.markAsNotDone();
        }
        return copiedTask;
    }

    /**
     * Copies this task's completion status to another task.
     *
     * @param <T> type of task receiving the completion status.
     * @param task task that should receive this task's completion status.
     * @return supplied task with the completion status copied.
     */
    protected final <T extends Task> T copyCompletionStatusTo(T task) {
        assert task != null : "Target task must not be null";

        if (isCompleted) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Marks this task as completed.
     */
    public void markAsDone() {
        this.isCompleted = true;
    }

    /**
     * Marks this task as incomplete.
     */
    public void markAsNotDone() {
        this.isCompleted = false;
    }

    /**
     * Returns the icon representing this task's completion status.
     *
     * @return {@code X} if completed; a space otherwise.
     */
    public String getStatusIcon() {
        return isCompleted ? "X" : " ";
    }

    /**
     * Returns the task fields shared by all task types in the storage file format.
     *
     * @return completion status and description separated by delimiters.
     */
    public String toFileFormat() {
        return String.format("%d | %s", isCompleted ? 1 : 0, description);
    }

    /**
     * Checks whether this task occurs on the specified date.
     * Tasks without a date do not occur on any particular date by default.
     *
     * @param date date to check.
     * @return true if this task occurs on the date, otherwise false.
     */
    public boolean occursOn(LocalDate date) {
        return false;
    }

    /**
     * Returns this task in its user-facing display format.
     *
     * @return Formatted task description and completion status.
     */
    @Override
    public String toString() {
        return String.format("[%s] %s", this.getStatusIcon(), description);
    }
}
