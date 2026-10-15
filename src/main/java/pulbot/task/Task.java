package pulbot.task;

/** Represents a task that can be stored and managed by Pulbot. */
public class Task {
    private final String description;
    private final TaskType type;
    private boolean isDone;

    public Task(String description) {
        this(description, TaskType.TODO);
    }

    protected Task(String description, TaskType type) {
        assert description != null && !description.isBlank()
                : "Tasks must have a non-blank description after input validation";
        assert type != null : "Every task must have a type";
        if (description.indexOf('\t') >= 0 || description.indexOf('\n') >= 0 || description.indexOf('\r') >= 0) {
            throw new IllegalArgumentException(
                    "Task descriptions cannot contain tabs or line breaks. Use spaces instead.");
        }
        this.description = description;
        this.isDone = false;
        this.type = type;
    }

    /** Returns the completion icon for this task. */
    public String getStatusIcon() {
        return isDone ? "\u2713" : " ";
    }

    /** Marks this task as completed. */
    public void markAsDone() {
        isDone = true;
    }

    /** Marks this task as incomplete. */
    public void markAsNotDone() {
        isDone = false;
    }

    /** Returns the task description. */
    public String getDescription() {
        return description;
    }

    /** Returns this task's type. */
    public TaskType getType() {
        return type;
    }

    /** Returns whether this task is completed. */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns whether this task and another task contain the same identifying details.
     * Completion status is deliberately ignored because it does not describe the task itself.
     */
    public boolean hasSameDetails(Task other) {
        return other != null
                && type == other.type
                && description.strip().equalsIgnoreCase(other.description.strip())
                && hasSameSchedule(other);
    }

    /** Returns whether this task and another task have the same type-specific schedule. */
    protected boolean hasSameSchedule(Task other) {
        return true;
    }

    /** Returns the user-facing representation of this task. */
    @Override
    public String toString() {
        return "[" + type.getSymbol() + "][" + getStatusIcon() + "] " + description;
    }
}
