package task;

/**
 * Represents whether a task is in progress or completed.
 */
public enum TaskStatus {
    IN_PROGRESS,
    DONE;

    /**
     * Returns the marker used to display this status to the user.
     *
     * @return display marker for this status
     */
    public String renderTaskStatus() {
        if (this == IN_PROGRESS) {
            return "[ ]";
        } else if (this == DONE) {
            return "[X]";
        } else {
            throw new RuntimeException("Unknown Task Status should not be possible");
        }
    }

    /**
     * Converts this status into its storage representation.
     *
     * @return serialized status value
     */
    public String serialize() {
        if (this == IN_PROGRESS) {
            return "P";
        } else if (this == DONE) {
            return "D";
        } else {
            throw new RuntimeException("Unknown Task Status should not be possible");
        }
    }

    /**
     * Converts a stored status value into its corresponding task status.
     *
     * @param taskStatusString serialized status value
     * @return task status represented by the value
     * @throws RuntimeException if the value does not represent a known status
     */
    public static TaskStatus deserialize(String taskStatusString) {
        if (taskStatusString.equals("P")) {
            return IN_PROGRESS;
        } else if (taskStatusString.equals("D")) {
            return DONE;
        } else {
            throw new RuntimeException("Unknown Task Status should not be possible");
        }
    }

}
