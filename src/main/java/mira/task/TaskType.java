package mira.task;

/**
 * Types of tasks that Mira can track and their display symbols.
 */
public enum TaskType {
    /** A task without scheduling information. */
    TODO("T"),
    /** A task with a due date or time. */
    DEADLINE("D"),
    /** A task with start and end times. */
    EVENT("E");

    private final String symbol;

    TaskType(String symbol) {
        this.symbol = symbol;
    }

    /**
     * Returns the one-letter symbol displayed beside a task.
     *
     * @return the display symbol for this task type.
     */
    public String getSymbol() {
        return symbol;
    }
}
