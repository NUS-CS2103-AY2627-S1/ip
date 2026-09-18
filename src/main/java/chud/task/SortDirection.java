package chud.task;

/** The direction to sort in, for the {@code list /sort <key> [asc|desc]} option. */
public enum SortDirection {
    ASC,
    DESC;

    /**
     * Matches a typed direction word (exact, lowercase, e.g. "asc") to its SortDirection, or
     * null if it doesn't match either recognized direction.
     */
    public static SortDirection fromArgument(String text) {
        for (SortDirection direction : values()) {
            if (direction.name().toLowerCase().equals(text)) {
                return direction;
            }
        }
        return null;
    }
}
