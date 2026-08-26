package ultron.model;

/**
 * The supported categories of tasks and their display symbols.
 */
public enum TaskType {
    TODO("T"),
    DEADLINE("D"),
    EVENT("E");

    private final String symbol;

    /**
     * Creates a task type with the symbol used in task-list output.
     *
     * @param symbol the one-letter task-type symbol
     */
    TaskType(String symbol) {
        this.symbol = symbol;
    }

    /**
     * Returns the symbol displayed for this task type.
     *
     * @return the task-type symbol
     */
    public String getSymbol() {
        return symbol;
    }
}
