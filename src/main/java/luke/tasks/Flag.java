package luke.tasks;

import java.util.Arrays;

/**
 * Supported flag names that can appear after a task description.
 */
public enum Flag {
    /** Deadline due time, written as {@code /by}. */
    BY("by"),

    /** Event start time, written as {@code /from}. */
    FROM("from"),

    /** Event end time, written as {@code /to}. */
    TO("to"),

    /** List sorting mode, written as {@code /sort}. */
    SORT("sort");

    private final String keyword;

    Flag(String keyword) {
        assert keyword != null && !keyword.isBlank() : "Flag keyword must be present.";
        this.keyword = keyword;
    }

    /**
     * Returns the flag matching a user-typed keyword such as {@code by}.
     *
     * @param word the flag keyword without the leading slash
     * @return the matching flag, or {@code null} if none matches
     */
    public static Flag findByKeyword(String word) {
        return Arrays.stream(values())
                .filter(flag -> word.equalsIgnoreCase(flag.keyword))
                .findFirst()
                .orElse(null);
    }
}
