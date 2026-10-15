package kiaw.task;

import java.time.LocalDate;

/**
 * Represents a task tracked by Kiaw.
 *
 * A task contains a description and completion status.
 */
public class Task {
    protected String description;
    protected boolean isDone;

    /**
     * Creates a new incomplete task with the specified description.
     *
     * @param description description of the task
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
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
     * Returns the symbol representing the completion status.
     *
     * @return "X" if completed, otherwise a blank space
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Returns the description of this task.
     *
     * @return task description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the symbol representing this task's type.
     *
     * @return task type symbol
     */
    public String getTypeIcon() {
        return "T";
    }

    /**
     * Returns the details of this task for display.
     *
     * @return formatted task details
     */
    public String getDetails() {
        return description;
    }

    /**
     * Returns the date used to sort this task chronologically.
     *
     * Tasks without an associated date return null.
     *
     * @return date used for sorting, or null if the task has no date
     */
    public LocalDate getSortDate() {
        return null;
    }
}
