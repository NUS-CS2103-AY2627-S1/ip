package hermes.task;

/**
 * The kinds of task Hermes stores, each paired with the letter that identifies
 * it in the data file and the number of fields its stored line holds.
 *
 * <p>Keeping the letter beside the field count describes the storage format in
 * one place. Before this, the letter appeared in the code that writes a line
 * and again, twice, in the code that reads one back, so adding a kind of task
 * meant finding every one of those places.
 */
public enum TaskType {
    TODO("T", 3),
    DEADLINE("D", 4),
    EVENT("E", 5);

    private final String symbol;
    private final int storedFieldCount;

    TaskType(String symbol, int storedFieldCount) {
        this.symbol = symbol;
        this.storedFieldCount = storedFieldCount;
    }

    /**
     * Returns the kind of task stored under a given letter.
     *
     * @param symbol the first field of a stored line.
     * @return the matching kind, or null if no kind uses that letter.
     */
    public static TaskType of(String symbol) {
        for (TaskType type : values()) {
            if (type.symbol.equals(symbol)) {
                return type;
            }
        }

        return null;
    }

    public String getSymbol() {
        return this.symbol;
    }

    public int getStoredFieldCount() {
        return this.storedFieldCount;
    }
}
