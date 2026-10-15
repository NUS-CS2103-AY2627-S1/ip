package sevensix;

/**
 * Represents a task stored by SevenSix.
 */
public class Task {
    /** The text that describes the task. */
    private final String description;
    /** Whether the task has been completed. */
    private boolean isDone;
    /** The task type used for persistence and display. */
    private final TaskType taskType;

    /**
     * Creates a task that is initially not done.
     *
     * @param description the text describing the task.
     */
    public Task(String description) {
        this(description, TaskType.TODO);
    }

    /**
     * Creates a task with the specified type that is initially not done.
     *
     * @param description the text describing the task.
     * @param taskType the type of this task.
     */
    public Task(String description, TaskType taskType) {
        assert description != null && !description.isBlank()
                : "Command parsing must reject an empty description before a task is created";
        assert taskType != null : "Every task must carry a type so it can be displayed and saved";
        this.description = description;
        this.isDone = false;
        this.taskType = taskType;
    }

    /**
     * Returns the marker used to show whether this task is done.
     *
     * @return {@code X} for a done task, or a space otherwise.
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Returns the letter that identifies this task's type in the user interface.
     *
     * @return the icon associated with this task's type.
     */
    public String getTaskTypeIcon() {
        return taskType.getIcon();
    }

    /**
     * Returns the task description for persistence.
     *
     * @return the task description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns whether this task has been marked as done.
     *
     * @return {@code true} when this task is done.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Marks this task as done.
     */
    public void markAsDone() {
        isDone = true;
    }

    /**
     * Marks this task as not done.
     */
    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Returns the task in the format used by list and status messages.
     *
     * @return the status marker and task description.
     */
    @Override
    public String toString() {
        return "[" + getTaskTypeIcon() + "][" + getStatusIcon() + "] " + description;
    }
}
