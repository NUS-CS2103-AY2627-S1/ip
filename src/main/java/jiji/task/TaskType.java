package jiji.task;

/**
 * Represents the different types of tasks supported by Jiji along with their storage type codes.
 */
public enum TaskType {
    /** Todo task type. */
    TODO("T"),
    /** Deadline task type. */
    DEADLINE("D"),
    /** Event task type. */
    EVENT("E");

    private final String code;

    /**
     * Constructs a TaskType with the specified single-letter storage code.
     *
     * @param code The storage identifier code (e.g. "T", "D", "E").
     */
    TaskType(String code) {
        this.code = code;
    }

    /**
     * Returns the single-letter code used for file persistence.
     *
     * @return The storage code.
     */
    public String getCode() {
        return code;
    }

    /**
     * Resolves a TaskType from its single-letter storage code.
     *
     * @param code The single-letter code read from storage.
     * @return The corresponding {@link TaskType}, or null if unrecognized.
     */
    public static TaskType fromCode(String code) {
        if (code == null) {
            return null;
        }
        for (TaskType type : values()) {
            if (type.code.equalsIgnoreCase(code.trim())) {
                return type;
            }
        }
        return null;
    }
}
