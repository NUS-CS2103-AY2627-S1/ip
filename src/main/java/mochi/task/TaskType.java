package mochi.task;

/**
 * Represents the type of a task: a todo, a deadline, or an event.
 */
public enum TaskType {
    TODO('T'),
    DEADLINE('D'),
    EVENT('E');

    private final char symbol;

    TaskType(char symbol) {
        this.symbol = symbol;
    }

    /**
     * Returns the single-character symbol used in file storage.
     *
     * @return the symbol for this task type
     */
    public char getSymbol() {
        return symbol;
    }
}
