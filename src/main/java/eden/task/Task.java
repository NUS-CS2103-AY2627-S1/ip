package eden.task;

/**
 * Represents a task stored by Eden.
 */
public abstract class Task {
    private String description;
    private boolean isMarked;

    /**
     * Creates an unmarked task with the given description.
     *
     * @param description task description.
     */
    public Task(String description) {
        this.description = description;
        isMarked = false;
    }

    /**
     * Marks this task as completed.
     */
    public void mark() {
        isMarked = true;
    }

    /**
     * Marks this task as not completed.
     */
    public void unmark() {
        isMarked = false;
    }

    /**
     * Returns whether this task has been completed.
     *
     * @return true if this task is marked as completed.
     */
    public boolean isMarked() {
        return isMarked;
    }

    /**
     * Returns this task's description.
     *
     * @return the task description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Converts this task to the line format used in Eden's data file.
     *
     * @return this task in Eden's persistent text format.
     */
    public abstract String toDataString();

    /**
     * Escapes backslashes and pipe characters within one stored data field.
     * Backslashes are escaped first so loading can distinguish a literal
     * backslash from the escape marker before a pipe.
     *
     * @param field task text to store.
     * @return the safely escaped field.
     */
    protected String escapeDataField(String field) {
        return field.replace("\\", "\\\\").replace("|", "\\|");
    }

    /**
     * Returns the task in the format shown to the user.
     *
     * @return the display form of this task.
     */
    @Override
    public String toString() {
        if (isMarked) {
            return "[X] " + description;
        }
        return "[ ] " + description;
    }
}
