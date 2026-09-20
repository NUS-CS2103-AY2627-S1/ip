package recordbase.types;

import java.util.Arrays;
import java.util.Locale;

import recordbase.exceptions.RecordException;

/**
 * Represents a task's urgency, where a smaller numeric level is more urgent.
 */
public enum Priority {
    /** Highest urgency, represented by level 1. */
    HIGH("High", 1),
    /** Above-medium urgency, represented by level 2. */
    MEDIUM_HIGH("Medium-High", 2),
    /** Default urgency, represented by level 3. */
    MEDIUM("Medium", 3),
    /** Below-medium urgency, represented by level 4. */
    LOW_MEDIUM("Low-Medium", 4),
    /** Lowest urgency, represented by level 5. */
    LOW("Low", 5);

    private final String displayName;
    private final int level;

    Priority(String displayName, int level) {
        this.displayName = displayName;
        this.level = level;
    }

    /**
     * Parses either a priority name or its numeric level, ignoring letter case.
     *
     * @param value priority name or level
     * @return the matching priority
     * @throws RecordException if the value does not represent a supported priority
     */
    public static Priority fromString(String value) {
        String normalizedValue = value.strip().toLowerCase(Locale.ROOT);
        return Arrays.stream(values())
                .filter(priority -> priority.matches(normalizedValue))
                .findFirst()
                .orElseThrow(() -> new RecordException(
                        "Priority must be High (1), Medium-High (2), Medium (3), "
                        + "Low-Medium (4), or Low (5)."));
    }

    private boolean matches(String value) {
        return displayName.toLowerCase(Locale.ROOT).equals(value)
                || Integer.toString(level).equals(value);
    }

    /**
     * Returns the numeric level, where 1 is highest and 5 is lowest.
     *
     * @return numeric priority level
     */
    public int getLevel() {
        return level;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
