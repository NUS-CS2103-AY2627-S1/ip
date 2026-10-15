package swell.task;

/**
 * Represents the supported task types in Swell.
 */
public enum TaskType {
    /** Todo task. */
    TODO("T"),

    /** Deadline task. */
    DEADLINE("D"),

    /** Event task. */
    EVENT("E");

    private final String symbol;

    /**
     * Creates a task type with the display symbol used in task lists.
     *
     * @param symbol display symbol.
     */
    TaskType(String symbol) {
        this.symbol = symbol;
    }

    /**
     * Returns the display symbol for this task type.
     *
     * @return display symbol.
     */
    public String getSymbol() {
        return symbol;
    }
}
