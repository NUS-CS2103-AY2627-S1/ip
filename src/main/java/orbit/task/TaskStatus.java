package orbit.task;

/**
 * Identifies whether a task is completed and provides its display icon.
 */
public enum TaskStatus {
    /** Indicates that a task has not been completed. */
    NOT_DONE("[ ]", "0"),

    /** Indicates that a task has been completed. */
    DONE("[X]", "1");

    private final String icon;
    private final String storageValue;

    /**
     * Creates a task status with its display icon.
     *
     * @param icon icon used when displaying this status
     * @param storageValue value used when saving this status
     */
    TaskStatus(String icon, String storageValue) {
        this.icon = icon;
        this.storageValue = storageValue;
    }

    /**
     * Returns this status's display icon.
     *
     * @return task status icon
     */
    public String getIcon() {
        return icon;
    }

    /**
     * Returns the value used to represent this status in the storage file.
     *
     * @return task status storage value
     */
    public String getStorageValue() {
        return storageValue;
    }

    /**
     * Finds the task status represented by a storage value.
     *
     * @param storageValue value read from the storage file
     * @return matching task status
     * @throws IllegalArgumentException if the value does not represent a task status
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
