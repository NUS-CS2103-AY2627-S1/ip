package kdb;

/** Represents a basic task with a description and completion status. */
public class Task {
    private final String description;
    private boolean isDone;
    private Priority priority;

    /**
     * Creates an unfinished task with the given description.
     *
     * @param description text that describes the task
     */
    public Task(String description) {
        this(description, Priority.MEDIUM);
    }

    /** Creates a task with the given description and priority. */
    public Task(String description, Priority priority) {
        this.description = description;
        this.isDone = false;
        this.priority = priority;
    }

    /** Returns the task description. */
    public String getDescription() {
        return description;
    }

    /** Returns the display icon for this task's completion status. */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Returns whether this task has been marked as done.
     *
     * @return true if the task is done
     */
    public boolean isDone() {
        return isDone;
    }

    /** Returns this task's priority. */
    public Priority getPriority() {
        return priority;
    }

    /** Changes this task's priority. */
    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    /**
     * Converts this task to the format used in the task data file.
     *
     * @return a pipe-separated representation of this task
     */
    public String toFileFormat() {
        return "T | " + (isDone ? "1" : "0") + " | " + description;
    }

    /** Marks this task as completed. */
    public void markAsDone() {
        isDone = true;
    }

    /** Marks this task as incomplete. */
    public void markAsNotDone() {
        isDone = false;
    }

    /** Returns a human-readable representation of this task. */
    @Override
    public String toString() {
        return "[" + priority + "][" + getStatusIcon() + "] " + description;
    }
}
