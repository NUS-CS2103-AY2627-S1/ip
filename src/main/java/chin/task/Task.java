package chin.task;

/**
 * Represents a to-do item with a description and a done/not-done state.
 * Base class for concrete task types like {@link Todo}, {@link Deadline}, and {@link Event}.
 */
public class Task {
    protected final String description;
    protected boolean isDone;

    /**
     * Creates a new task in the not-done state.
     *
     * @param description human-readable description
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /** Marks this task as done. */
    public void mark() {
        this.isDone = true;
    }

    /** Marks this task as not done. */
    public void unmark() {
        this.isDone = false;
    }

    /** Returns the {@code [X]}/{@code [ ]} status icon. */
    protected String getStatusIcon() {
        return "[" + (isDone ? "X" : " ") + "]";
    }

    @Override
    public String toString() {
        return getStatusIcon() + " " + description;
    }

    /** Returns a pipe-separated line for {@link chin.util.Storage} to persist. */
    public String serialize() {
        return (isDone ? "1" : "0") + " | " + description;
    }
}
