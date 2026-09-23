package max.data;

public enum TaskType {
    TODO("[T]"), 
    DEADLINE("[D]"), 
    EVENT("[E]");

    private final String s;

    TaskType(String s) {
        this.s = s;
    }

    @Override
    public String toString() {
        return this.s;
    }

    public static TaskType fromSymbol(String symbol) {
        for (TaskType t : TaskType.values()) {
            if (t.toString().equals(symbol)) {
                return t;
            }
        }
        throw new IllegalArgumentException("Unknown task symbol : " + symbol);
    }
}
