package prism.task;

/** Identifies the supported task kinds and their file-format symbols. */
public enum TaskType {
    TODO("T"),
    DEADLINE("D"),
    EVENT("E");

    private final String symbol;

    /** Associates a task type with its serialized symbol. */
    TaskType(String symbol) {
        this.symbol = symbol;
    }

    /** Returns the one-letter symbol used in task serialization. */
    public String getSymbol() {
        return this.symbol;
    }
}
