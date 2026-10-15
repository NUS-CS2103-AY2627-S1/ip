package pulbot.task;

/** Identifies the supported kinds of tasks. */
public enum TaskType {
    TODO("T"),
    DEADLINE("D"),
    EVENT("E");

    private final String symbol;

    TaskType(String symbol) {
        this.symbol = symbol;
    }

    /** Returns the one-letter symbol used to display and store this type. */
    public String getSymbol() {
        return symbol;
    }
}
