package aravii;

/**
 * Identifies the supported task categories and their display symbols.
 */
enum TaskType {
    TODO("T"),
    DEADLINE("D"),
    EVENT("E");

    private final String symbol;

    TaskType(String symbol) {
        this.symbol = symbol;
    }

    String getSymbol() {
        return symbol;
    }
}
