package atlas.task;

import java.util.Locale;

/**
 * The priority levels a task can carry. The word is what the user types and
 * what is written to the data file, so the words must stay stable even if the
 * constants are renamed.
 */
public enum Priority {
    /** Work that should be done before anything else. */
    HIGH("high"),
    /** Work with a normal level of urgency. */
    MEDIUM("medium"),
    /** Work that can wait. */
    LOW("low");

    /** The text the user types to select this level. */
    private final String word;

    /**
     * Creates a priority represented by the given word.
     *
     * @param word text that selects this level.
     */
    Priority(String word) {
        this.word = word;
    }

    /**
     * Returns the text that selects this level.
     *
     * @return the level word.
     */
    public String getWord() {
        return word;
    }

    /**
     * Returns the tag shown beside a ranked task, e.g. {@code HIGH}.
     *
     * @return the level word in upper case.
     */
    public String getTag() {
        return word.toUpperCase(Locale.ROOT);
    }

    /**
     * Returns the level named by a word, or null when no level uses that word.
     * The word {@code none} is not a level: it is handled by the parser as the
     * instruction to remove a task's priority.
     *
     * @param word text to look up.
     * @return the matching level, or {@code null} when the word names none.
     */
    public static Priority fromWord(String word) {
        for (Priority priority : values()) {
            if (priority.word.equals(word)) {
                return priority;
            }
        }
        return null;
    }
}
