package bingus.task;

/**
 * Identifies the supported task categories and their display symbols.
 */
public enum TaskType {
    TODO("T"),
    DEADLINE("D"),
    EVENT("E");

    private final String symbol;

    /**
     * Creates a task type with its display symbol.
     *
     * @param symbol symbol displayed for this type.
     */
    TaskType(String symbol) {
        this.symbol = symbol;
    }

    /**
     * Returns the symbol used when displaying this task type.
     *
     * @return task-type display symbol.
     */
    public String getSymbol() {
        return symbol;
    }

    /**
     * Returns the task type represented by the specified symbol.
     *
     * @param symbol task-type symbol.
     * @return corresponding task type.
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
