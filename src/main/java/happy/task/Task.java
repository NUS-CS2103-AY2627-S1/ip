package happy.task;

import java.time.LocalDate;

/**
 * Represents a generic task with a description and completion status.
 */
public class Task {
    protected String description;
    protected boolean isDone;
    protected Priority priority = Priority.NONE;

    /**
     * Constructs a new Task with the given description.
     *
     * @param description The task description.
     */
    public Task(String description) {
        assert description != null : "Task description should not be null";
        this.description = description;
        isDone = false;
    }

    /**
     * Returns the priority level of the task.
     *
     * @return Priority enum value.
     */
    public Priority getPriority() {
        return priority;
    }

    /**
     * Sets the priority level of the task.
     *
     * @param priority New Priority enum value.
     */
    public void setPriority(Priority priority) {
        assert priority != null : "Priority cannot be null";
        this.priority = priority;
    }

    /**
     * Returns the status icon of the task. "X" indicates done, " " indicates not done.
     *
     * @return Status icon string.
     */
    public String getStatusIcon() {
        return (isDone ? "X" : " ");
    }

    /**
     * Marks the task as completed.
     */
    public void markAsDone() {
        isDone = true;
    }

    /**
     * Marks the task as incomplete.
     */
    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Returns the description of the task.
     *
     * @return Task description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Checks if the task occurs or is due on a specific target date.
     * Base implementation returns false.
     *
     * @param date Target date to check against.
     * @return true if task occurs on target date, false otherwise.
     */
    public boolean isOccurringOn(LocalDate date) {
        return false;
    }

    /**
     * Converts the task object into a file storage string format.
     *
     * @return Formatted string representation for file storage.
     */
    public String toFileFormat() {
        String pStr = priority == Priority.NONE ? "" : " | " + priority;
        return (isDone ? "1" : "0") + pStr + " | " + description;
    }

    @Override
    public String toString() {
        String pStr = priority == Priority.NONE ? "" : " [" + priority + "]";
        return "[" + getStatusIcon() + "]" + pStr + " " + description;
    }

    /**
     * Compares this task with another object for equality based on description.
     *
     * @param obj Target object to compare with.
     * @return true if target object is a Task with matching description (case-insensitive).
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Task other = (Task) obj;
        return this.description.equalsIgnoreCase(other.description);
    }
}
