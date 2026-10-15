package zen.task;

/** Represents the supported task priorities in display order. */
public enum Priority {
    HIGH("high"),
    MEDIUM("medium"),
    LOW("low"),
    NONE("none");

    private final String displayValue;

    Priority(String displayValue) {
        this.displayValue = displayValue;
    }

    /** Returns the lowercase value used in task displays and storage. */
    public String getDisplayValue() {
        return displayValue;
    }

    /**
     * Returns a priority parsed from case-insensitive command input.
     *
     * @param value priority value entered by the user
     * @return the matching assignable priority
     * @throws IllegalArgumentException if {@code value} is not high, medium, or low
     */
    public static Priority fromCommandValue(String value) {
        return switch (value.toLowerCase()) {
            case "high" -> HIGH;
            case "medium" -> MEDIUM;
            case "low" -> LOW;
            default -> throw new IllegalArgumentException("Unsupported priority: " + value);
        };
    }

    /**
     * Returns a priority parsed from its normalized storage value.
     *
     * @param value priority value read from storage
     * @return the matching stored priority
     * @throws IllegalArgumentException if {@code value} is not a normalized stored value
     */
    public static Priority fromStorageValue(String value) {
        return switch (value) {
            case "high" -> HIGH;
            case "medium" -> MEDIUM;
            case "low" -> LOW;
            case "none" -> NONE;
            default -> throw new IllegalArgumentException("Unsupported stored priority: " + value);
        };
    }
}
