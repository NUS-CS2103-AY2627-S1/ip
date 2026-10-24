package kiki.task;

/**
 * Priority levels that can be assigned to tasks.
 */
public enum Priority {
    LOW("L"),
    NORMAL("N"),
    HIGH("H");

    private final String symbol;

    Priority(String symbol) {
        this.symbol = symbol;
    }

    /**
     * Returns the compact display symbol for this priority.
     *
     * @return Priority symbol.
     */
    public String getSymbol() {
        return symbol;
    }
}
