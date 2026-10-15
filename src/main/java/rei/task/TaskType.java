package rei.task;

/**
 * Identifies the kind of task stored by Rei and its display code.
 */
public enum TaskType {
    TODO("T"),
    DEADLINE("D"),
    EVENT("E");

    private final String displayCode;

    /**
     * Creates a task type with the specified command-line display code.
     *
     * @param displayCode Short code shown beside tasks of this type.
     */
    TaskType(String displayCode) {
        this.displayCode = displayCode;
    }

    /** Returns the short code used in the command-line task list. */
    public String getDisplayCode() {
        return displayCode;
    }
}
