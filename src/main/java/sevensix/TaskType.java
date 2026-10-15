package sevensix;

/**
 * Identifies the kinds of tasks supported by SevenSix and their display icons.
 */
public enum TaskType {
    /** A task without a date or time. */
    TODO("T"),
    /** A task that must be completed by a date or time. */
    DEADLINE("D"),
    /** A task that takes place between a start and end date or time. */
    EVENT("E");

    /** The one-letter icon written to the storage file and shown in the UI. */
    private final String icon;

    /**
     * Creates a task type with its one-letter display icon.
     *
     * @param icon the icon shown in the task list.
     */
    TaskType(String icon) {
        this.icon = icon;
    }

    /**
     * Returns the icon used for this task type.
     *
     * @return the one-letter task type icon.
     */
    public String getIcon() {
        return icon;
    }
}
