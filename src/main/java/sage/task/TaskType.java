package sage.task;

/**
 * Represents the supported categories of tasks in the application.
 */
public enum TaskType {
    /**
     * A simple to-do item without a date or time.
     */
    TODO("T"),
    /**
     * A task that must be completed by a specific date or time.
     */
    DEADLINE("D"),
    /**
     * A task that spans a start and end time.
     */
    EVENT("E"),
    /**
     * A snippet of information to remember.
     */
    NOTE("N");

    private final String symbol;

    /**
     * Creates a task type with its persistence symbol.
     *
     * @param symbol the one-character code used when storing tasks on disk.
     */
    TaskType(String symbol) {
        this.symbol = symbol;
    }

    /**
     * Returns the short symbol used to identify this task type.
     *
     * @return the storage symbol for the task type.
     */
    public String getSymbol() {
        return symbol;
    }
}
