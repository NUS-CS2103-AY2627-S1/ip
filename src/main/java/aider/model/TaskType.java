package aider.model;

/**
 * The supported types of tasks in Aider.
 */
public enum TaskType {
    TODO("T"),
    DEADLINE("D"),
    EVENT("E");

    /** The one-letter icon used when displaying this task type. */
    private final String icon;

    /**
     * Creates a task type with its display icon.
     *
     * @param icon the one-letter display icon
     */
    TaskType(String icon) {
        this.icon = icon;
    }

    /**
     * Returns the display icon for this task type.
     *
     * @return the one-letter display icon
     */
    public String getIcon() {
        return icon;
    }
}
