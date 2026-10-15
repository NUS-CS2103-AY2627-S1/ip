package niulai.model;

/**
 * Represents the completion state of a task.
 */
public enum TaskStatus {
    /** The task still needs to be completed. */
    PENDING(0),

    /** The task has been completed. */
    COMPLETED(1);

    /** The numeric value used to persist this status. */
    private final int storageValue;

    /** Creates a task status with its persistence value. */
    TaskStatus(int storageValue) {
        this.storageValue = storageValue;
    }

    /** @return the numeric value used to persist this status */
    public int getStorageValue() {
        return storageValue;
    }

    /**
     * Converts a persisted numeric value into a task status.
     *
     * @param storageValue the persisted status value
     * @return the corresponding task status
     * @throws IllegalArgumentException if the value does not represent a task status
     */
    public static TaskStatus fromStorageValue(int storageValue) {
        for (TaskStatus status : values()) {
            if (status.storageValue == storageValue) {
                return status;
            }
        }

        throw new IllegalArgumentException("Unknown task status value: " + storageValue);
    }
}
