package alfred.task;

import alfred.AlfredException;

/**
 * Ranks how much a task matters, with {@link #NONE} for one the user has said
 * nothing about.
 */
public enum Priority {
    HIGH,
    MEDIUM,
    LOW,
    NONE;

    /**
     * Returns the level a word names, whatever its capitalization.
     *
     * @param text the level as the user typed it or the save file holds it.
     * @return the level that word names.
     * @throws AlfredException if the text names no level.
     */
    public static Priority parse(String text) throws AlfredException {
        try {
            return valueOf(text.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new AlfredException("I know high, medium, low, and none as priorities, sir.");
        }
    }

    /**
     * Returns the box this level is displayed in, such as {@code [HIGH]}.
     *
     * @return the box, or an empty string for a task with no priority.
     */
    public String box() {
        return this == NONE ? "" : "[" + name() + "]";
    }
}
