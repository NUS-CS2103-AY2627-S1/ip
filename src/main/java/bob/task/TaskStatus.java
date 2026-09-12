package bob.task;

/**
 * Represents the completion state of a task.
 */
public enum TaskStatus {
    /** Task has been completed. */
    DONE("X", "1"),
    /** Task has not been completed. */
    NOT_DONE(" ", "0");

    private final String icon;
    private final String storageCode;

    /**
     * Creates a status with its display icon and persistent code.
     *
     * @param icon symbol shown to the user.
     * @param storageCode value written to storage.
     */
    TaskStatus(String icon, String storageCode) {
        this.icon = icon;
        this.storageCode = storageCode;
    }

    /**
     * Returns the symbol used when displaying this status.
     *
     * @return display icon.
     */
    public String getIcon() {
        return icon;
    }

    /**
     * Returns the value used to persist this status.
     *
     * @return storage code.
     */
    public String getStorageCode() {
        return storageCode;
    }

    /**
     * Finds the status represented by a storage code.
     *
     * @param code storage code to look up.
     * @return matching task status.
     * @throws IllegalArgumentException if the code is unknown.
     */
    public static TaskStatus fromStorageCode(String code) {
        for (TaskStatus status : values()) {
            if (status.storageCode.equals(code)) {
                return status;
            }
        }

        throw new IllegalArgumentException("Unknown task status code: " + code);
    }
}
