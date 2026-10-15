package bot67.task;

/**
 * Represents the supported task types and their display symbols.
 */
public enum TaskType {
    TODO("T"),
    DEADLINE("D"),
    EVENT("E");

    private final String symbol;

    /**
     * Creates a task type with its display symbol.
     *
     * @param symbol the symbol shown in the task description
     */
    TaskType(String symbol) {
        this.symbol = symbol;
    }

    /**
     * Returns the display symbol for this task type.
     *
     * @return the display symbol
     */
    public String getSymbol() {
        return symbol;
    }
}
