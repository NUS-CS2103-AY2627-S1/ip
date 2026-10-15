package bubba;

/**
 * Defines task categories and their symbols in display and storage records.
 */
public enum TaskType {
    /** Task without a date or time. */
    TODO("T"),
    /** Task with a due date and time. */
    DEADLINE("D"),
    /** Task with start and end dates and times. */
    EVENT("E");

    private final String symbol;

    TaskType(String symbol) {
        this.symbol = symbol;
    }

    /**
     * Returns the symbol identifying this task category.
     *
     * @return Single-letter task symbol.
     */
    public String getSymbol() {
        return symbol;
    }
}
