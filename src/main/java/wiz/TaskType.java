package wiz;

/**
 * Represents the types of tasks supported by Wiz.
 */
public enum TaskType {
    TODO("T"),
    DEADLINE("D"),
    EVENT("E");

    private final String symbol;

    TaskType(String symbol) {
        this.symbol = symbol;
    }

    /**
     * Returns the single character identifier for the task type.
     *
     * @return The task type symbol.
     */
    public String getSymbol() {
        return symbol;
    }
}