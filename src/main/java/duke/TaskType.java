package duke;

/**
 * The supported kinds of tasks in Furina.
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
     * Returns the short symbol used when displaying a task.
     *
     * @return T, D, or E
     */
    public String getSymbol() {
        return symbol;
    }
}
