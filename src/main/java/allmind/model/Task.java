package allmind.model;

/** Represents a task entered by the user and whether it has been completed. */
public class Task {
    /** The text describing this task. */
    protected String description;
    /** Whether this task has been marked as done. */
    protected boolean isDone;

    /** Creates a new unfinished task. */
    public Task(String description) {
        assert description != null : "A task must have a description";
        this.description = description;
        this.isDone = false;
    }
    @Override
    public String toString() {
        return "[" + this.getStatusIcon() + "] "
            + this.getDescription();
    }

    /** Marks this task as completed. */
    public void markAsDone() {
        isDone = true;
    }

    /** Marks this task as incomplete. */
    public void markAsNotDone() {
        isDone = false;
    }

    /** Returns the display icon for this task's completion status. */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /** Returns this task's description. */
    public String getDescription() {
        return description;
    }

    /** Returns whether this task has been completed. */
    public boolean isDone() {
        return isDone;
    }

}
