package nimbus.task;

/**
 * Represents the different types of tasks in the Nimbus application.
 */
public enum TaskType {
    TODO("T"), DEADLINE("D"), EVENT("E");

    public final String type;

    /**
     * Constructs a TaskType with its file/display marker.
     *
     * @param type The single-letter marker representing this task type.
     */
    TaskType(String type) {
        this.type = type;
    }

    /**
     * Returns the marker associated with this task type.
     *
     * @return The single-letter marker (e.g. {@code "T"} for TODO).
     */
    public String getType() {
        return type;
    }

    /**
     * Returns the TaskType whose marker matches the given string.
     *
     * @param type The marker to look up (e.g. {@code "D"}).
     * @return The matching TaskType.
     * @throws IllegalArgumentException If no TaskType has that marker.
     */
    public static TaskType fromString(String type) {
        for (TaskType taskType : TaskType.values()) {
            if (taskType.type.equals(type)) {
                return taskType;
            }
        }
        throw new IllegalArgumentException("Invalid task type: " + type);
    }
}
