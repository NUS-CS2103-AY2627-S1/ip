package duchess.tasks;

/**
 * Represents a task that can be completed.
 */
public abstract class Task {

    private final String description;
    private boolean isDone;

    /**
     * Creates a new task with some description.
     *
     * @param description The description of the task.
     */
    protected Task(String description) {
        assert (description != null) : "Task description must not be null";

        this.description = description;
        this.isDone = false;
    }

    /**
     * Marks this task as complete.
     */
    public void mark() {
        this.isDone = true;
    }

    /**
     * Marks this task as incomplete.
     */
    public void unmark() {
        this.isDone = false;
    }

    /**
     * Returns the description of this task for use by specialised task types.
     *
     * @return the task description
     */
    protected String getDescription() {
        return this.description;
    }

    /**
     * Returns whether this task description contains the supplied keyword,
     * ignoring letter case.
     */
    public boolean containsKeyword(String keyword) {
        assert (keyword != null) : "Search keyword should not be null";
        return description.toLowerCase().contains(keyword.toLowerCase());
    }

    /**
     * Returns the string representation of a task, in
     * user-readable format.
     */
    @Override
    public String toString() {
        return (isDone ? "[X] " : "[ ] ") + description;
    }

    /**
     * Returns the string representation of a task, in
     * storage format.
     */
    public String getStorageFormat() {
        return (isDone ? "1 | " : "0 | ") + description;
    }
}
