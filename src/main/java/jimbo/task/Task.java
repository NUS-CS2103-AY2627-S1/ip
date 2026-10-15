package jimbo.task;

/**
 * Represents a task with a description and a done/not-done status.
 * This is an abstract base class: concrete task types (e.g. Todo, Deadline,
 * Event) extend it and specify their own type icon and any extra details.
 */
public abstract class Task {
    protected String description;
    protected DoneStatus status;

    /**
     * Creates a task with the given description, initially not done.
     */
    public Task(String description) {
        assert description != null && !description.isEmpty()
                : "Task description should never be null or empty; Parser is expected to validate this "
                + "before constructing a Task";
        this.description = description;
        this.status = DoneStatus.NOT_DONE;
    }

    /**
     * Returns this task's description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Updates this task's description, e.g. in response to an "update"
     * command. Leaves every other field (status, and any type-specific
     * fields on a subclass) unchanged.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns the single-character icon shown next to a task to indicate
     * whether it is done ("X") or not done (" ").
     */
    public String getStatusIcon() {
        return (status == DoneStatus.DONE ? "X" : " "); // mark done task with X
    }

    /**
     * Returns the single-letter icon identifying this task's type,
     * e.g. "T" for Todo, "D" for Deadline, "E" for Event.
     */
    public abstract String getTypeIcon();

    /**
     * Returns this task's representation as a single line in the save file,
     * e.g. {@code "T | 1 | read book"}. Each concrete subclass is
     * responsible for including its own type-specific fields (such as a
     * deadline's "by" date), so that adding a new task type in future does
     * not require changes to any shared save/load logic.
     */
    public abstract String toSaveFormat();

    /**
     * Marks this task as done.
     */
    public void markAsDone() {
        status = DoneStatus.DONE;
    }

    /**
     * Marks this task as not done.
     */
    public void markAsNotDone() {
        status = DoneStatus.NOT_DONE;
    }

    @Override
    public String toString() {
        return "[" + getTypeIcon() + "][" + getStatusIcon() + "] " + description;
    }
}
