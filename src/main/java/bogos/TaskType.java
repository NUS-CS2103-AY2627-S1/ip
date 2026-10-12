package bogos;

/**
 * Defines the supported task types and their data-file codes.
 */
public enum TaskType {
    TODO("T"),
    DEADLINE("D"),
    EVENT("E");

    private final String storageCode;

    TaskType(String storageCode) {
        this.storageCode = storageCode;
    }

    /**
     * Returns the code used to store this task type in the data file.
     *
     * @return Data-file code for this task type.
     */
    public String getStorageCode() {
        return storageCode;
    }

    /**
     * Returns the task type represented by a data-file code.
     *
     * @param storageCode Data-file code to convert.
     * @return Task type represented by the code.
     * @throws IllegalArgumentException If the code does not represent a supported task type.
     */
    public static TaskType fromStorageCode(String storageCode) {
        for (TaskType taskType : values()) {
            if (taskType.storageCode.equals(storageCode)) {
                return taskType;
            }
        }
        throw new IllegalArgumentException("Unknown task type.");
    }
}
