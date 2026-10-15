package stephen.task;

/**
 * Represents the fixed types of tasks supported by the chatbot.
 */
public enum TaskType {
    /** Task without an attached date. */
    TODO("T"),
    /** Task due on a particular date. */
    DEADLINE("D"),
    /** Task occurring over a date range. */
    EVENT("E");

    private final String symbol;

    /**
     * Creates a task type with the symbol used in task displays.
     *
     * @param symbol short symbol shown before a task
     */
    TaskType(String symbol) {
        this.symbol = symbol;
    }

    /**
     * Returns the symbol used to display this task type.
     *
     * @return task type symbol
     */
    public String getSymbol() {
        return symbol;
    }
}
