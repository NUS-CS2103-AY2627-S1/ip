package martin.task;

/** Represents the task types supported by Martin and their storage codes. */
public enum TaskType {
    TODO("T"),
    DEADLINE("D"),
    EVENT("E");

    private final String storageCode;

    /**
     * Constructs a task type with the specified storage code.
     *
     * @param storageCode The code used to represent this type in storage.
     */
    TaskType(String storageCode) {
        this.storageCode = storageCode;
    }

    /**
     * Returns the code used to represent this task type in storage.
     *
     * @return The storage code for this task type.
     */
    public String getStorageCode() {
        return this.storageCode;
    }

    /**
     * Returns the task type represented by a storage code.
     *
     * @param storageCode The code read from the storage file.
     * @return The corresponding task type.
     * @throws IllegalArgumentException If the storage code is unknown.
     */
    public static TaskType fromStorageCode(String storageCode) {
        for (TaskType taskType : values()) {
            if (taskType.storageCode.equals(storageCode)) {
                return taskType;
            }
        }
        throw new IllegalArgumentException("Unknown task type in storage file: " + storageCode);
    }
}
