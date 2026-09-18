package sozius.task;

/**
 * Task abstract class provides common methods for all Task classes
 */
public abstract class Task {
    private boolean isDone;
    private final String description;

    protected Task(String description) {
        this(false, description);
    }
    protected Task(boolean done, String description) {
        assert description != null;

        this.isDone = done;
        this.description = description;
    }
    /**
     * Returns whether the task is done
     * @return the boolean
     */
    public boolean isDone() {
        return isDone;
    }
    /**
     * Sets isDone to the new value
     * @param done the new value
     */
    public void setDone(boolean done) {
        this.isDone = done;
    }
    /**
     * Returns the task description
     * @return the description
     */
    public String getDescription() {
        return description;
    }
    /**
     * Returns string representation of the task.
     * @return The task string in file format
     */
    public abstract String toFileString();
    /**
     * Returns string representation of the task.
     * @return The task string in user format
     */
    public String toUserString() {
        return String.format("[%c] %s",
                isDone ? 'X' : ' ',
                description);
    }
}
