package todd;

/** Represents a task with a description and completion status. */
public class Task {
    protected String description;
    protected boolean isDone;

    /** Creates an incomplete task with the specified description. */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /** Returns the icon used to display the task's completion status. */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /** Returns the task description. */
    public String getDescription() {
        return description;
    }

    /** Marks this task as completed. */
    public void markAsDone() {
        isDone = true;
    }

    /** Marks this task as incomplete. */
    public void markAsUndone() {
        isDone = false;
    }

    /** Returns whether this task has been completed. */
    public boolean isDone() {
        return isDone;
    }
}
