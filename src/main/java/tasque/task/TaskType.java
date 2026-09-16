package tasque.task;

/**
 * Identifies the supported task kinds and their storage symbols.
 */
public enum TaskType {
    TODO("T"),
    DEADLINE("D"),
    EVENT("E");

    private final String symbol;

    /**
     * Creates a task type with its single-letter storage symbol.
     *
     * @param symbol Single-letter symbol used in storage records.
     */
    TaskType(String symbol) {
        this.symbol = symbol;
    }

    /**
     * Returns the single-letter symbol used when storing this type.
     *
     * @return Single-letter symbol used in storage records.
     */
    public String getSymbol() {
        return this.symbol;
    }
}
