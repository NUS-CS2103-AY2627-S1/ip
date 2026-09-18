package bobby.task;

/**
 * Represents the supported task categories and their display symbols.
 */
public enum TaskType {
    TODO("T"),
    DEADLINE("D"),
    EVENT("E");

    private final String symbol;

    /**
     * Creates a task type with the symbol shown in task lists.
     *
     * @param symbol one-letter display symbol.
     */
    TaskType(String symbol) {
        this.symbol = symbol;
    }

    /**
     * Returns the symbol used when displaying a task of this type.
     *
     * @return one-letter display symbol.
     */
    public String getSymbol() {
        return symbol;
    }

    /**
     * Returns the task type represented by the given symbol.
     *
     * @param symbol one-letter task type symbol.
     * @return task type represented by the symbol.
     * @throws IllegalArgumentException if the symbol is unknown.
     */
    public static TaskType fromSymbol(String symbol) {
        for (TaskType taskType : values()) {
            if (taskType.symbol.equals(symbol)) {
                return taskType;
            }
        }
        throw new IllegalArgumentException("Unknown task type symbol: " + symbol);
    }
}
