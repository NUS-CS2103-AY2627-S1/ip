
package ip.model;

/** Represents a task with a description and completion status. */
public class Task {

    protected String description;
    protected boolean isDone;

    /** Creates an incomplete task with the given description. */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /** Marks this task as completed. */
    public void markAsDone() {
        this.isDone = true;
    }

    /** Marks this task as incomplete. */
    public void markAsNotDone() {
        this.isDone = false;
    }

    /** Returns the task description. */
    public String getDescription() {
        return description;
    }

    /** Returns the display icon for this task's completion status. */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /** Returns the serialized representation used by persistent storage. */
    public String toFileFormat() {
        return (isDone ? "1" : "0") + " | " + description;
    }

    /** Returns the user-facing representation of this task. */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
