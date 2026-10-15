package et.task;

/**
 * Represents the supported kinds of tasks and their list-display codes.
 */
public enum TaskType {
    TODO("T"),
    DEADLINE("D"),
    EVENT("E");

    /** The short code displayed before tasks of this type. */
    private final String displayCode;

    /**
     * Creates a task type with its display code.
     *
     * @param displayCode the code used in the task list
     */
    TaskType(String displayCode) {
        this.displayCode = displayCode;
    }

    /**
     * Returns the task type represented by a display code.
     *
     * @param displayCode the code to interpret
     * @return the task type with the supplied display code
     * @throws IllegalArgumentException if the display code is unknown
     */
    public static TaskType fromDisplayCode(String displayCode) {
        for (TaskType taskType : values()) {
            if (taskType.displayCode.equals(displayCode)) {
                return taskType;
            }
        }
        throw new IllegalArgumentException("Unknown task type display code");
    }

    /**
     * Returns the code used to display this task type.
     *
     * @return the task type display code
     */
    public String getDisplayCode() {
        return displayCode;
    }
}
