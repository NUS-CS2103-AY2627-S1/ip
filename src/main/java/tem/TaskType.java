package tem;

/**
 * Represents the fixed task categories Tem knows how to store.
 */
public enum TaskType {
    TODO("T"),
    DEADLINE("D"),
    EVENT("E");

    private final String displaySymbol;

    /**
     * Creates a task type with the given display symbol.
     *
     * @param displaySymbol letter shown when displaying tasks of this type.
     */
    TaskType(String displaySymbol) {
        this.displaySymbol = displaySymbol;
    }

    /**
     * Returns the letter used when displaying a task of this type.
     *
     * @return task type symbol shown to the user
     */
    public String getDisplaySymbol() {
        return displaySymbol;
    }
}
