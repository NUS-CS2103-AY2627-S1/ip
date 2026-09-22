package nico;

import java.util.Locale;

/**
 * The three priorities that can be assigned to a task.
 */
public enum Priority {
    HIGH, MEDIUM, LOW;

    /**
     * Parses a priority entered by the user, ignoring letter case.
     *
     * @throws NicoException if the value is not high, medium, or low
     */
    public static Priority parse(String text) throws NicoException {
        if (text != null) {
            for (Priority priority : values()) {
                if (priority.toString().equalsIgnoreCase(text.trim())) {
                    return priority;
                }
            }
        }
        throw NicoException.invalidInput("Choose high, medium, or low lah.",
                "high, medium, or low");
    }

    @Override
    public String toString() {
        return name().toLowerCase(Locale.ROOT);
    }
}
