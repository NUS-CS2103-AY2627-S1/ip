package prism.task;

/** Defines the shared state and behavior of all Prism task types. */
public abstract class Task {
    protected String description;
    protected boolean isDone;
    protected TaskType type;

    /** Creates a task with the supplied description and type. */
    public Task(String description, TaskType type) {
        this.description = description;
        this.isDone = false;
        this.type = type;
    }

    /** Returns {@code X} for a completed task or a blank marker otherwise. */
    public String getStatusIcon() {
        return (this.isDone ? "X" : " ");
    }

    /** Marks this task as completed. */
    public void markAsDone() {
        this.isDone = true;
    }

    /** Marks this task as incomplete. */
    public void markAsNotDone() {
        this.isDone = false;
    }

    /** Returns the task description. */
    public String getDescription() {
        return this.description;
    }

    /** Returns whether this task is completed. */
    public boolean isDone() {
        return this.isDone;
    }

    /** Returns this task's type. */
    public TaskType getType() {
        return this.type;
    }

    /** Returns this task in the format used by the data file. */
    public String toFileFormat() {
        return this.type.getSymbol() + " | " + (this.isDone ? "1" : "0") + " | " + this.description;
    }

    /** Returns the human-readable representation of this task. */
    @Override
    public String toString() {
        return "[" + this.type.getSymbol() + "][" + getStatusIcon() + "] " + this.description;
    }
}
