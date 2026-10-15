package bubba;

/**
 * Stores the description, completion status and type shared by all tasks.
 */
public class Task {
    /** Description supplied by the user. */
    protected String description;
    /** Whether the task has been completed. */
    protected boolean isDone;
    /** Task category used in display and storage records. */
    protected TaskType type;

    /**
     * Creates an incomplete task of the specified type.
     *
     * @param description Description of the task.
     * @param type Category of the task.
     */
    public Task(String description, TaskType type) {
        this.description = description;
        this.isDone = false;
        this.type = type;
    }

    /**
     * Marks this task as completed.
     */
    public void done() {
        isDone = true;
    }

    /**
     * Marks this task as incomplete.
     */
    public void undoDone() {
        isDone = false;
    }

    /**
     * Returns a pipe-separated record containing the type, completion flag and description.
     * Subclasses append any additional task details.
     *
     * @return Record for saving this task to a file.
     */
    public String toStorageString() {
        return type.getSymbol()
                + " | "
                + (isDone ? "1" : "0")
                + " | "
                + description;
    }

    /**
     * Returns the task type, completion indicator and description for display.
     *
     * @return Human-readable task summary.
     */
    @Override
    public String toString() {
        String status = isDone ? "X" : " ";
        return "[" + type.getSymbol() + "][" + status + "] " + description;
    }
}
