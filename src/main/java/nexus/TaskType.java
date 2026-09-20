package nexus;

/**
 * The supported categories of task.
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
     * Returns the one-letter display symbol for this task type.
     *
     * @return the task type symbol
     */
    public String getSymbol() {
        return symbol;
    }
}
