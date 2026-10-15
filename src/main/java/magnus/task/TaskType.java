package magnus.task;

/**
 * Identifies each supported task type and its persistent-storage code.
 */
public enum TaskType {
    /** A task without an associated date or time. */
    TODO("T"),
    /** A task with a completion deadline. */
    DEADLINE("D"),
    /** A task occurring between start and end times. */
    EVENT("E");

    private final String storageCode;

    /**
     * Creates a task type with its persistent-storage code.
     *
     * @param storageCode The code used in serialized task data.
     */
    TaskType(String storageCode) {
        this.storageCode = storageCode;
    }

    /**
     * Returns the code used to identify this type in serialized task data.
     *
     * @return This task type's storage code.
     */
    public String getStorageCode() {
        return this.storageCode;
    }

    /**
     * Returns the task type identified by a persistent-storage code.
     *
     * @param storageCode The storage code to parse.
     * @return The matching task type.
     * @throws IllegalArgumentException If the code does not identify a supported task type.
     */
    public static TaskType parseStorageCode(String storageCode) {
        for (TaskType taskType : values()) {
            if (taskType.storageCode.equals(storageCode)) {
                return taskType;
            }
        }
        throw new IllegalArgumentException("unknown task type '" + storageCode + "'");
    }
}
