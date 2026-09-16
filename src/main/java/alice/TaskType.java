package alice;

/**
 * Enumerates the different types of tasks supported by the application.
 * Provides mappings between task instances, command keywords, and type icons.
 */
public enum TaskType {
    TODO("todo", "[T]"),
    DEADLINE("deadline", "[D]"),
    EVENT("event", "[E]");

    private final String command;
    private final String icon;

    TaskType(String command, String icon) {
        this.command = command;
        this.icon = icon;
    }

    /**
     * Returns the command keyword associated with this task type.
     *
     * @return The command keyword (e.g., "todo").
     */
    public String getCommand() {
        return command;
    }

    /**
     * Returns the icon string associated with this task type.
     *
     * @return The type icon (e.g., "[T]").
     */
    public String getIcon() {
        return icon;
    }

    /**
     * Determines the TaskType of a given Task object.
     *
     * @param task The task instance.
     * @return The corresponding TaskType enum.
     * @throws IllegalArgumentException if the task type is unknown.
     */
    public static TaskType fromTask(Task task) {
        if (task instanceof ToDos) {
            return TODO;
        } else if (task instanceof Deadlines) {
            return DEADLINE;
        } else if (task instanceof Events) {
            return EVENT;
        } else {
            throw new IllegalArgumentException("Unknown task type");
        }
    }
}
