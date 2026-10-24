package kiki.task;

/**
 * Represents the category of a {@link Task}, and the single-letter icon
 * used when printing it (e.g. "[T]" for a todo).
 */
public enum TaskType {
    TODO("T"),
    DEADLINE("D"),
    EVENT("E");

    private final String symbol;

    TaskType(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }
}
