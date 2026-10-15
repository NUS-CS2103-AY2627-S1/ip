package lenzabot.task;

/**
 * Represents a task with a description, category, and completion status.
 */
public abstract class Task {
    /** Separator between fields in the save file format. */
    public static final String SAVE_FILE_SEPARATOR = " | ";

    /** Description of what this task involves. */
    protected final String description;

    /** Category used to identify this task. */
    protected final TaskType taskType;

    /** Whether this task has been completed. */
    protected boolean isCompleted = false;

    /**
     * Creates a task with a description and a fixed task category.
     *
     * @param description Description of what the task involves.
     * @param taskType Category used to identify the task.
     */
    public Task(String description, TaskType taskType) {
        this.description = description;
        this.taskType = taskType;
    }

    /**
     * Returns the task description.
     *
     * @return Description of what this task involves.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Returns whether the task is completed.
     *
     * @return True if this task is completed, otherwise false.
     */
    public boolean isCompleted() {
        return this.isCompleted;
    }

    /**
     * Marks this task as completed.
     */
    public void markAsCompleted() {
        this.isCompleted = true;
    }

    /**
     * Marks this task as incomplete.
     */
    public void markAsIncomplete() {
        this.isCompleted = false;
    }

    /**
     * Returns the completion-status icon displayed for this task.
     *
     * @return `X` when completed, or a space when incomplete.
     */
    public String getStatusIcon() {
        return this.isCompleted ? "X" : " ";
    }

    /**
     * Returns the icon identifying this task's category.
     *
     * @return Single-character task category icon.
     */
    public String getTaskIcon() {
        return this.taskType.getIcon();
    }

    @Override
    public String toString() {
        return String.format("[%s][%s] %s", getTaskIcon(), getStatusIcon(), this.description);
    }

    /**
     * Returns the one-line format used by Storage to persist this task,
     * e.g. "T | 0 | read book" for an incomplete todo.
     *
     * @return Serialized form of this task.
     */
    public String toSaveFormat() {
        return String.join(
                SAVE_FILE_SEPARATOR,
                getTaskIcon(),
                isCompleted() ? "1" : "0",
                this.description
        );
    }
}
