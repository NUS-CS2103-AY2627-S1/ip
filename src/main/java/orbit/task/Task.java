package orbit.task;

import java.time.LocalDate;

/**
 * Represents a task with a description and completion status.
 */
public abstract class Task {
    private final String description;
    private final TaskType type;
    private TaskStatus status;

    /**
     * Creates a new task that is initially not completed.
     *
     * @param description description of the task
     * @param type category of the task
     */
    protected Task(String description, TaskType type) {
        assert description != null;
        this.description = description;
        this.type = type;
        status = TaskStatus.NOT_DONE;
    }

    /**
     * Returns an icon that represents the task's completion status.
     *
     * @return {@code "[X]"} if the task is completed, or {@code "[ ]"} otherwise
     */
    public String getStatusIcon() {
        return status.getIcon();
    }

    /**
     * Marks this task as completed and returns its updated display text.
     *
     * @return the task's status icon and description
     */
    public String mark() {
        status = TaskStatus.DONE;

        return toString();
    }

    /**
     * Marks this task as not completed and returns its updated display text.
     *
     * @return the task's status icon and description
     */
    public String unmark() {
        status = TaskStatus.NOT_DONE;

        return toString();
    }

    /**
     * Returns this task's description.
     *
     * @return the task description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Checks whether this task belongs in the schedule for a date.
     *
     * @param date date whose schedule is being viewed
     * @return true if this task occurs on the date
     */
    public boolean occursOn(LocalDate date) {
        return false;
    }

    /**
     * Returns a representation of this task suitable for saving to a file.
     *
     * @return pipe-separated task data
     */
    public String toDataString() {
        return type.getStorageCode() + " | " + status.getStorageValue() + " | " + description;
    }

    /**
     * Returns the task in the format used when displaying it to the user.
     *
     * @return the task's type icon, status icon, and description
     */
    @Override
    public String toString() {
        return type.getIcon() + getStatusIcon() + " " + description;
    }
}
