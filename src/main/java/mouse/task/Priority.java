package mouse.task;

import mouse.MouseException;

/**
 * Urgency of a task. Default is {@link #NONE}.
 */
public enum Priority {
    HIGH,
    LOW,
    NONE;

    /**
     * Parses {@code high}, {@code low}, or {@code none}, ignoring case.
     *
     * @param raw User-supplied priority word.
     * @return Matching priority.
     * @throws MouseException If {@code raw} is not a known priority.
     */
    public static Priority fromString(String raw) throws MouseException {
        String normalised = raw.trim().toLowerCase();
        switch (normalised) {
        case "high":
            return HIGH;
        case "low":
            return LOW;
        case "none":
            return NONE;
        default:
            throw new MouseException("Priority must be high, low, or none. Mouse is picky GRR");
        }
    }

    /**
     * Returns the lowercase label shown in replies.
     *
     * @return Display word for this priority.
     */
    public String toDisplayString() {
        return name().toLowerCase();
    }
}
