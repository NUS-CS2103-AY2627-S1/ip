package carl.task;

/**
 * Represents the different types of tasks available in the application.
 */
public enum TaskType {

    /**
     * Represents a Todo task.
     */
    TODO("T"),

    /**
     * Represents a Deadline task.
     */
    DEADLINE("D"),

    /**
     * Represents an Event task.
     */
    EVENT("E");


    private final String prefix;

    /**
     * Constructs a TaskType with the specified string prefix.
     *
     * @param prefix The string prefix representing the task type.
     */
    TaskType(String prefix) {
        this.prefix = prefix;
    }

    /**
     * Returns the string representation of the task type for UI display or saving.
     *
     * @return The prefix representing the type (e.g., "T", "D", or "E").
     */
    @Override
    public String toString() {
        return this.prefix;
    }

    /**
     * Converts a string prefix into its corresponding TaskType enum.
     *
     * @param prefix The string prefix to convert.
     * @return The corresponding TaskType (TODO, DEADLINE, or EVENT).
     * @throws IllegalArgumentException If the prefix does not match any valid task type.
     */
    public static TaskType parsePrefix(String prefix) {
        return switch (prefix) {
            case "T" -> TODO;
            case "D" -> DEADLINE;
            case "E" -> EVENT;
            case null, default -> throw new IllegalArgumentException("Invalid task prefix");
        };
    }
}
