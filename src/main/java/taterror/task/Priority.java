package taterror.task;

/**
 * How urgently a task should be treated. {@link #NONE} is the default for a
 * task that was never given a priority.
 */
public enum Priority {
    NONE, LOW, MEDIUM, HIGH;

    /**
     * Parses a user-supplied priority level, case-insensitively.
     *
     * @param text e.g. {@code "high"}, {@code "HIGH"}, {@code "none"}
     * @return the matching {@link Priority}, or {@code null} if {@code text}
     *         doesn't match any level
     */
    public static Priority fromString(String text) {
        for (Priority priority : values()) {
            if (priority.name().equalsIgnoreCase(text)) {
                return priority;
            }
        }
        return null;
    }
}
