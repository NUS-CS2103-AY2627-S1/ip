package rudra.task;

/**
 * Identifies the supported task variants and their display prefixes.
 */
public enum TaskType {
    TODO("T"),
    DEADLINE("D"),
    EVENT("E");

    private final String displayCode;

    /**
     * Creates a task type with the prefix used in the task list.
     *
     * @param displayCode Single-letter code shown before each task.
     */
    TaskType(String displayCode) {
        this.displayCode = displayCode;
    }

    /**
     * Returns the single-letter code shown in the UI for this task type.
     *
     * @return Task type display code.
     */
    public String getDisplayCode() {
        return this.displayCode;
    }
}
