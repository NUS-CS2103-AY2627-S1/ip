package alice.task;

import java.util.Locale;

/**
 * Represents the supported intervals between recurring task occurrences.
 */
public enum Recurrence {
    DAILY("daily"),
    WEEKLY("weekly"),
    MONTHLY("monthly"),
    YEARLY("yearly");

    private final String displayName;

    Recurrence(String displayName) {
        this.displayName = displayName;
    }

    /** Returns the interval name used in commands and task displays. */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Returns the recurrence identified by the supplied interval name.
     *
     * @param intervalName The interval name from the user's command.
     * @return The matching recurrence, or {@code null} if the name is unsupported.
     */
    public static Recurrence fromIntervalName(String intervalName) {
        for (Recurrence recurrence : values()) {
            if (recurrence.displayName.equals(intervalName.toLowerCase(Locale.ROOT))) {
                return recurrence;
            }
        }
        return null;
    }
}
