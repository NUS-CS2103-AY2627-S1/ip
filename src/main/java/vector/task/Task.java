package vector.task;

import java.time.LocalDate;

/**
 * Represents a task with a description and a completion status.
 */
public class Task {
    protected String description;
    protected boolean isDone;

    /**
     * Constructs a new Task with the given description.
     *
     * @param description The description of the task.
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Gets the status icon of the task.
     *
     * @return "X" if the task is done, " " otherwise.
     */
    public String getStatusIcon() {
        return (isDone ? "X" : " "); // mark done task with X
    }

    /**
     * Marks the task as done.
     */
    public void markAsDone() {
        this.isDone = true;
    }

    /**
     * Marks the task as not done.
     */
    public void unmarkAsDone() {
        this.isDone = false;
    }

    /**
     * Gets the description of the task.
     *
     * @return The description.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Returns the string representation of the task.
     *
     * @return A string in the format "[statusIcon] description".
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Task task = (Task) obj;
        return description.equals(task.description);
    }

    /**
     * Returns the string representation of the task for file storage.
     *
     * @return A string formatted for file storage.
     */
    public String toFileFormat() {
        return " | " + (isDone ? "1" : "0") + " | " + description;
    }

    /**
     * Checks if the task occurs on a specific date.
     * By default, a task does not occur on any specific date unless overridden.
     *
     * @param date The date to check against.
     * @return true if it occurs on the date, false otherwise.
     */
    public boolean occursOn(LocalDate date) {
        return false;
    }
}
