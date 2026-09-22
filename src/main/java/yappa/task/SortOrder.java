package yappa.task;

import yappa.exception.YappaException;

/** Identifies the direction in which tasks are sorted. */
public enum SortOrder {
    ASCENDING("asc"),
    DESCENDING("desc");

    private final String displayName;

    SortOrder(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Resolves a user-typed sort order into a {@code SortOrder}.
     *
     * @param input Sort order as typed by the user.
     * @return Matching sort order.
     * @throws YappaException If the input does not match a known sort order.
     */
    public static SortOrder fromInput(String input) throws YappaException {
        for (SortOrder order : values()) {
            if (order.displayName.equalsIgnoreCase(input.trim())) {
                return order;
            }
        }

        throw new YappaException(
                "Invalid sort order. Use 'asc' or 'desc'.");
    }

    @Override
    public String toString() {
        return displayName;
    }
}
