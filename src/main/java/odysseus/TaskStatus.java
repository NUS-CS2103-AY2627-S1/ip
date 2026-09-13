package odysseus;

/**
 * The completion state of a task and its display marker.
 */
public enum TaskStatus {
    NOT_DONE("[ ]"),
    DONE("[X]");

    private final String marker;

    TaskStatus(String marker) {
        this.marker = marker;
    }

    /**
     * Returns the marker used when displaying this status.
     */
    public String getMarker() {
        return marker;
    }
}
