package tangent.task;

/** Represents a generic task object with a description and a done status. */
public class Task {
    private static final String DONE_ICON = "X";
    private static final String UNDONE_ICON = " ";
    /** The description of the task. */
    private final String description;
    /** Whether the task is done or not done. */
    private boolean isDone;

    /** Creates a new Task object with a description and a done status initialized to false. */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /** Returns a string based on the done status: "X" if the task is done, " " if the task is not done. */
    public String getStatusIcon() {
        return isDone ? DONE_ICON : UNDONE_ICON;
    }

    /** Returns the task description. */
    public String getDescription() {
        return description;
    }

    /** Returns whether this task is done. */
    public boolean isDone() {
        return isDone;
    }

    /** Marks this task as done. */
    public void markAsDone() {
        this.isDone = true;
    }

    /** Marks this task as undone. */
    public void markAsUndone() {
        this.isDone = false;
    }

    /** Converts a Task object to a String to be displayed to the user. */
    @Override
    public String toString() {
        return "[" + this.getStatusIcon() + "] " + this.description;
    }
}
