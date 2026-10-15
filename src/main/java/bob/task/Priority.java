package bob.task;

import java.util.Locale;

/**
 * Represents an optional task priority; NONE leaves the task untagged.
 */
public enum Priority {
    NONE, LOW, MODERATE, HIGH;

    /**
     * Parses a priority label using the same lowercase spelling as command fields.
     *
     * @param label priority label
     * @return matching priority
     * @throws IllegalArgumentException if the label is not supported
     */
    public static Priority fromLabel(String label) {
        for (Priority priority : values()) {
            if (priority.toString().equals(label)) {
                return priority;
            }
        }
        throw new IllegalArgumentException("Unknown priority: " + label);
    }

    @Override
    public String toString() {
        return name().toLowerCase(Locale.ROOT);
    }
}
