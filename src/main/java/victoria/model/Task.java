package victoria.model;

/** Base class for a task and its completion status. */
public abstract class Task {
    private final String description;
    private boolean done;

    /** Creates an unfinished task with the given description. */
    protected Task(String description) { this.description = description; }
    /** Marks this task as done. */
    public void markDone() { done = true; }
    /** Marks this task as not done. */
    public void markNotDone() { done = false; }
    /** Returns whether this task has been completed. */
    public boolean isDone() { return done; }
    /** Returns the user-entered description. */
    public String getDescription() { return description; }
    /** Returns the one-letter marker used by the UI. */
    protected abstract String getTypeMarker();
    /** Returns optional timing text used by the UI. */
    protected abstract String getTimingText();
    /** Returns this task in the format used by the user interface. */
    @Override
    public String toString() {
        return getTypeMarker() + (done ? "[X] " : "[ ] ") + description + getTimingText();
    }
}
