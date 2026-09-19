package carl.task;

/**
 * Represents the completion status of a task.
 */
public enum TaskStatus {
    /**
     * Indicates that the task has not been completed.
     */
    NOT_DONE("[ ]"),

    /**
     * Indicates that the task has been completed.
     */
    DONE("[X]");

    private final String prefix;

    /**
     * Constructs a TaskStatus with the specified string representation.
     *
     * @param prefix The string prefix representing the status.
     */
    TaskStatus(String prefix) {
        this.prefix = prefix;
    }

    /**
     * Returns the string representation of the task status for UI display.
     *
     * @return The prefix representing the status (e.g., "[ ]" or "[X]").
     */
    @Override
    public String toString() {
        return prefix;
    }

    /**
     * Converts the task status into an integer format, typically used for saving to storage.
     *
     * @return 1 if the task is done, 0 if it is not done.
     */
    public int toInt() {
        return switch (this) {
            case DONE -> 1;
            case NOT_DONE -> 0;
        };
    }
}
