package bob.task;

/**
 * Identifies the kind of task stored by the chatbot.
 */
public enum TaskType {
    /** Task without an associated date or time. */
    TODO("T", 3),
    /** Task with a due date or time. */
    DEADLINE("D", 4),
    /** Task with a start and end date or time. */
    EVENT("E", 5);

    private final String symbol;
    private final int storageFieldCount;

    /**
     * Creates a task type with its display symbol and storage shape.
     *
     * @param symbol symbol used to display and persist the type.
     * @param storageFieldCount number of fields in a stored record of this type.
     */
    TaskType(String symbol, int storageFieldCount) {
        this.symbol = symbol;
        this.storageFieldCount = storageFieldCount;
    }

    /**
     * Returns the symbol used when displaying this task type.
     *
     * @return task type symbol.
     */
    public String getSymbol() {
        return symbol;
    }

    /**
     * Returns the expected number of fields in this type's storage record.
     *
     * @return storage field count.
     */
    public int getStorageFieldCount() {
        return storageFieldCount;
    }

    /**
     * Finds the task type represented by a display or storage symbol.
     *
     * @param symbol symbol to look up.
     * @return matching task type.
     * @throws IllegalArgumentException if the symbol is unknown.
     */
    public static TaskType fromSymbol(String symbol) {
        for (TaskType type : values()) {
            if (type.symbol.equals(symbol)) {
                return type;
            }
        }

        throw new IllegalArgumentException("Unknown task type symbol: " + symbol);
    }
}
