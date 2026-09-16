package puyo.task;

/**
 * Represents the supported types of tasks in the application.
 */
public enum TaskType {
    TODO("T"),
    DEADLINE("D"),
    EVENT("E");

    private final String code;

    /**
     * Constructs a {@code TaskType} with the specified character code.
     *
     * @param code The single-character string code representing the task type.
     */
    TaskType(String code) {
        this.code = code;
    }

    /**
     * Returns the string code associated with the task type.
     *
     * @return The character code of the task type.
     */
    public String getCode() {
        return code;
    }
}
