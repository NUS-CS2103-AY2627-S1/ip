package jeryl.task;

import jeryl.exception.JerylException;

/**
 * How urgently a task needs attention. NONE means no priority has been
 * set, and is the default for a task created without a "/priority" flag.
 */
public enum Priority {
    NONE, LOW, MEDIUM, HIGH;

    /**
     * Parses a priority keyword as typed by the user (case-insensitive,
     * one of "low"/"medium"/"high") into a Priority.
     */
    public static Priority fromKeyword(String keyword) throws JerylException {
        if (keyword.equalsIgnoreCase("high") || keyword.equalsIgnoreCase("medium") || keyword.equalsIgnoreCase("low")) {
            return Priority.valueOf(keyword.toUpperCase());
        }
        throw new JerylException("Invalid priority \"" + keyword + "\". Use high, medium, or low.");
    }

    /**
     * Returns the short tag shown before a task's type tag when listed,
     * e.g. "[H]", or an empty string when priority is NONE.
     */
    public String getTag() {
        return switch (this) {
            case HIGH -> "[H]";
            case MEDIUM -> "[M]";
            case LOW -> "[L]";
            case NONE -> "";
        };
    }
}
