package lumine.task;

/**
 * Identifies each task type with a single-character symbol used in the display
 * and storage formats.
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
     * Returns the single-character symbol representing this task type (e.g. {@code "T"}).
     */
    public String getSymbol() {
        return symbol;
    }
}
