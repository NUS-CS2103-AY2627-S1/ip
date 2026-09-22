package panda.task;

/**
 * Represents whether a task is unfinished or completed.
 */
public enum TaskStatus {
    /** An unfinished task. */
    NOT_DONE("0"),

    /** A completed task. */
    DONE("1");

    private final String storageValue;

    /**
     * Creates a task status with its serialized value.
     *
     * @param storageValue the value written to storage.
     */
    TaskStatus(String storageValue) {
        this.storageValue = storageValue;
    }

    /**
     * Returns the value used to store this status.
     *
     * @return the serialized status value.
     */
    public String getStorageValue() {
        return storageValue;
    }

    /**
     * Returns the task status represented by a storage value.
     *
     * @param storageValue the serialized value to interpret.
     * @return the matching task status.
     * @throws IllegalArgumentException if the value is not recognized.
     */
    public static TaskStatus fromStorageValue(String storageValue) {
        for (TaskStatus taskStatus : values()) {
            if (taskStatus.storageValue.equals(storageValue)) {
                return taskStatus;
            }
        }
        throw new IllegalArgumentException("Unknown task status value: " + storageValue);
    }
}
