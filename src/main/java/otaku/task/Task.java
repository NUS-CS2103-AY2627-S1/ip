package otaku.task;

/** Represents a task and whether it has been completed. */
public abstract class Task {
    private final String description;
    private boolean isDone;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description description of the task
     */
    protected Task(String description) {
        assert description != null : "A task description must be provided by the command parser";
        assert !description.isBlank() : "A task description must be validated before construction";

        this.description = description;
        this.isDone = false;
    }

    /** Returns the icon that represents this task's completion status. */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /** Returns whether this task has been completed. */
    public boolean isDone() {
        return isDone;
    }

    /** Returns this task's description. */
    public String getDescription() {
        return description;
    }

    /** Returns whether this task's description contains the given keyword. */
    public boolean containsKeyword(String keyword) {
        return description.contains(keyword);
    }

    /** Marks this task as completed. */
    public void markAsDone() {
        isDone = true;
    }

    /** Marks this task as incomplete. */
    public void unmarkAsDone() {
        isDone = false;
    }

    /** Returns the letter that identifies this task type. */
    protected abstract String getTypeIcon();

    /** Returns this task's description, type, and completion status. */
    @Override
    public String toString() {
        return "[" + getTypeIcon() + "][" + getStatusIcon() + "] " + description;
    }
}
