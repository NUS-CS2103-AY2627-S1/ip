package orbit.task;

/**
 * Identifies the supported task categories and their display icons.
 */
public enum TaskType {
    /** A task without an associated date. */
    TODO("[T]", "T"),

    /** A task that must be completed by a date. */
    DEADLINE("[D]", "D"),

    /** A task that occurs between a start and end date. */
    EVENT("[E]", "E");

    private final String icon;
    private final String storageCode;

    /**
     * Creates a task type with its display icon.
     *
     * @param icon icon used when displaying this task type
     * @param storageCode code used when saving this task type
     */
    TaskType(String icon, String storageCode) {
        this.icon = icon;
        this.storageCode = storageCode;
    }

    /**
     * Returns this task type's display icon.
     *
     * @return task type icon
     */
    public String getIcon() {
        return icon;
    }

    /**
     * Returns the code used to identify this task type in the storage file.
     *
     * @return task type storage code
     */
    public String getStorageCode() {
        return storageCode;
    }

    /**
     * Finds the task type represented by a storage code.
     *
     * @param storageCode code read from the storage file
     * @return matching task type
     * @throws IllegalArgumentException if the code does not represent a task type
     */
    public static TaskType fromStorageCode(String storageCode) {
        for (TaskType taskType : values()) {
            if (taskType.storageCode.equals(storageCode)) {
                return taskType;
            }
        }
        throw new IllegalArgumentException("Unknown task type code: " + storageCode);
    }
}
