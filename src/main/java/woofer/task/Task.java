package woofer.task;

/**
 * Represents one task in Woofer's in-memory task list.
 */
public abstract class Task {
    private final String description;
    private final TaskType type;
    private boolean isDone;

    /**
     * Creates a new task that is initially not done.
     *
    * @param description text describing the task.
     * @param type type of task being created.
     */
    protected Task(String description, TaskType type) {
        assert description != null && !description.isBlank()
                : "Task description must not be blank";
        assert type != null : "Task type must not be null";
        this.description = description;
        this.type = type;
        this.isDone = false;
    }

    /**
     * Returns the task's status icon.
     *
     * @return {@code X} when done, or a blank space otherwise.
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Returns the one-letter icon for this task type.
     *
     * @return the task type icon.
     */
    public String getTypeIcon() {
        return type.getIcon();
    }

    /**
     * Returns the task description.
     *
     * @return the task description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns whether this task has been marked as done.
     *
     * @return {@code true} when the task is done, or {@code false} otherwise.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns additional information displayed after the description.
     *
     * @return task-specific date/time information.
     */
    protected String getDateDetails() {
        return "";
    }

    /**
     * Formats this task for the task list.
     *
    * @return the task type, completion status, description, and date details.
     */
    public String getDisplayText() {
        String dateDetails = getDateDetails();
        assert dateDetails != null : "Task date details must not be null";
        return "[" + getTypeIcon() + "][" + getStatusIcon() + "] "
                + description + dateDetails;
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
}
