package max.data;

public enum Status {
    DONE("[X]"),
    NOT_DONE("[ ]");

    private final String s;

    Status(String symbol) {
        this.s = symbol;
    }

    @Override
    public String toString() {
        return s;
    }

    public static Status fromSymbol(String symbol) {
        for (Status s : Status.values()) {
            if (s.toString().equals(symbol)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Unknown status symbol"); 
    }
}
