package yapbot.task;

import yapbot.exception.YapBotException;

/**
 * Represents how urgent a task is: {@link #HIGH}, {@link #MEDIUM}, or
 * {@link #LOW}. A task has no priority at all until one is explicitly set.
 */
public enum Priority {
    HIGH,
    MEDIUM,
    LOW;

    /**
     * Parses user-typed text into a {@link Priority}, matching the level's
     * name case-insensitively (e.g. "high", "High", and "HIGH" all match
     * {@link #HIGH}).
     *
     * @param input the raw text the user typed for the priority.
     * @return the matching priority.
     * @throws YapBotException if {@code input} does not match any level.
     */
    public static Priority fromInput(String input) throws YapBotException {
        for (Priority level : values()) {
            if (level.name().equalsIgnoreCase(input)) {
                return level;
            }
        }
        throw new YapBotException("'" + input + "' is not a valid priority. Use high, medium or low.");
    }
}
