package tem;

/**
 * Represents one task in Tem's task list and whether it has been completed.
 */
public abstract class Task {
    private final String description;
    private final TaskType taskType;
    private boolean isDone;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description description supplied by the user.
     * @param taskType fixed type of this task.
     */
    public Task(String description, TaskType taskType) {
        assert description != null : "Task description should not be null";
        assert taskType != null : "Task type should not be null";
        this.description = description;
        this.taskType = taskType;
        this.isDone = false;
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
    public void unmarkAsDone() {
        isDone = false;
    }

    /**
     * Returns whether this task has been marked as done.
     *
     * @return {@code true} if the task is completed
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns the task description entered by the user.
     *
     * @return task description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns whether this task represents the same work as another task.
     *
     * Completion status is deliberately excluded so that a completed task is
     * not added again by mistake.
     *
     * @param other task to compare with this task.
     * @return whether both tasks have the same type, description, and schedule details
     */
    public boolean hasSameDetails(Task other) {
        return other != null
                && taskType == other.taskType
                && description.equalsIgnoreCase(other.description)
                && getScheduleDetails().equals(other.getScheduleDetails());
    }

    /**
     * Returns task-specific date or time details used when comparing tasks.
     *
     * @return details that distinguish tasks of the same type and description
     */
    protected String getScheduleDetails() {
        return "";
    }

    /**
     * Returns the fixed type of this task.
     *
     * @return task type
     */
    public TaskType getTaskType() {
        return taskType;
    }

    /**
     * Returns the symbol used when displaying this task's completion status.
     *
     * @return {@code X} for a completed task, or a space for an incomplete task
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Returns one line used when saving this task to disk.
     *
     * @return storage line for this task
     */
    public abstract String toStorageString();

    /**
     * Escapes characters that would break the save-file field format.
     *
     * @param field raw field text.
     * @return escaped text safe to write as one field
     */
    protected static String escapeStorageField(String field) {
        StringBuilder escaped = new StringBuilder();
        for (char character : field.toCharArray()) {
            switch (character) {
                case '\\':
                    escaped.append("\\\\");
                    break;
                case '|':
                    escaped.append("\\|");
                    break;
                case '\n':
                    escaped.append("\\n");
                    break;
                case '\r':
                    escaped.append("\\r");
                    break;
                default:
                    escaped.append(character);
            }
        }
        return escaped.toString();
    }

    /**
     * Returns this task in the format shown to the user.
     *
     * @return task type, completion status, and description
     */
    @Override
    public String toString() {
        return "[" + taskType.getDisplaySymbol() + "][" + getStatusIcon() + "] " + description;
    }
}
