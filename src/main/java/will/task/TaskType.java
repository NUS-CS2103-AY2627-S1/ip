package will.task;

/**
 * The kind of a {@link Task}, along with the single-letter tag used to
 * display it (e.g. "[T]" for a Todo).
 */
public enum TaskType {
    TODO("T"),
    DEADLINE("D"),
    EVENT("E");

    private final String symbol;

    TaskType(String symbol) {
        this.symbol = symbol;
    }

    /** @return The single-letter tag used to display this type, e.g. "T". */
    public String getSymbol() {
        return symbol;
    }
}
