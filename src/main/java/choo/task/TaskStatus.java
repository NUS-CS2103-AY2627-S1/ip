package choo.task;

/**
 * Represents whether a task is complete and supplies its display symbol.
 */
public enum TaskStatus {
    /** Indicates that a task is completed. */
    DONE("X"),
    /** Indicates that a task is incomplete. */
    NOT_DONE(" ");

    private final String icon;

    TaskStatus(String icon) {
        this.icon = icon;
    }

    /**
     * Returns the symbol used to display this status.
     *
     * @return status symbol.
     */
    public String getIcon() {
        return this.icon;
    }
}
