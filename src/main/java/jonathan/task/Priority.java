package jonathan.task;

import java.util.Locale;

/** Represents the priority assigned to a task. */
public enum Priority {
    NONE(0, "none"),
    HIGH(1, "high"),
    MEDIUM(2, "medium"),
    LOW(3, "low");
    private final int level;
    private final String label;

    Priority(int level, String label) {
        this.level = level;
        this.label = label;
    }

    /**
     * Returns the numeric priority level.
     *
     * @return the priority level
     */
    public int getLevel() {
        return level;
    }

    /**
     * Returns the display label.
     *
     * @return the priority label
     */
    public String getLabel() {
        return label;
    }


    /**
     * Converts a user-entered priority into a priority value.
     *
     * @param input priority name or numeric level
     * @return the corresponding priority
     */
    public static Priority fromInput(String input) {
        String normalized = input.toLowerCase(Locale.ROOT);

        return switch (normalized) {
            case "0", "none" -> NONE;
            case "1", "high" -> HIGH;
            case "2", "medium" -> MEDIUM;
            case "3", "low" -> LOW;
            default -> throw new IllegalArgumentException("Invalid priority");
        };
    }


}
