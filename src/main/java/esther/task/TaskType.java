package esther.task;

/**
 * Represents the supported types of tasks.
 */
public enum TaskType {
    /** A task without a deadline or event time. */
    TODO("T"),

    /** A task with a due date. */
    DEADLINE("D"),

    /** A task occurring during a time range. */
    EVENT("E");

    private final String symbol;

    TaskType(String symbol) {
        this.symbol = symbol;
    }

    /**
     * Returns the symbol used to display this task type.
     *
     * @return Display symbol of this task type.
     */
    public String getSymbol() {
        return symbol;
    }
}
