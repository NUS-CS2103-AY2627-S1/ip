package quackers;

/**
 * Identifies the supported kinds of tasks and their display symbols.
 */
public enum TaskType {
    TODO("T"),
    NOTE("N"),
    DEADLINE("D"),
    EVENT("E");

    private final String symbol;

    /**
     * Creates a task type with the symbol shown in the user interface.
     *
     * @param symbol the task type's display symbol.
     */
    TaskType(String symbol) {
        this.symbol = symbol;
    }

    /**
     * Returns the symbol used to display this task type.
     *
     * @return the task type's display symbol
     */
    public String getSymbol() {
        return symbol;
    }
}
