package floppy;

/**
 * Identifies the supported task categories and their display symbols.
 */
public enum TaskType {
    TODO("T"),
    DEADLINE("D"),
    EVENT("E"),
    PERIOD("P");

    private final String symbol;

    /**
     * Creates a task type with the symbol used in task listings.
     *
     * @param symbol Short display symbol for the task type.
     */
    TaskType(String symbol) {
        this.symbol = symbol;
    }

    /**
     * Returns the short symbol used to display this task type.
     *
     * @return Display symbol for this task type.
     */
    public String getSymbol() {
        return symbol;
    }
}
