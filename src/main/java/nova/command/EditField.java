package nova.command;

/**
 * Identifies a task detail that can be changed by an edit command.
 */
public enum EditField {
    /** Task description. */
    DESCRIPTION("/description"),
    /** Deadline due date or time. */
    BY("/by"),
    /** Event start date or time. */
    FROM("/from"),
    /** Event end date or time. */
    TO("/to");

    private final String marker;

    /**
     * Creates an edit field with its command marker.
     *
     * @param marker marker used to select the field.
     */
    EditField(String marker) {
        this.marker = marker;
    }

    /**
     * Returns the marker used in an edit command.
     *
     * @return field marker.
     */
    public String getMarker() {
        return marker;
    }
}
