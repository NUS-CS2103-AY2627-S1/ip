package adrian;

/**
 * Identifies the supported task categories and their display symbols.
 */
public enum TaskType {
    /** A task without an associated date or time. */
    TODO("T"),
    /** A task that must be completed by a specific date and time. */
    DEADLINE("D"),
    /** A task that takes place between a start and end date and time. */
    EVENT("E"),
    /** An unscheduled task that requires a fixed amount of time. */
    FIXED_DURATION("F");

    private final String symbol;

    /**
     * Creates a task category with the given display symbol.
     *
     * @param symbol symbol used to identify the category.
     */
    TaskType(String symbol) {
        this.symbol = symbol;
    }

    /**
     * Returns the single-character symbol for this task category.
     *
     * @return task category symbol.
     */
    @Override
    public String toString() {
        return symbol;
    }
}
