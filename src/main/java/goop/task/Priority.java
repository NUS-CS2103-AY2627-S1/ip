package goop.task;

import java.util.Locale;

/**
 * Represents an optional task priority, with level 1 being the most urgent.
 */
public enum Priority {
    NONE,
    HIGH,
    MEDIUM,
    LOW;

    /**
     * Returns the lowercase label used in task displays and saved records.
     *
     * @return Priority label.
     */
    public String getLabel() {
        return name().toLowerCase(Locale.ROOT);
    }
}
