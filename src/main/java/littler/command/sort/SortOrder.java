package littler.command.sort;

/**
 * Represents ascending or descending sort order, along with the input keyword used to select it.
 */
public enum SortOrder {
    ASCENDING("a"),
    DESCENDING("d");

    private final String keyword;

    /**
     * Constructs a SortOrder with the specified keyword.
     *
     * @param keyword the input keyword associated with this sort order
     */
    SortOrder(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Returns the input keyword associated with this sort order.
     *
     * @return the keyword string
     */
    public String getKeyword() {
        return keyword;
    }

    /**
     * Matches a user-typed keyword to its corresponding SortOrder.
     *
     * @param keyword the raw keyword typed by the user
     * @return the matching SortOrder, or null if no order matches
     */
    public static SortOrder fromKeyword(String keyword) {
        for (SortOrder order : values()) {
            if (order.keyword.equalsIgnoreCase(keyword)) {
                return order;
            }
        }
        return null;
    }
}
