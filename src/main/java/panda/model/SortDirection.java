package panda.model;

/**
 * Directions available when ordering deadlines by date/time.
 */
public enum SortDirection {
    ASCENDING("Ascending"),
    DESCENDING("Descending");

    private final String displayName;

    SortDirection(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns the direction opposite to this one.
     *
     * @return the opposite direction
     */
    public SortDirection getOpposite() {
        return this == ASCENDING ? DESCENDING : ASCENDING;
    }

    /**
     * Returns the text displayed on the GUI direction button.
     *
     * @return the direction display name
     */
    public String getDisplayName() {
        return displayName;
    }
}
