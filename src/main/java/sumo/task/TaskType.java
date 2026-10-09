package sumo.task;

import java.util.Arrays;

/** Represents the finite set of task types supported by Sumo. */
public enum TaskType {
    /** Represents a task without a scheduled date. */
    TODO("T", 3),
    /** Represents a task with a due date. */
    DEADLINE("D", 4),
    /** Represents a task with a start and end date. */
    EVENT("E", 5);

    private final String storageCode;
    private final int storedFieldCount;

    TaskType(String storageCode, int storedFieldCount) {
        this.storageCode = storageCode;
        this.storedFieldCount = storedFieldCount;
    }

    /**
     * Returns the single-character code used in the task data file.
     *
     * @return the stored task type code.
     */
    public String getStorageCode() {
        return storageCode;
    }

    /**
     * Returns the number of fields required for this task type in storage.
     *
     * @return the number of stored fields.
     */
    public int getStoredFieldCount() {
        return storedFieldCount;
    }

    /**
     * Returns the task type represented by a stored type code.
     *
     * @param storageCode code read from the task data file.
     * @return matching task type.
     * @throws IllegalArgumentException if the code is not recognized.
     */
    public static TaskType fromStorageCode(String storageCode) {
        return Arrays.stream(values())
                .filter(taskType -> taskType.storageCode.equals(storageCode))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Unknown task type in data file: " + storageCode));
    }
}
