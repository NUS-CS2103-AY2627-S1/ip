package baron.core.task;

/**
 * Represents the supported task kinds and their persistent and display codes.
 */
public enum TaskType {
    TODO("T"),
    DEADLINE("D"),
    EVENT("E");

    private final String fileCode;

    TaskType(String fileCode) {
        this.fileCode = fileCode;
    }

    /**
     * Returns the code used to store this task kind in the task file.
     *
     * @return The persistent task-kind code.
     */
    public String getFileCode() {
        return fileCode;
    }

    /**
     * Returns the task kind represented by the specified persistent code.
     *
     * @param fileCode The code read from the task file.
     * @return The corresponding task kind.
     * @throws IllegalArgumentException If the code does not represent a supported task kind.
     */
    public static TaskType fromFileCode(String fileCode) {
        return switch (fileCode) {
            case "T" -> TODO;
            case "D" -> DEADLINE;
            case "E" -> EVENT;
            default -> throw new IllegalArgumentException("Unknown task type");
        };
    }
}
