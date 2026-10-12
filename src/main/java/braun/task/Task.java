package braun.task;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents a task with a description and a completion status.
 * Provides methods to retrieve status details and toggle completion.
 */
public class Task {

    /** The description or title of the task */
    protected String description;

    /** The completion status of the task */
    protected boolean isDone;

    /**
     * Constructs a new {@code Task} with the specified description.
     * The task is initially marked as not done.
     *
     * @param description the textual description of the task.
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the status icon indicating whether the task is completed.
     *
     * @return "X" if the task is done, otherwise a single space " ".
     */
    public String getStatusIcon() {
        return (isDone ? "X" : " "); // mark done task with X
    }

    /**
     * Marks the task as completed.
     */
    public void markAsDone() {
        this.isDone = true;
    }

    /**
     * Marks the task as not completed (undone).
     */
    public void markAsUndone() {
        this.isDone = false;
    }

    /**
     * Returns the description of the task.
     *
     * @return the task description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns whether the task is completed.
     *
     * @return {@code true} if done, {@code false} otherwise.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns the formatted string representation of the task including its status icon.
     *
     * @return a formatted string such as "[X] read book" or "[ ] return book".
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }

    /**
     * Returns the formatted string representation of the task for file storage.
     *
     * @return a pipe-delimited string representing completion status and description.
     */
    public String toFileFormat() {
        return (isDone ? "1" : "0") + " | " + description;
    }

    /**
     * Checks whether this task occurs on or is scheduled for the given date.
     * Default implementation returns false.
     *
     * @param queryDate the date to check against.
     * @return {@code true} if the task occurs on the specified date, {@code false} otherwise.
     */
    public boolean isOnDate(LocalDate queryDate) {
        return false;
    }

    /**
     * Compares this task with another object for equality.
     * Two tasks are equal if they have the same class type and identical description.
     *
     * @param other the object to compare against.
     * @return {@code true} if both tasks are equivalent; {@code false} otherwise.
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        Task task = (Task) other;
        return description.equalsIgnoreCase(task.description);
    }

    /**
     * Returns a hash code based on the task description.
     *
     * @return integer hash code.
     */
    @Override
    public int hashCode() {
        return Objects.hash(description.toLowerCase());
    }
}
