package mouse.task;

/**
 * The three kinds of tasks Mouse can store.
 */
public enum TaskType {
    TODO("T"),
    DEADLINE("D"),
    EVENT("E");

    private final String symbol;

    /**
     * Creates a task type with the given list symbol.
     *
     * @param symbol Single-letter type symbol.
     */
    TaskType(String symbol) {
        this.symbol = symbol;
    }

    /**
     * Returns the letter shown in the task list, for example {@code T} in {@code [T]}.
     *
     * @return Single-letter type symbol.
     */
    public String getSymbol() {
        return symbol;
    }
}
