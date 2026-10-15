package tungtung;

/** Represents a task that can be marked as done or undone. */
public class Task {
    protected String description;
    protected boolean isDone;

    /** Creates an incomplete task with the supplied description. */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    public String getStatusIcon() {
        return (this.isDone ? "X" : " "); // mark done task with X
    }

    /** Marks this task as completed. */
    public void setDone() {
        this.isDone = true;
    }

    /** Marks this task as incomplete. */
    public void setUndone() {
        this.isDone = false;
    }

    @Override
    public String toString() {
        return "[" + this.getStatusIcon() + "] " + this.description;
    }
}
