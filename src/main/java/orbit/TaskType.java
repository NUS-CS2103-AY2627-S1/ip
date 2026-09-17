package orbit;

/**
 * Types of tasks supported by Orbit and their display symbols.
 */
public enum TaskType {
    TODO("T"),
    DEADLINE("D"),
    EVENT("E");

    private final String symbol;

    TaskType(String symbol) {
        this.symbol = symbol;
    }

    /** @return Single-letter symbol displayed for this task type. */
    public String getSymbol() {
        return symbol;
    }
}
