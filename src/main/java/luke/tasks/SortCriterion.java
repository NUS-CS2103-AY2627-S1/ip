package luke.tasks;

import java.util.Arrays;

/**
 * Criteria supported by the {@code list /sort} command.
 */
public enum SortCriterion {
    /** Sort tasks alphabetically by their descriptions. */
    ALPHA("alpha"),

    /** Sort tasks chronologically by their first parseable date or time. */
    TIME("time");

    private final String keyword;

    SortCriterion(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Returns the criterion matching a user-typed keyword.
     *
     * @param word sort value entered after {@code /sort}
     * @return matching criterion, or {@code null} if none matches
     */
    public static SortCriterion findByKeyword(String word) {
        return Arrays.stream(values())
                .filter(criterion -> word.equalsIgnoreCase(criterion.keyword))
                .findFirst()
                .orElse(null);
    }
}
