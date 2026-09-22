package genie.model;

/** Represents a generic task with a description and completion status. */
public class Task {
    protected String description;
    protected boolean isDone;

    /**
     * Initializes a new Task.
     *
     * @param description Description of the task.
     */
    public Task(String description) {
        assert description != null : "Task description must not be null";
        this.description = description;
        this.isDone = false;
    }

    /** Marks the task as completed. */
    public void markAsDone() {
        this.isDone = true;
    }

    /** Marks the task as incomplete. */
    public void markAsUndone() {
        this.isDone = false;
    }

    /** Returns whether the task is completed. */
    public boolean isDone() {
        return isDone;
    }

    /** Returns the icon representing the task's completion status. */
    public String getStatusIcon() {
        return (isDone ? "[X]" : "[ ]");
    }

    /** Returns the task description. */
    public String getName() {
        return this.description;
    }

    /** Updates the task description. */
    public void setDescription(String description) {
        assert description != null : "Task description must not be null";
        this.description = description;
    }

    /**
     * Returns the string representation of the task for saving to a file.
     *
     * @return Formatted string for file storage.
     */
    public String toFileFormat() {
        return (isDone ? "1" : "0") + " | " + description;
    }

    /** Returns the task description with its completion status. */
    @Override
    public String toString() {
        return getStatusIcon() + " " + description;
    }
}
