package martin.task;

import java.util.Locale;

import martin.exception.IllegalCommandException;

/** Represents the priority levels that can be assigned to a task. */
public enum Priority {
    HIGH("H"),
    MEDIUM("M"),
    LOW("L");

    private final String storageCode;

    /**
     * Constructs a priority with the specified storage code.
     *
     * @param storageCode The code used to represent this priority in storage.
     */
    Priority(String storageCode) {
        this.storageCode = storageCode;
    }

    /**
     * Returns the code used to represent this priority in storage.
     *
     * @return The storage code for this priority.
     */
    public String getStorageCode() {
        return this.storageCode;
    }

    /**
     * Creates a priority from a user-provided priority name.
     *
     * @param priorityName The priority name entered by the user.
     * @return The corresponding priority.
     * @throws IllegalCommandException If the name is not high, medium, or low.
     */
    public static Priority fromUserInput(String priorityName) {
        try {
            return valueOf(priorityName.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new IllegalCommandException("Priority must be high, medium, or low.");
        }
    }

    /**
     * Returns the priority represented by fields from the storage file. Old storage
     * records with no priority field are treated as low priority.
     *
     * @param taskType The type of task represented by the storage fields.
     * @param parts    The fields parsed from the storage file.
     * @return The corresponding priority.
     * @throws IllegalArgumentException If the storage format is invalid.
     */
    public static Priority getPriorityFromStorage(TaskType taskType, String[] parts) {
        int basePartCount = switch (taskType) {
            case TODO -> 3;
            case DEADLINE -> 4;
            case EVENT -> 5;
        };
        if (parts.length == basePartCount) {
            return Priority.LOW;
        }
        if (parts.length == basePartCount + 1) {
            for (Priority priority : values()) {
                if (priority.storageCode.equals(parts[basePartCount])) {
                    return priority;
                }
            }
        }
        throw new IllegalArgumentException("Invalid task format in storage file.");
    }
}
