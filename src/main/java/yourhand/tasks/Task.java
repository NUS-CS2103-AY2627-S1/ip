package yourhand.tasks;

/**
 * Represents a task and whether it has been completed.
 */
public abstract class Task {
    private final String description;
    private final TaskType taskType;
    private boolean isDone;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description Text describing the task.
     * @param taskType Category of the task.
     */
    protected Task(String description, TaskType taskType) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Task description must not be empty.");
        }
        if (description.indexOf('|') >= 0 || description.chars().anyMatch(Character::isISOControl)) {
            throw new IllegalArgumentException("Task description contains an unsupported character.");
        }
        if (taskType == null) {
            throw new IllegalArgumentException("Task type must be provided.");
        }
        this.description = description;
        this.taskType = taskType;
        this.isDone = false;
    }

    /**
     * Returns the symbol used to show this task's completion status.
     *
     * @return {@code X} when done, otherwise a space.
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Marks this task as done.
     *
     * @return True if the task changed from not done to done.
     */
    public boolean markAsDone() {
        if (isDone) {
            return false;
        }
        isDone = true;
        return true;
    }

    /**
     * Marks this task as not done.
     *
     * @return True if the task changed from done to not done.
     */
    public boolean markAsUndone() {
        if (!isDone) {
            return false;
        }
        isDone = false;
        return true;
    }

    /**
     * Returns this task's description.
     *
     * @return The task description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns this task in the format used by the data file.
     *
     * @return A single line that represents this task.
     */
    public final String toFileString() {
        String status = isDone ? "1" : "0";
        return taskType.getIcon() + " | " + status + " | " + description + getFileDetails();
    }

    /** Returns additional task-type-specific fields for the data file. */
    protected String getFileDetails() {
        return "";
    }

    /** Returns the task's display text, including its completion status. */
    @Override
    public String toString() {
        return "[" + taskType.getIcon() + "][" + getStatusIcon() + "] " + description;
    }
}
