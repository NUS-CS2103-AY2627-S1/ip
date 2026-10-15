package bingusdingus.task;

/** Represents a task in the task list. */
public class Task {
    private static final String TASK_STATUS_INCOMPLETE = "0";
    private static final String TASK_STATUS_COMPLETE = "1";

    private final String description;
    private boolean isDone;

    /** Creates an incomplete task. */
    public Task(String description) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Task description cannot be blank");
        }
        if (description.contains("|") || description.contains("\n") || description.contains("\r")) {
            throw new IllegalArgumentException("Task description contains an unsupported character");
        }
        this.description = description;
        this.isDone = false;
    }

    /** Marks the task as done. */
    public void markAsDone() {
        isDone = true;
    }

    /** Marks the task as not done. */
    public void markAsNotDone() {
        isDone = false;
    }

    /** Returns whether this task has been completed. */
    public boolean isDone() {
        return this.isDone;
    }

    /** Returns the task's display status icon. */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /** Returns the task description. */
    public String getDescription() {
        return description;
    }

    /** Returns whether this task has the same type and details as the supplied task. */
    public boolean hasSameDetailsAs(Task other) {
        return other != null
                && getClass() == other.getClass()
                && description.equals(other.description);
    }

    /**
     * Returns the task in the format used by the task storage file.
     *
     * @return a pipe-delimited representation of this task.
     */
    public String toFileFormat() {
        return getBaseFileFormat("T");
    }

    /** Returns the shared storage prefix for this task and the supplied task type. */
    protected String getBaseFileFormat(String taskType) {
        String status = isDone ? TASK_STATUS_COMPLETE : TASK_STATUS_INCOMPLETE;
        return taskType + " | " + status + " | " + description;
    }
}
