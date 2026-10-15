package jeryl.task;

/**
 * Represents a single task tracked by the chatbot, with a description
 * and a completion status. Subclasses add the type-specific details
 * (e.g. a deadline's due date).
 */
public class Task {
    protected String description;
    protected boolean isDone;
    protected Priority priority;

    /**
     * Creates a not-yet-done task with the given description and no
     * priority set.
     */
    public Task(String description) {
        this(description, Priority.NONE);
    }

    /**
     * Creates a not-yet-done task with the given description and priority.
     */
    public Task(String description, Priority priority) {
        this.description = description;
        this.isDone = false;
        this.priority = priority;
    }

    /**
     * Returns this task's description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns this task's priority tag as shown in the task listing,
     * e.g. "[H]", or an empty string if no priority is set.
     */
    protected String getPriorityTag() {
        return priority.getTag();
    }

    /**
     * Marks this task as done.
     */
    public void markAsDone() {
        isDone = true;
    }

    /**
     * Marks this task as not done.
     */
    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Returns "X" if the task is done, or a blank space otherwise.
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }

    /**
     * Returns this task encoded as a single line for saving to disk, e.g.
     * "T | 1 | read book". Subclasses append their own type-specific fields.
     */
    public String toFileString() {
        return (isDone ? "1" : "0") + " | " + description;
    }

    /**
     * Appends the priority field to a fully-built file line, e.g. turning
     * "T | 0 | read book" into "T | 0 | read book | HIGH". Subclasses call
     * this last, after appending their own type-specific fields, so the
     * priority field is always the final field in the line. Returns the
     * line unchanged if no priority is set, so old save files (with no
     * priority field at all) stay in the same format they were written in.
     */
    protected String withPriority(String line) {
        return priority == Priority.NONE ? line : line + " | " + priority;
    }
}
