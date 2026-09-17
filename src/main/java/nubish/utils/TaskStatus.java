package nubish.utils;

/**
 * Represents completion status icons used for tasks.
 */
public enum TaskStatus {
    NOT_DONE(" "),
    DONE("X");

    private final String icon;

    TaskStatus(String icon) {
        this.icon = icon;
    }

    /**
     * Returns the icon displayed for this status.
     *
     * @return status icon
     */
    public String getIcon() {
        return icon;
    }
}
