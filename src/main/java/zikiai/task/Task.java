package zikiai.task;

/**
 * Represents a task and whether it has been completed.
 */
public class Task {
    private boolean isDone;
    private final String description;
    private Priority priority;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description description of the task.
     */
    public Task(String description) {
        assert description != null && !description.isBlank()
                : "Task description must not be blank";
        this.description = description;
        this.isDone = false;
        this.priority = Priority.NONE;
    }

    /**
     * Returns the icon that represents the task's completion status.
     *
     * @return {@code X} when done, or a space when not done.
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
     * Assigns the given priority to this task.
     *
     * @param priority priority to assign.
     */
    public void setPriority(Priority priority) {
        assert priority != null : "Task priority must not be null";
        this.priority = priority;
    }

    /**
     * Returns this task's priority.
     *
     * @return assigned priority.
     */
    public Priority getPriority() {
        return priority;
    }

    /**
     * Returns the task description together with its completion status.
     *
     * @return formatted task, such as {@code [X] read book}.
     */
    public String getDescription() {
        return priority.getDisplayTag() + "[" + getStatusIcon() + "] " + description;
    }

    /**
     * Returns the unformatted text used to describe this task.
     *
     * @return raw task description.
     */
    protected String getDescriptionText() {
        return description;
    }

    /**
     * Returns the compact priority tag used in the data file.
     *
     * @return an empty string for no priority, or a one-letter priority tag.
     */
    protected String getPriorityStorageTag() {
        return priority.getStorageTag();
    }

    /**
     * Returns this task in the format used by the data file.
     * Subclasses override this method to include their task type and details.
     *
     * @return storage representation of this task.
     */
    public String toDataString() {
        return "[?][" + getStatusIcon() + "]" + getPriorityStorageTag()
                + " | " + description;
    }
}
