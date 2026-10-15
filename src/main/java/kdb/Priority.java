package kdb;

import java.util.Locale;

/** Represents the priority assigned to a task. */
public enum Priority {
    HIGH,
    MEDIUM,
    LOW;

    /**
     * Converts user input into a priority.
     *
     * @param input priority name entered by the user
     * @return the matching priority, or {@code MEDIUM} for blank input
     * @throws IllegalArgumentException if the input is not a valid priority
     */
    public static Priority fromInput(String input) {
        if (input == null || input.isBlank()) {
            return MEDIUM;
        }

        try {
            return valueOf(input.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Priority must be high, medium, or low.", e);
        }
    }

    /** Returns the lowercase name shown to users. */
    @Override
    public String toString() {
        return name().toLowerCase(Locale.ROOT);
    }
}
