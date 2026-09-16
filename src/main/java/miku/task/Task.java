package miku.task;

/** Represents the common information and behaviour shared by all tasks. */
public abstract class Task {
    private final TaskType type;
    private final String description;
    private boolean isDone;

    /** Creates an undone task with the given type and description. */
    protected Task(TaskType type, String description) {
        assert type != null : "Tasks must have a type.";
        assert description != null : "Tasks must have a description.";
        this.type = type;
        this.description = description;
        isDone = false;
    }

    /** Returns the symbol used to show whether this task is complete. */
    public String getStatusIcon() {
        return isDone ? "★" : " ";
    }

    /** Marks this task as done. */
    public void markAsDone() {
        isDone = true;
    }

    /** Marks this task as not done. */
    public void markAsNotDone() {
        isDone = false;
    }

    /** Returns this task's description. */
    public String getDescription() {
        return description;
    }

    /** Returns this task's type marker. */
    public String getType() {
        return type.getMarker();
    }

    /** Returns whether this task has been completed. */
    public boolean isDone() {
        return isDone;
    }

    /** Returns this task in Miku's task-list display format. */
    @Override
    public String toString() {
        return "[" + type.getMarker() + "][" + getStatusIcon() + "] " + description;
    }
}
