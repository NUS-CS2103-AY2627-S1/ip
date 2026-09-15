package tianyi.task;

/**
 * Represents a task with a description and completion status.
 */
public class Task {
    /**
     * Storage marker for a completed task.
     */
    public static final String DATA_STATUS_DONE = "1";
    /**
     * Storage marker for an incomplete task.
     */
    public static final String DATA_STATUS_NOT_DONE = "0";

    /**
     * Separator between fields in a serialized task.
     */
    protected static final String DATA_SEPARATOR = " | ";

    private final String description;
    private boolean isDone;

    /**
     * Creates an incomplete task with the specified description.
     *
     * @param description Description of the task.
     */
    public Task(String description) {
        this.description = description;
        isDone = false;
    }

    /**
     * Reports whether this task is complete.
     *
     * @return Completion status.
     */
    public boolean isDone() {
        return isDone;
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
     * Reports whether this task occurs on the specified date.
     * Plain tasks have no date and therefore never occur on a specific date.
     *
     * @param taskTime Date to check.
     * @return Always {@code false} for a plain task.
     */
    public boolean isOccurringOn(TaskTime taskTime) {
        return false;
    }

    /**
     * Reports whether this task's description contains the specified keyword.
     *
     * @param keyword Keyword to find in the description.
     * @return {@code true} if the description contains the keyword, otherwise {@code false}.
     */
    public boolean matchesKeyword(String keyword) {
        return description.contains(keyword);
    }

    /**
     * Serializes the task's completion status and description.
     *
     * @return Storage representation of this task's common fields.
     */
    public String getData() {
        String status = isDone ? DATA_STATUS_DONE : DATA_STATUS_NOT_DONE;
        return status + DATA_SEPARATOR + description;
    }

    /**
     * Formats the task's completion status and description for display.
     *
     * @return User-facing representation of the task.
     */
    @Override
    public String toString() {
        String statusIcon = isDone ? "[X]" : "[ ]";
        return statusIcon + " " + description;
    }
}
