package bot67.task;

/**
 * Represents a task stored by Bot67.
 */
public class Task {
    private final String name;
    private final TaskType type;
    private boolean isDone;

    /**
     * Creates an incomplete task of the specified type.
     *
     * @param name the task description
     * @param type the task type
     */
    public Task(String name, TaskType type) {
        this.name = name;
        this.type = type;
        this.isDone = false;
    }

    /**
     * Returns the completion status icon for this task.
     *
     * @return {@code X} if completed, or a blank space otherwise
     */
    public String getStatusIcon() {
        return (this.isDone ? "X" : " "); // mark done task with X
    }

    /** Marks this task as completed. */
    public void mark() {
        this.isDone = true;
    }

    /** Marks this task as incomplete. */
    public void unmark() {
        this.isDone = false;
    }

    /**
     * Returns the task description without its type or status markers.
     *
     * @return the task name
     */
    public String getName() {
        return this.name;
    }

    /** Returns this task's type. */
    public TaskType getType() {
        return this.type;
    }

    /** Returns the task in the format used by the save file. */
    public String toFileFormat() {
        return this.type.getSymbol() + " | " + (this.isDone ? "1" : "0") + " | " + this.name;
    }

    /**
     * Returns the standard display description for this task.
     *
     * @return the formatted task description
     */
    public String getDescription() {
        return "[" + this.getType().getSymbol() + "][" + this.getStatusIcon() + "] "
                + this.getName();
    }
}
