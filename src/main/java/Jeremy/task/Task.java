package jeremy.task;

/**
 * Base class for all task types. Todo, Deadline, and Event
 * inherit from this and override getTypeIcon() (and toString()
 * where extra details need to be shown).
 */
public abstract class Task {
    protected String description;
    protected boolean isDone;

    public Task(String description) {
        assert description != null && !description.isBlank() : "Task description must be provided";
        this.description = description;
        this.isDone = false;
    }

    public void markAsDone() {
        this.isDone = true;
    }

    public void markAsNotDone() {
        this.isDone = false;
    }

    public boolean isDone() {
        return isDone;
    }

    public String getDescription() {
        return description;
    }

    protected String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /** One-letter code identifying the task type: T / D / E. */
    protected abstract String getTypeIcon();

    @Override
    public String toString() {
        return "[" + getTypeIcon() + "][" + getStatusIcon() + "] " + description;
    }
}
