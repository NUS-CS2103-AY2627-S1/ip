package littler.command.sort;

/**
 * Represents the criteria by which tasks can be sorted, along with the input keyword used to select it.
 */
public enum SortCriteria {
    DATE("date"),
    NAME("name"),
    PRIORITY("priority"),
    TAG("tag");

    private final String keyword;

    /**
     * Constructs a SortCriteria with the specified keyword.
     *
     * @param keyword the input keyword associated with this sort criteria
     */
    SortCriteria(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Returns the input keyword associated with this sort criteria.
     *
     * @return the keyword string
     */
    public String getKeyword() {
        return keyword;
    }

    /**
     * Matches a user-typed keyword to its corresponding SortCriteria.
     *
     * @param keyword the raw keyword typed by the user
     * @return the matching SortCriteria, or null if no criteria matches
     */
    public static SortCriteria fromKeyword(String keyword) {
        for (SortCriteria criteria : values()) {
            if (criteria.keyword.equalsIgnoreCase(keyword)) {
                return criteria;
            }
        }
        return null;
    }
}
