package alfred.task;

/**
 * Defines the kinds of tasks Alfred can store and their list display codes.
 */
public enum TaskType {
    TODO("T"),
    DEADLINE("D"),
    EVENT("E");

    private final String displayCode;

    /**
     * Creates a task type with its one-letter display code.
     *
     * @param displayCode code displayed before a task's completion status
     */
    TaskType(String displayCode) {
        this.displayCode = displayCode;
    }

    /**
     * Returns the code displayed before tasks of this type.
     *
     * @return the task type display code
     */
    public String getDisplayCode() {
        return displayCode;
    }
}
