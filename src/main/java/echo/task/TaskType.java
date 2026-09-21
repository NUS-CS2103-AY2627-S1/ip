package echo.task;

/**
 * An enum that contains all allowed task types
 */
public enum TaskType {
    TODO("T"),
    DEADLINE("D"),
    EVENT("E");

    private final String symbol;

    TaskType(String typeSymbol) {
        this.symbol = typeSymbol;
    }

    /**
     * Creates a {@link TaskType} from a string-based symbol
     * @param symbol Symbol of the task type
     * @return A {@link TaskType} corresponding to the symbol
     */
    public static TaskType fromString(String symbol) {
        for (TaskType t : TaskType.values()) {
            if (t.toString().equals(symbol)) {
                return t;
            }
        }
        throw new IllegalArgumentException("Invalid task type: " + symbol);
    }

    /**
     * Retrieves the stored type symbol
     * @return type symbol of the task type
     */
    @Override
    public String toString() {
        return symbol;
    }
}
