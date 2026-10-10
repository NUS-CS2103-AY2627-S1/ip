package cassava.task;

/**
 * Represents a single task item.
 */
public abstract class Task {
    private boolean completed;
    private String description;
    private TaskPriority taskPriority;

    /**
     * Default constructor for Task.
     * @param description Text description of task.
     * @param completed Completion status.
     * @param taskPriority Priority of task.
     */
    public Task(String description, boolean completed, TaskPriority taskPriority) {
        this.completed = completed;
        this.description = description;
        this.taskPriority = taskPriority;
    }

    /**
     * Creates a Task with completed = false,
     * and taskPriority = UNKNOWN.
     * @param description Text description of task.
     */
    public Task(String description) {
        this(description, false, TaskPriority.UNKNOWN);
    }

    /**
     * Represents the priority level of a task.
     * Must be one of UNKNOWN, HIGH, MEDIUM or LOW.
     */
    public enum TaskPriority {
        UNKNOWN,
        HIGH,
        MEDIUM,
        LOW
    }

    /**
     * Converts TaskPriority to String.
     * @param taskPriority TaskPriority enum value.
     * @return String representing the TaskPriority enum value.
     */
    public static String taskPriorityToString(TaskPriority taskPriority) {
        return switch (taskPriority) {
            case HIGH -> "high";
            case MEDIUM -> "medium";
            case LOW -> "low";
            default -> "unknown";
        };
    }

    /**
     * Converts String to TaskPriority
     * @param taskPriorityString String representing the TaskPriority enum value.
     * @return TaskPriority enum value.
     */
    public static TaskPriority stringToTaskPriority(String taskPriorityString) {
        return switch (taskPriorityString) {
            case "high" -> TaskPriority.HIGH;
            case "medium" -> TaskPriority.MEDIUM;
            case "low" -> TaskPriority.LOW;
            default -> TaskPriority.UNKNOWN;
        };
    }

    public boolean equals(Task other) {
        return other.toDataString().equals(this.toDataString());
    }

    @Override
    public String toString() {
        return "[" + getTaskTypeToken() + "] "
                + "[" + (completed ? "X" : " ") + "] "
                + description
                + (this.taskPriority != TaskPriority.UNKNOWN
                ? " [" + taskPriorityToString(this.taskPriority) + "]"
                : "");
    }

    public abstract String getTaskTypeToken();

    /**
     * Converts task to String representation for data storage.
     * @return String representation for data storage.
     */
    public String toDataString() {
        return this.getTaskTypeToken() + " | "
                + (this.completed ? "1" : "0") + " | "
                + this.description + " | "
                + taskPriorityToString(this.taskPriority);
    }

    public String getDescription() {
        return this.description;
    }

    public void mark() {
        this.completed = true;
    }

    public void unmark() {
        this.completed = false;
    }

    public void setTaskPriority(TaskPriority taskPriority) {
        this.taskPriority = taskPriority;
    }
}
