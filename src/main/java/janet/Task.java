package janet;

/**
 * Represents a task with a completion status, type, and label.
 */
public abstract class Task {

    protected final String taskType;
    protected final String taskLabel;
    private final boolean isDone;

    /**
     * Creates a task with the specified completion status, type, and label.
     *
     * @throws JanetException If the task label is empty.
     */
    public Task(boolean isDone, String taskType, String taskLabel) throws JanetException {
        if (taskLabel.isEmpty()) {
            throw new JanetException("No task label provided!");
        }
        this.isDone = isDone;
        this.taskType = taskType;
        this.taskLabel = taskLabel;
    }

    /**
     * Returns a new <code>Task</code> the same as this <code>Task</code>but marked as completed.
     *
     * @throws JanetException If the new task could not be instantiated.
     */
    public abstract Task markDone() throws JanetException;

    public int compareTaskLabel(Task task) {
        return this.taskLabel.compareTo(task.taskLabel);
    }

    /**
     * Returns a string representation of the <code>Task</code> that is suitable for saving to a file.
     */
    public String toFileFormat() {
        return String.join(
                StorageTaskParser.INLINE_SEP,
                this.taskType,
                this.isDone ? "1" : "0",
                this.taskLabel
        );
    }

    // User not allowed to e.g. submit the same task with a different deadline. Expected to delete.
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Task t) {
            return this.taskType.equals(t.taskType)
                    && this.taskLabel.equals(t.taskLabel);
        }

        return false;
    }

    @Override
    public String toString() {
        return String.format("[%s][%s] %s", this.taskType, this.isDone ? "X" : " ", this.taskLabel);
    }
}
