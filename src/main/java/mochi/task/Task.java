package mochi.task;

/**
 * Represents a task and whether it has been completed.
 */
public class Task {
    /**
     * Represents the completion status of a task.
     */
    private enum TaskStatus {
        NOT_DONE(" "),
        DONE("X");

        private final String icon;

        TaskStatus(String icon) {
            this.icon = icon;
        }

        public String getIcon() {
            return icon;
        }
    }

    /** Description shown to the user and stored in the data file. */
    private final String description;
    private TaskStatus status;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description Description of the task.
     */
    public Task(String description) {
        assert description != null : "Task description must not be null";
        assert !description.isBlank() : "Task description must not be blank";

        this.description = description;
        this.status = TaskStatus.NOT_DONE;
    }

    /**
     * Returns the icon representing whether this task is complete.
     *
     * @return {@code X} if complete, or a space otherwise.
     */
    public String getStatusIcon() {
        return status.getIcon();
    }

    /**
     * Returns the description of this task.
     *
     * @return The task description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Marks this task as complete.
     */
    public void mark() {
        status = TaskStatus.DONE;
    }

    /**
     * Marks this task as incomplete.
     */
    public void unmark() {
        status = TaskStatus.NOT_DONE;
    }

    /**
     * Converts this task into the line format used in the data file.
     *
     * @return The serialized task.
     */
    public String toDataString() {
        return formatDataString("T");
    }

    /**
     * Formats the common fields shared by all saved task types.
     *
     * @param taskType Single-letter code identifying the task type.
     * @return The task type, completion status, and description.
     */
    protected String formatDataString(String taskType) {
        assert taskType != null
                && (taskType.equals("T") || taskType.equals("D") || taskType.equals("E"))
                : "Task type must use a supported storage code";

        return taskType + " | " + (status == TaskStatus.DONE ? "1" : "0")
                + " | " + description;
    }

    /**
     * Returns this task in its user-facing display format.
     *
     * @return Formatted task description and completion status.
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
